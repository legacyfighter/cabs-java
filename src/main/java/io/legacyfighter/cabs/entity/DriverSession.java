package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class DriverSession extends BaseEntity {

    @Column(nullable = false)
    private Instant loggedAt;

    private Instant loggedOutAt;

    @ManyToOne
    private Driver driver;

    @Column(nullable = false)
    private String platesNumber;

    @Enumerated(EnumType.STRING)
    private CarType.CarClass carClass;

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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getPlatesNumber() {
        return platesNumber;
    }

    public void setPlatesNumber(String platesNumber) {
        this.platesNumber = platesNumber;
    }

    public CarType.CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarType.CarClass carClass) {
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


