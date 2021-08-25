package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

import static io.legacyfighter.cabs.distance.Distance.ofKm;

@Entity
public class Transit extends BaseEntity {


    public Transit() {
    }

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

    @OneToOne
    public Client client;

    public CarType.CarClass getCarType() {
        return carType;
    }

    @Enumerated(EnumType.STRING)
    private CarType.CarClass carType;

    public void setCarType(CarType.CarClass carType) {
        this.carType = carType;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCompleteAt() {
        return completeAt;
    }

    private Instant completeAt;

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

    public void setClient(Client client) {
        this.client = client;
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

    public void setPublished(Instant published) {
        this.published = published;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Distance getKm() {
        return Distance.ofKm(km);
    }

    public void setKm(Distance km) {
        this.km = km.toKmInFloat();
        estimateCost();
    }

    public Integer getAwaitingDriversResponses() {
        return awaitingDriversResponses;
    }

    public void setAwaitingDriversResponses(Integer proposedDriversCounter) {
        this.awaitingDriversResponses = proposedDriversCounter;
    }

    public Set<Driver> getDriversRejections() {
        return driversRejections;
    }

    public void setDriversRejections(Set<Driver> driversRejections) {
        this.driversRejections = driversRejections;
    }

    public Set<Driver> getProposedDrivers() {
        return proposedDrivers;
    }

    public void setProposedDrivers(Set<Driver> proposedDrivers) {
        this.proposedDrivers = proposedDrivers;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getStarted() {
        return started;
    }

    public void setStarted(Instant started) {
        this.started = started;
    }

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public Integer getPickupAddressChangeCounter() {
        return pickupAddressChangeCounter;
    }

    public void setPickupAddressChangeCounter(Integer pickupChanges) {
        this.pickupAddressChangeCounter = pickupChanges;
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

    public void completeAt(Instant when) {
        this.completeAt = when;
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
