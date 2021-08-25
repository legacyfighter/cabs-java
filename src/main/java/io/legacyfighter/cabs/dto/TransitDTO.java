package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.Transit;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TransitDTO {

    private Long id;

    private String tariff;

    private Transit.Status status;

    public DriverDTO driver;

    public Integer factor;

    private Distance distance;

    private String distanceUnit;

    private float kmRate;

    private BigDecimal price;

    private BigDecimal driverFee;

    private BigDecimal estimatedPrice;

    private BigDecimal baseFee;

    private Instant date;

    private Instant dateTime;

    private Instant published;

    private Instant acceptedAt;

    private Instant started;

    private Instant completeAt;

    private ClaimDTO claimDTO;

    private List<DriverDTO> proposedDrivers = new ArrayList<>();

    private AddressDTO to;

    private AddressDTO from;

    private CarType.CarClass carClass;

    private ClientDTO clientDTO;

    public TransitDTO() {

    }

    public TransitDTO(Transit transit) {
        id = transit.getId();
        distance = transit.getKm();
        factor = 1;
        if (transit.getPrice() != null) {
            price = new BigDecimal(transit.getPrice().toInt());
        }
        date = transit.getDateTime();
        status = transit.getStatus();
        setTariff(transit);
        for (Driver d : transit.getProposedDrivers()) {
            proposedDrivers.add(new DriverDTO(d));
        }
        to = new AddressDTO(transit.getTo());
        from = new AddressDTO(transit.getFrom());
        carClass = transit.getCarType();
        clientDTO = new ClientDTO(transit.getClient());
        if (transit.getDriversFee() != null) {
            driverFee = new BigDecimal(transit.getDriversFee().toInt());
        }
        if (transit.getEstimatedPrice() != null) {
            estimatedPrice = new BigDecimal(transit.getEstimatedPrice().toInt());
        }
        dateTime = transit.getDateTime();
        published = transit.getPublished();
        acceptedAt = transit.getAcceptedAt();
        started = transit.getStarted();
        completeAt = transit.getCompleteAt();

    }

    public float getKmRate() {
        return kmRate;
    }

    private void setTariff(Transit transit) {
        LocalDateTime day = date.atZone(ZoneId.systemDefault()).toLocalDateTime();

        // wprowadzenie nowych cennikow od 1.01.2019
        this.tariff = transit.getTariff().getName();
        this.kmRate = transit.getTariff().getKmRate();
        this.baseFee = new BigDecimal(transit.getTariff().getBaseFee());

    }

    public String getTariff() {
        return tariff;
    }

    public String getDistance(String unit) {
        this.distanceUnit = unit;
        return distance.printIn(unit);
    }

    public List<DriverDTO> getProposedDrivers() {
        return proposedDrivers;
    }

    public void setProposedDrivers(List<DriverDTO> proposedDrivers) {
        this.proposedDrivers = proposedDrivers;
    }

    public ClaimDTO getClaimDTO() {
        return claimDTO;
    }

    public void setClaimDTO(ClaimDTO claimDTO) {
        this.claimDTO = claimDTO;
    }


    public AddressDTO getTo() {
        return to;
    }

    public void setTo(AddressDTO to) {
        this.to = to;
    }

    public AddressDTO getFrom() {
        return from;
    }

    public void setFrom(AddressDTO from) {
        this.from = from;
    }

    public CarType.CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarType.CarClass carClass) {
        this.carClass = carClass;
    }

    public ClientDTO getClientDTO() {
        return clientDTO;
    }

    public void setClientDTO(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
    }

    public Long getId() {
        return id;
    }

    public Transit.Status getStatus() {
        return status;
    }

    public void setStatus(Transit.Status status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getDriverFee() {
        return driverFee;
    }

    public void setDriverFee(BigDecimal driverFee) {
        this.driverFee = driverFee;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public Instant getPublished() {
        return published;
    }

    public void setPublished(Instant published) {
        this.published = published;
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

    public Instant getCompleteAt() {
        return completeAt;
    }

    public void setCompleteAt(Instant completeAt) {
        this.completeAt = completeAt;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
