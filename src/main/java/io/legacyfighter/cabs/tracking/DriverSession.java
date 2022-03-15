package io.legacyfighter.cabs.tracking;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
public
class DriverSession extends BaseEntity {

    @Column(nullable = false)
    private Instant loggedAt;

    private Instant loggedOutAt;

    private Long driverId;

    @Column(nullable = false)
    private String platesNumber;

    @Enumerated(EnumType.STRING)
    private CarClass carClass;

    private String carBrand;

    String getCarBrand() {
        return carBrand;
    }

    void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    Instant getLoggedAt() {
        return loggedAt;
    }

    void setLoggedAt(Instant loggedAt) {
        this.loggedAt = loggedAt;
    }

    Instant getLoggedOutAt() {
        return loggedOutAt;
    }

    void setLoggedOutAt(Instant loggedOutAt) {
        this.loggedOutAt = loggedOutAt;
    }

    public Long getDriverId() {
        return driverId;
    }

    void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    String getPlatesNumber() {
        return platesNumber;
    }

    void setPlatesNumber(String platesNumber) {
        this.platesNumber = platesNumber;
    }

    CarClass getCarClass() {
        return carClass;
    }

    void setCarClass(CarClass carClass) {
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


