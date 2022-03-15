package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class DriverSession extends BaseEntity {

    @Column(nullable = false)
    private Instant loggedAt;

    private Instant loggedOutAt;

    private Long driverId;

    @Column(nullable = false)
    private String platesNumber;

    @Enumerated(EnumType.STRING)
    private CarClass carClass;

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    private String carBrand;

    public Instant getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(Instant loggedAt) {
        this.loggedAt = loggedAt;
    }

    public Instant getLoggedOutAt() {
        return loggedOutAt;
    }

    public void setLoggedOutAt(Instant loggedOutAt) {
        this.loggedOutAt = loggedOutAt;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getPlatesNumber() {
        return platesNumber;
    }

    public void setPlatesNumber(String platesNumber) {
        this.platesNumber = platesNumber;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DriverSession))
            return false;

        DriverSession other = (DriverSession) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}


