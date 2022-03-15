package io.legacyfighter.cabs.tracking;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
class DriverPosition extends BaseEntity {

    private Long driverId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private Instant seenAt;

    DriverPosition() {
    }

    Long getDriverId() {
        return driverId;
    }

    void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    double getLatitude() {
        return latitude;
    }

    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    double getLongitude() {
        return longitude;
    }

    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    Instant getSeenAt() {
        return seenAt;
    }

    void setSeenAt(Instant seenAt) {
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
