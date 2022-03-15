package io.legacyfighter.cabs.carfleet;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;

@Entity
class CarType extends BaseEntity {

    enum Status {
        INACTIVE, ACTIVE
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

    CarType(CarClass carClass, String description, int minNoOfCarsToActivateClass) {
        this.carClass = carClass;
        this.description = description;
        this.minNoOfCarsToActivateClass = minNoOfCarsToActivateClass;
    }

    CarType() {
    }

    void registerCar() {
        carsCounter++;
    }

    void unregisterCar() {
        carsCounter--;
        if (carsCounter < 0) {
            throw new IllegalStateException();
        }
    }

    void activate() {
        if (carsCounter < minNoOfCarsToActivateClass) {
            throw new IllegalStateException("Cannot activate car class when less than " + minNoOfCarsToActivateClass + " cars in the fleet");
        }
        this.status = Status.ACTIVE;
    }

    void deactivate() {
        this.status = Status.INACTIVE;
    }

    CarClass getCarClass() {
        return carClass;
    }

    void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    Status getStatus() {
        return status;
    }

    int getCarsCounter() {
        return carsCounter;
    }

    int getMinNoOfCarsToActivateClass() {
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


