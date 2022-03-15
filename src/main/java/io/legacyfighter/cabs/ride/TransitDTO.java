package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.crm.ClientDTO;
import io.legacyfighter.cabs.crm.claims.ClaimDTO;
import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.ride.details.Status;
import io.legacyfighter.cabs.ride.details.TransitDetailsDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TransitDTO {

    private Long id; //Transit.id

    private UUID requestId;

    private String tariff;

    private Status status;

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

    public TransitDTO(TransitDetailsDTO transitDetails, Set<DriverDTO> proposedDrivers, Set<DriverDTO> driverRejections, Long assignedDriver) {
        this(transitDetails.transitId, transitDetails.requestUUID, transitDetails.tariffName,
                transitDetails.status,
                proposedDrivers
                        .stream()
                        .filter(driver -> driver.getId().equals(assignedDriver))
                        .findFirst()
                        .orElse(null),
                transitDetails.distance, transitDetails.kmRate,
                transitDetails.price != null ? new BigDecimal(transitDetails.price.toInt()) : null,
                transitDetails.driverFee != null ? new BigDecimal(transitDetails.driverFee.toInt()) : null,
                transitDetails.estimatedPrice != null ? new BigDecimal(transitDetails.estimatedPrice.toInt()) : null,
                new BigDecimal(transitDetails.baseFee),
                transitDetails.dateTime, transitDetails.publishedAt,
                transitDetails.acceptedAt, transitDetails.started, transitDetails.completedAt,
                null, new ArrayList<>(proposedDrivers), transitDetails.from,
                transitDetails.to, transitDetails.carType, transitDetails.client);
    }

    public TransitDTO(Long id, UUID requestId, String tariff, Status status, DriverDTO driver,
                      Distance distance, float kmRate, BigDecimal price, BigDecimal driverFee,
                      BigDecimal estimatedPrice, BigDecimal baseFee, Instant dateTime,
                      Instant published, Instant acceptedAt, Instant started, Instant completeAt,
                      ClaimDTO claimDTO, List<DriverDTO> proposedDrivers, AddressDTO from, AddressDTO to,
                      CarClass carClass, ClientDTO clientDTO) {
        this.id = id;
        this.requestId = requestId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public UUID getRequestId() {
        return requestId;
    }
}
