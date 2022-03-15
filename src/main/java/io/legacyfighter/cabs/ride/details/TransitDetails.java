package io.legacyfighter.cabs.ride.details;

import io.legacyfighter.cabs.assignment.AssignmentStatus;
import io.legacyfighter.cabs.assignment.InvolvedDriversSummary;
import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariff;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
class TransitDetails extends BaseEntity {

    private Long transitId;

    private UUID requestUUID;

    private Instant dateTime;

    private Instant completeAt;

    @OneToOne(fetch = FetchType.EAGER)
    private Client client;

    @Enumerated(EnumType.STRING)
    private CarClass carType;

    @OneToOne(fetch = FetchType.EAGER)
    private Address from;

    @OneToOne(fetch = FetchType.EAGER)
    private Address to;

    private Distance distance;

    private Instant started;

    private Instant acceptedAt;

    private Instant publishedAt;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "price")),
    })
    private Money price;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "estimatedPrice")),
    })
    private Money estimatedPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "driversFee")),
    })
    private Money driversFee;

    private Long driverId;

    private Status status;

    private Tariff tariff;

    private TransitDetails() {
    }

    TransitDetails(Instant dateTime, UUID requestUUID, Address from, Address to, Distance distance, Client client, CarClass carClass, Money estimatedPrice, Tariff tariff) {
        this.requestUUID = requestUUID;
        this.dateTime = dateTime;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.client = client;
        this.carType = carClass;
        this.status = Status.DRAFT;
        this.estimatedPrice = estimatedPrice;
        this.tariff = tariff;
    }

    UUID getRequestUUID() {
        return requestUUID;
    }

    Instant getDateTime() {
        return dateTime;
    }

    Instant getCompleteAt() {
        return completeAt;
    }

    Client getClient() {
        return client;
    }

    CarClass getCarType() {
        return carType;
    }

    Address getFrom() {
        return from;
    }

    Address getTo() {
        return to;
    }

    Instant getStarted() {
        return started;
    }

    Instant getAcceptedAt() {
        return acceptedAt;
    }

    void startedAt(Instant when, Long transitId) {
        this.started = when;
        this.status = Status.IN_TRANSIT;
        this.transitId = transitId;
    }

    void acceptedAt(Instant when, Long driverId) {
        this.acceptedAt = when;
        this.driverId = driverId;
        this.status = Status.TRANSIT_TO_PASSENGER;

    }

    void publishedAt(Instant when) {
        this.publishedAt = when;
        this.status = Status.WAITING_FOR_DRIVER_ASSIGNMENT;
    }

    void completedAt(Instant when, Money price, Money driverFee) {
        this.completeAt = when;
        this.price = price;
        this.driversFee = driverFee;
        this.status = Status.COMPLETED;
    }

    void pickupChangedTo(Address newAddress, Distance newDistance) {
        this.from = newAddress;
        this.distance = newDistance;
    }

    void destinationChangedTo(Address newAddress, Distance newDistance) {
        this.to = newAddress;
        this.distance = newDistance;
    }

    Money getDriversFee() {
        return driversFee;
    }

    Money getPrice() {
        return price;
    }

    Long getDriverId() {
        return driverId;
    }

    void involvedDriversAre(InvolvedDriversSummary involvedDriversSummary) {
        if (involvedDriversSummary.status == AssignmentStatus.DRIVER_ASSIGNMENT_FAILED) {
            this.status = Status.DRIVER_ASSIGNMENT_FAILED;
        } else {
            this.status = Status.TRANSIT_TO_PASSENGER;
        }
    }

    void cancelled() {
        status = Status.CANCELLED;
    }

    Money getEstimatedPrice() {
        return estimatedPrice;
    }

    Status getStatus() {
        return status;
    }

    Instant getPublishedAt() {
        return publishedAt;
    }

    Distance getDistance() {
        return distance;
    }

    Float getKmRate() {
        if (tariff == null) {
            return null;
        }
        return tariff.getKmRate();
    }

    Integer getBaseFee() {
        if (tariff == null) {
            return null;
        }
        return tariff.getBaseFee();
    }

    String getTariffName() {
        if (tariff == null) {
            return null;
        }
        return tariff.getName();
    }

    public Long getTransitId() {
        return transitId;
    }
}
