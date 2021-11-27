package io.legacyfighter.cabs.entity;


public class CarTypeActiveCounter  {

    private final CarType carType;

    public CarTypeActiveCounter(CarType carType) {
        this.carType = carType;
    }

    public void registerActiveCar() {
        carType.registerActiveCar();
    }

    public void unregisterActiveCar() {
        carType.unregisterActiveCar();
    }

    public int getActiveCarsCounter() {
        return carType.getActiveCarsCounter();
    }
}