package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.crm.ClientDTO;
import io.legacyfighter.cabs.crm.claims.ClaimDTO;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.transitdetails.TransitDetailsDTO;

import java.math.BigDecimal;
import java.time.Instant;
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

    private CarClass carClass;

    private ClientDTO clientDTO;

    public TransitDTO() {

    }

    public TransitDTO(Transit transit, TransitDetailsDTO transitDetails) {
        this(transitDetails.transitId, transitDetails.tariffName,
                transitDetails.status, transit.getDriver() == null ? null : new DriverDTO(transit.getDriver()),
                transitDetails.distance, transitDetails.kmRate,
                transitDetails.price != null ? new BigDecimal(transitDetails.price.toInt()) : null,
                transitDetails.driverFee != null ? new BigDecimal(transitDetails.driverFee.toInt()) : null,
                transitDetails.estimatedPrice != null ? new BigDecimal(transitDetails.estimatedPrice.toInt()) : null,
                new BigDecimal(transitDetails.baseFee),
                transitDetails.dateTime, transitDetails.publishedAt,
                transitDetails.acceptedAt, transitDetails.started, transitDetails.completedAt,
                null, new ArrayList<>(), transitDetails.from,
                transitDetails.to, transitDetails.carType, transitDetails.client);

        for (Driver d : transit.getProposedDrivers()) {
            proposedDrivers.add(new DriverDTO(d));
        }
    }

    public TransitDTO(Long id, String tariff, Transit.Status status, DriverDTO driver,
                      Distance distance, float kmRate, BigDecimal price, BigDecimal driverFee,
                      BigDecimal estimatedPrice, BigDecimal baseFee, Instant dateTime,
                      Instant published, Instant acceptedAt, Instant started, Instant completeAt,
                      ClaimDTO claimDTO, List<DriverDTO> proposedDrivers, AddressDTO from, AddressDTO to,
                      CarClass carClass, ClientDTO clientDTO) {
        this.id = id;
        this.factor = 1;
        this.tariff = tariff;
        this.status = status;
        this.driver = driver;
        this.distance = distance;
        this.kmRate = kmRate;
        this.price = price;
        this.driverFee = driverFee;
        this.estimatedPrice = estimatedPrice;
        this.baseFee = baseFee;
        this.dateTime = dateTime;
        this.published = published;
        this.acceptedAt = acceptedAt;
        this.started = started;
        this.completeAt = completeAt;
        this.claimDTO = claimDTO;
        this.proposedDrivers = proposedDrivers;
        this.to = to;
        this.from = from;
        this.carClass = carClass;
        this.clientDTO = clientDTO;
    }

    public float getKmRate() {
        return kmRate;
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

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
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
