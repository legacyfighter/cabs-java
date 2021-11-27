package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static io.legacyfighter.cabs.distance.Distance.ofKm;

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

    private Instant date;

    @OneToOne
    private Address from;

    @OneToOne
    public Address to;

    public Integer pickupAddressChangeCounter = 0;

    @ManyToOne
    public Driver driver;

    public Instant acceptedAt;

    public Instant started;

    @ManyToMany
    public Set<Driver> driversRejections = new HashSet<>();

    @ManyToMany
    public Set<Driver> proposedDrivers = new HashSet<>();

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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="driversFee")),
    })
    private Money driversFee;

    private Instant dateTime;

    private Instant published;

    private Instant completeAt;

    @OneToOne
    public Client client;

    public CarType.CarClass getCarType() {
        return carType;
    }

    @Enumerated(EnumType.STRING)
    private CarType.CarClass carType;

    public Transit() {
    }

    public Transit(Address from, Address to, Client client, CarType.CarClass carClass, Instant when, Distance distance) {
        this(Status.DRAFT, from, to, client, carClass, when, distance);
    }

    public Transit(Status status, Address from, Address to, Client client, CarType.CarClass carClass, Instant when, Distance distance) {
        this.from = from;
        this.to = to;
        this.client = client;
        this.carType = carClass;
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
        this.from = newAddress;
        this.pickupAddressChangeCounter = pickupAddressChangeCounter + 1;
        this.km = newDistance.toKmInFloat();
        this.estimateCost();
    }

    public void changeDestinationTo(Address newAddress, Distance newDistance) {
        if (status.equals(Transit.Status.COMPLETED)) {
            throw new IllegalStateException("Address 'to' cannot be changed, id = " + getId());
        }

        this.to = newAddress;
        this.km = newDistance.toKmInFloat();
        estimateCost();
    }

    public void cancel() {
        if (!EnumSet.of(Transit.Status.DRAFT, Transit.Status.WAITING_FOR_DRIVER_ASSIGNMENT, Transit.Status.TRANSIT_TO_PASSENGER).contains(this.getStatus())) {
            throw new IllegalStateException("Transit cannot be cancelled, id = " + getId());
        }
        this.status = Status.CANCELLED;
        this.driver = null;
        this.km = Distance.ZERO.toKmInFloat();
        this.awaitingDriversResponses = 0;

    }

    public boolean canProposeTo(Driver driver) {
        return !this.driversRejections
                .contains(driver);
    }

    public void proposeTo(Driver driver) {
        if (canProposeTo(driver)) {
            this.proposedDrivers.add(driver);
            this.awaitingDriversResponses++;
        }
    }

    public void failDriverAssignment() {
        this.status = Status.DRIVER_ASSIGNMENT_FAILED;
        this.driver = null;
        this.km = Distance.ZERO.toKmInFloat();
        this.awaitingDriversResponses = 0;
    }

    public boolean shouldNotWaitForDriverAnyMore(Instant date) {
        return (status.equals(Transit.Status.CANCELLED) || published.plus(300, ChronoUnit.SECONDS).isBefore(date));
    }

    public void acceptBy(Driver driver, Instant when) {
        if (this.driver != null) {
            throw new IllegalStateException("Transit already accepted, id = " + getId());
        } else {
            if (!proposedDrivers.contains(driver)) {
                throw new IllegalStateException("Driver out of possible drivers, id = " + getId());
            } else {
                if (driversRejections.contains(driver)) {
                    throw new IllegalStateException("Driver out of possible drivers, id = " + getId());
                }
            }
            this.driver = driver;
            this.driver.setOccupied(true);
            this.awaitingDriversResponses = 0;
            this.acceptedAt = when;
            this.status = Status.TRANSIT_TO_PASSENGER;
        }
    }

    public void start(Instant when) {
        if (!status.equals(Transit.Status.TRANSIT_TO_PASSENGER)) {
            throw new IllegalStateException("Transit cannot be started, id = " + getId());
        }
        this.started = when;
        this.status = Status.IN_TRANSIT;
    }

    public void rejectBy(Driver driver) {
        driversRejections.add(driver);
        awaitingDriversResponses--;
    }

    public void publishAt(Instant when) {
        this.status = Transit.Status.WAITING_FOR_DRIVER_ASSIGNMENT;
        this.published = when;
    }

    public void completeAt(Instant when, Address destinationAddress, Distance distance) {
        if (status.equals(Status.IN_TRANSIT)) {
            this.km = distance.toKmInFloat();
            this.estimateCost();
            this.completeAt = when;
            this.to = destinationAddress;
            this.status = Status.COMPLETED;
            this.calculateFinalCosts();
        } else {
            throw new IllegalArgumentException("Cannot complete Transit, id = " + getId());
        }
    }

    public Driver getDriver() {
        return driver;
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

    public Instant getCompleteAt() {
        return completeAt;
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

    public Client getClient() {
        return client;
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
        this.dateTime = dateTime;
    }

    public Instant getDateTime() {
        return dateTime;
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

    public Set<Driver> getProposedDrivers() {
        return proposedDrivers;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getStarted() {
        return started;
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
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

    public Money getDriversFee() {
        return driversFee;
    }

    public void setDriversFee(Money driversFee) {
        this.driversFee = driversFee;
    }

    public Money getEstimatedPrice() {
        return estimatedPrice;
    }

    public Tariff getTariff() {
        return tariff;
    }

}
