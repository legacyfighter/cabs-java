package io.legacyfighter.cabs.tracking;

import java.time.Instant;

public class DriverPositionDTO {

    public DriverPositionDTO() {

    }

    private Long driverId;

    private double latitude;

    private double longitude;

    private Instant seenAt;

    public DriverPositionDTO(Long driverId, double latitude, double longitude, Instant seenAt) {
        this.driverId = driverId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.seenAt = seenAt;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Instant getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Instant seenAt) {
        this.seenAt = seenAt;
    }
}
