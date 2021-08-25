package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.Transit;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransitDTO {

    private Long id;

    private String tariff;

    private Transit.Status status;

    public DriverDTO driver;

    public Integer factor;

    private Float distance;

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
        factor = transit.factor;
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
        if (day.getYear() <= 2018) {
            this.kmRate = 1.0f;
            this.tariff = "Standard";
            return;
        }

        Integer year = day.getYear();
        boolean leap = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);

        if (((leap && day.getDayOfYear() == 366) || (!leap && day.getDayOfYear() == 365)) || (day.getDayOfYear() == 1 && day.getHour() <= 6)) {
            this.tariff = "Sylwester";
            this.kmRate = 3.50f;
        } else {
            switch (day.getDayOfWeek()) {
                case MONDAY:
                case TUESDAY:
                case WEDNESDAY:
                case THURSDAY:
                    this.kmRate = 1.0f;
                    this.tariff = "Standard";
                    break;
                case FRIDAY:
                    if (day.getHour() < 17) {
                        this.tariff = "Standard";
                        this.kmRate = 1.0f;
                    } else {
                        this.tariff = "Weekend+";
                        this.kmRate = 2.50f;
                    }
                    break;
                case SATURDAY:
                    if (day.getHour() < 6 || day.getHour() >= 17) {
                        this.kmRate = 2.50f;
                        this.tariff = "Weekend+";
                    } else if (day.getHour() < 17) {
                        this.kmRate = 1.5f;
                        this.tariff = "Weekend";
                    }
                    break;
                case SUNDAY:
                    if (day.getHour() < 6) {
                        this.kmRate = 2.50f;
                        this.tariff = "Weekend+";
                    } else {
                        this.kmRate = 1.5f;
                        this.tariff = "Weekend";
                    }
                    break;
            }
        }

    }

    public String getTariff() {
        return tariff;
    }

    public String getDistance(String unit) {
        this.distanceUnit = unit;
        if (unit.equals("km")) {
            if (distance == Math.ceil(distance)) {
                return String.format(Locale.US, "%d", Math.round(distance)) + "km";

            }
            return String.format(Locale.US, "%.3f", distance) + "km";
        }
        if (unit.equals("miles")) {
            float distance = this.distance / 1.609344f;
            if (distance == Math.ceil(distance)) {
                return String.format(Locale.US, "%d", Math.round(distance)) + "miles";
            }
            return String.format(Locale.US, "%.3f", distance) + "miles";

        }
        if (unit.equals("m")) {
            return String.format(Locale.US, "%d", Math.round(distance * 1000)) + "m";
        }
        throw new IllegalArgumentException("Invalid unit " + unit);
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
