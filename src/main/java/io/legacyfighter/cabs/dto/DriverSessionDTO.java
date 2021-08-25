package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.entity.DriverSession;

import java.time.Instant;


public class DriverSessionDTO {

    private Instant loggedAt;

    private Instant loggedOutAt;

    private String platesNumber;

    private CarType.CarClass carClass;

    private String carBrand;

    public DriverSessionDTO() {

    }

    public DriverSessionDTO(DriverSession session) {
        this.carBrand = session.getCarBrand();
        this.platesNumber = session.getPlatesNumber();
        this.loggedAt = session.getLoggedAt();
        this.loggedOutAt = session.getLoggedOutAt();
        this.carClass = session.getCarClass();
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

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

}


