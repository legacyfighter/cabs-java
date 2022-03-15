package io.legacyfighter.cabs.carfleet;

public class CarTypeDTO {

    private Long id;

    private CarClass carClass;

    private CarType.Status status;

    private int carsCounter;

    private String description;

    private int activeCarsCounter;

    private int minNoOfCarsToActivateClass;

    public CarTypeDTO(CarType carType, int activeCarsCounter) {
        this.id = carType.getId();
        this.carClass = carType.getCarClass();
        this.status = carType.getStatus();
        this.carsCounter = carType.getCarsCounter();
        this.description = carType.getDescription();
        this.activeCarsCounter = activeCarsCounter;
        this.minNoOfCarsToActivateClass = carType.getMinNoOfCarsToActivateClass();
    }

    public CarTypeDTO() {

    }

    public Long getId() {
        return id;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public CarType.Status getStatus() {
        return status;
    }

    public void setStatus(CarType.Status status) {
        this.status = status;
    }

    public int getCarsCounter() {
        return carsCounter;
    }

    public void setCarsCounter(int carsCounter) {
        this.carsCounter = carsCounter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActiveCarsCounter() {
        return activeCarsCounter;
    }

    public void setActiveCarsCounter(int activeCarsCounter) {
        this.activeCarsCounter = activeCarsCounter;
    }


    public int getMinNoOfCarsToActivateClass() {
        return minNoOfCarsToActivateClass;
    }

    public void setMinNoOfCarsToActivateClass(int minNoOfCarsToActivateClass) {
        this.minNoOfCarsToActivateClass = minNoOfCarsToActivateClass;
    }
}


