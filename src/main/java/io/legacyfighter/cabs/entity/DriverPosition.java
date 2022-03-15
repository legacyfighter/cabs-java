package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.driverfleet.Driver;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class DriverPosition extends BaseEntity {

    @ManyToOne
    private Driver driver;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private Instant seenAt;

    public DriverPosition() {
    }

    public DriverPosition(Driver driver, Instant seenAt, double latitude, double longitude) {
        this.driver = driver;
        this.seenAt = seenAt;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DriverPosition))
            return false;

        DriverPosition other = (DriverPosition) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
