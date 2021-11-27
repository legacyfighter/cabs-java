package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;

@Entity
public class CarTypeActiveCounter  {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Id
    private CarType.CarClass carClass;

    @Column(nullable = false)
    private int activeCarsCounter;

    public CarTypeActiveCounter(CarType.CarClass carClass) {
        this.carClass = carClass;
    }

    public CarTypeActiveCounter() {
    }

    public int getActiveCarsCounter() {
        return activeCarsCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CarTypeActiveCounter))
            return false;

        CarTypeActiveCounter other = (CarTypeActiveCounter) o;

        return this.carClass != null &&
                this.carClass.equals(other.carClass);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}