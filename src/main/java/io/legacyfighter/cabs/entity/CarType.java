package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;

@Entity
public class CarType extends BaseEntity {

    public enum Status {
        INACTIVE, ACTIVE
    }

    public enum CarClass {
        ECO, REGULAR, VAN, PREMIUM
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarClass carClass;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.INACTIVE;

    @Column(nullable = false)
    private int carsCounter;

    @Column(nullable = false)
    private int minNoOfCarsToActivateClass;

    @Column(nullable = false)
    private int activeCarsCounter;

    public CarType(CarClass carClass, String description, int minNoOfCarsToActivateClass) {
        this.carClass = carClass;
        this.description = description;
        this.minNoOfCarsToActivateClass = minNoOfCarsToActivateClass;
    }

    public CarType() {
    }

    public void registerActiveCar() {
        activeCarsCounter++;
    }

    public void unregisterActiveCar() {
        activeCarsCounter--;
    }

    public void registerCar() {
        carsCounter++;
    }

    public void unregisterCar() {
        carsCounter--;
        if (carsCounter < 0) {
            throw new IllegalStateException();
        }
    }

    public void activate() {
        if (carsCounter < minNoOfCarsToActivateClass) {
            throw new IllegalStateException("Cannot activate car class when less than " + minNoOfCarsToActivateClass + " cars in the fleet");
        }
        this.status = Status.ACTIVE;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public int getCarsCounter() {
        return carsCounter;
    }

    public int getActiveCarsCounter() {
        return activeCarsCounter;
    }

    public int getMinNoOfCarsToActivateClass() {
        return minNoOfCarsToActivateClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CarType))
            return false;

        CarType other = (CarType) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}


