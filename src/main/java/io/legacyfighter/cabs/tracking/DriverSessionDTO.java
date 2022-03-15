package io.legacyfighter.cabs.tracking;

import io.legacyfighter.cabs.carfleet.CarClass;

import java.time.Instant;


public class DriverSessionDTO {

    private Instant loggedAt;

    private Instant loggedOutAt;

    private String platesNumber;

    private CarClass carClass;

    private String carBrand;

    public DriverSessionDTO(Instant loggedAt, Instant loggedOutAt, String platesNumber, CarClass carClass, String carBrand) {
        this.loggedAt = loggedAt;
        this.loggedOutAt = loggedOutAt;
        this.platesNumber = platesNumber;
        this.carClass = carClass;
        this.carBrand = carBrand;
    }

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

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

}


