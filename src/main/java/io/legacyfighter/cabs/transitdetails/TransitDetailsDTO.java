package io.legacyfighter.cabs.transitdetails;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.dto.ClientDTO;
import io.legacyfighter.cabs.entity.Tariff;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;

import java.time.Instant;

public class TransitDetailsDTO {

    public Long transitId;

    public Instant dateTime;

    public Instant completedAt;

    public ClientDTO client;

    public CarClass carType;

    public AddressDTO from;

    public AddressDTO to;

    public Instant started;

    public Instant acceptedAt;

    public Money price;

    public Money driverFee;

    public Long driverId;

    public Money estimatedPrice;

    public Transit.Status status;

    public Instant publishedAt;

    public Distance distance;

    public Integer baseFee;

    public Float kmRate;

    public String tariffName;

    public TransitDetailsDTO(TransitDetails td) {
        transitId = td.getTransitId();
        dateTime = td.getDateTime();
        completedAt = td.getCompleteAt();
        client = new ClientDTO(td.getClient());
        carType = td.getCarType();
        from = new AddressDTO(td.getFrom());
        to = new AddressDTO(td.getTo());
        started = td.getStarted();
        acceptedAt = td.getAcceptedAt();
        driverFee = td.getDriversFee();
        price = td.getPrice();
        driverId = td.getDriverId();
        estimatedPrice = td.getEstimatedPrice();
        status = td.getStatus();
        publishedAt = td.getPublishedAt();
        distance = td.getDistance();
        baseFee = td.getBaseFee();
        kmRate = td.getKmRate();
        tariffName = td.getTariffName();
    }

    public TransitDetailsDTO(Long transitId, Instant dateTime, Instant completedAt, ClientDTO client, CarClass carType, AddressDTO from, AddressDTO to, Instant started, Instant acceptedAt, Distance distance, Tariff tariff) {
        this.transitId = transitId;
        this.dateTime = dateTime;
        this.completedAt = completedAt;
        this.client = client;
        this.carType = carType;
        this.from = from;
        this.to = to;
        this.started = started;
        this.acceptedAt = acceptedAt;
        this.distance = distance;
        this.kmRate = tariff.getKmRate();
        this.baseFee = tariff.getBaseFee();
        this.tariffName = tariff.getName();
    }
}
