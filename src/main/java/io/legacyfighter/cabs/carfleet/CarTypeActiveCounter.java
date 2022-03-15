package io.legacyfighter.cabs.carfleet;

import javax.persistence.*;

@Entity
class CarTypeActiveCounter {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Id
    private CarClass carClass;

    @Column(nullable = false)
    private int activeCarsCounter;

    CarTypeActiveCounter(CarClass carClass) {
        this.carClass = carClass;
    }

    CarTypeActiveCounter() {
    }

    int getActiveCarsCounter() {
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