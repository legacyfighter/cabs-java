package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.driverfleet.Driver;

import java.time.Instant;

public class DriverPositionDTOV2 {

    public DriverPositionDTOV2() {

    }

    private Driver driver;

    private double latitude;

    private double longitude;

    private Instant seenAt;

    public DriverPositionDTOV2(Driver driver, double latitude, double longitude, Instant seenAt) {
        this.driver = driver;
        this.latitude = latitude;
        this.longitude = longitude;
        this.seenAt = seenAt;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
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
