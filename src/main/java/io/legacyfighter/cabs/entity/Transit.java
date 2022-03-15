package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.common.JsonToCollectionMapper;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;

import static io.legacyfighter.cabs.geolocation.Distance.ofKm;

@Entity
public class Transit extends BaseEntity {

    public enum Status {
        DRAFT,
        CANCELLED,
        WAITING_FOR_DRIVER_ASSIGNMENT,
        DRIVER_ASSIGNMENT_FAILED,
        TRANSIT_TO_PASSENGER,
        IN_TRANSIT,
        COMPLETED
    }

    public enum DriverPaymentStatus {
        NOT_PAID, PAID, CLAIMED, RETURNED;
    }

    public enum ClientPaymentStatus {
        NOT_PAID, PAID, RETURNED;
    }

    private DriverPaymentStatus driverPaymentStatus;

    private ClientPaymentStatus clientPaymentStatus;

    private Client.PaymentType paymentType;

    private Status status;

    public Integer pickupAddressChangeCounter = 0;

    public Long driverId;

    public String driversRejections;

    public String proposedDrivers;

    public Integer awaitingDriversResponses = 0;

    @Embedded
    private Tariff tariff;

    private float km;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="price")),
    })
    private Money price;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="estimatedPrice")),
    })
    private Money estimatedPrice;

    private Instant published;

    public Transit() {
    }

    public Transit(Long id) {
        this.id = id;
    }

    public Transit(Instant when, Distance distance) {
        this(Status.DRAFT, when, distance);
    }

    public Transit(Status status, Instant when, Distance distance) {
        setDateTime(when);
        this.km = distance.toKmInFloat();
        this.status = status;
    }
    public void changePickupTo(Address newAddress, Distance newDistance, double distanceFromPreviousPickup) {
        if (distanceFromPreviousPickup > 0.25) {
            throw new IllegalStateException("Address 'from' cannot be changed, id = " + getId());
        }
        if (!this.status.equals(Transit.Status.DRAFT) &&
                !(this.status.equals(Transit.Status.WAITING_FOR_DRIVER_ASSIGNMENT))) {
            throw new IllegalStateException("Address 'from' cannot be changed, id = " + getId());
        } else if (pickupAddressChangeCounter > 2) {
            throw new IllegalStateException("Address 'from' cannot be changed, id = " + getId());
        }
        this.pickupAddressChangeCounter = pickupAddressChangeCounter + 1;
        this.km = newDistance.toKmInFloat();
        this.estimateCost();
    }

    public void changeDestinationTo(Address newAddress, Distance newDistance) {
        if (status.equals(Transit.Status.COMPLETED)) {
            throw new IllegalStateException("Address 'to' cannot be changed, id = " + getId());
        }

        this.km = newDistance.toKmInFloat();
        estimateCost();
    }

    public void cancel() {
        if (!EnumSet.of(Transit.Status.DRAFT, Transit.Status.WAITING_FOR_DRIVER_ASSIGNMENT, Transit.Status.TRANSIT_TO_PASSENGER).contains(this.getStatus())) {
            throw new IllegalStateException("Transit cannot be cancelled, id = " + getId());
        }
        this.status = Status.CANCELLED;
        this.driverId = null;
        this.km = Distance.ZERO.toKmInFloat();
        this.awaitingDriversResponses = 0;

    }

    public boolean canProposeTo(Long driverId) {
        return !getProposedDrivers()
                .contains(driverId);
    }

    public void proposeTo(Long driverId) {
        if (canProposeTo(driverId)) {
            addDriverToProposed(driverId);
            this.awaitingDriversResponses++;
        }
    }

    private void addDriverToProposed(Long driverId) {
        Set<Long> proposedDriversSet = getProposedDrivers();
        proposedDriversSet.add(driverId);
        proposedDrivers = JsonToCollectionMapper.serialize(proposedDriversSet);
    }

    public void failDriverAssignment() {
        this.status = Status.DRIVER_ASSIGNMENT_FAILED;
        this.driverId = null;
        this.km = Distance.ZERO.toKmInFloat();
        this.awaitingDriversResponses = 0;
    }

    public boolean shouldNotWaitForDriverAnyMore(Instant date) {
        return (status.equals(Transit.Status.CANCELLED) || published.plus(300, ChronoUnit.SECONDS).isBefore(date));
    }

    public void acceptBy(Long driverId, Instant when) {
        if (this.driverId != null) {
            throw new IllegalStateException("Transit already accepted, id = " + getId());
        } else {
            if (!getProposedDrivers().contains(driverId)) {
                throw new IllegalStateException("Driver out of possible drivers, id = " + getId());
            } else {
                if (getDriverRejections().contains(driverId)) {
                    throw new IllegalStateException("Driver out of possible drivers, id = " + getId());
                }
            }
            this.driverId = driverId;
            this.awaitingDriversResponses = 0;
            this.status = Status.TRANSIT_TO_PASSENGER;
        }
    }

    public void start(Instant when) {
        if (!status.equals(Transit.Status.TRANSIT_TO_PASSENGER)) {
            throw new IllegalStateException("Transit cannot be started, id = " + getId());
        }
        this.status = Status.IN_TRANSIT;
    }

    public void rejectBy(Long driverId) {
        addToDriverRejections(driverId);
        awaitingDriversResponses--;
    }

    private void addToDriverRejections(Long driverId) {
        Set<Long> driverRejectionSet = getDriverRejections();
        driverRejectionSet.add(driverId);
        driversRejections = JsonToCollectionMapper.serialize(driverRejectionSet);
    }

    public void publishAt(Instant when) {
        this.status = Transit.Status.WAITING_FOR_DRIVER_ASSIGNMENT;
        this.published = when;
    }

    public void completeAt(Instant when, Address destinationAddress, Distance distance) {
        if (status.equals(Status.IN_TRANSIT)) {
            this.km = distance.toKmInFloat();
            this.estimateCost();
            this.status = Status.COMPLETED;
            this.calculateFinalCosts();
        } else {
            throw new IllegalArgumentException("Cannot complete Transit, id = " + getId());
        }
    }

    public Long getDriverId() {
        return driverId;
    }

    public Money getPrice() {
        return price;
    }

    //just for testing
    public void setPrice(Money price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public Money estimateCost() {
        if (status.equals(Status.COMPLETED)) {
            throw new IllegalStateException("Estimating cost for completed transit is forbidden, id = " + this.getId());
        }

        Money estimated = calculateCost();

        this.estimatedPrice = estimated;
        this.price = null;

        return estimatedPrice;
    }

    public Money calculateFinalCosts() {
        if (status.equals(Status.COMPLETED)) {
            return calculateCost();
        } else {
            throw new IllegalStateException("Cannot calculate final cost if the transit is not completed");
        }
    }

    private Money calculateCost() {
        Money money = this.tariff.calculateCost(ofKm(km));
        this.price = money;
        return money;
    }

    public void setDateTime(Instant dateTime) {
        this.tariff = Tariff.ofTime(dateTime.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }


    public Instant getPublished() {
        return published;
    }

    public Distance getKm() {
        return Distance.ofKm(km);
    }

    public Integer getAwaitingDriversResponses() {
        return awaitingDriversResponses;
    }

    public Set<Long> getDriverRejections() {
        return JsonToCollectionMapper.deserialize(driversRejections);
    }

    public Set<Long> getProposedDrivers() {
        return JsonToCollectionMapper.deserialize(proposedDrivers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Transit))
            return false;

        Transit other = (Transit) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }


    public Money getEstimatedPrice() {
        return estimatedPrice;
    }

    public Tariff getTariff() {
        return tariff;
    }

}
