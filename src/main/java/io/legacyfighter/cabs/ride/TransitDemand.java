package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Entity;
import java.util.UUID;


@Entity
public class TransitDemand extends BaseEntity {

    private UUID transitRequestUUID;

    public enum Status {
        CANCELLED,
        WAITING_FOR_DRIVER_ASSIGNMENT,
        TRANSIT_TO_PASSENGER,
    }

    private Status status;

    public Integer pickupAddressChangeCounter = 0;

    TransitDemand() {

    }

    public TransitDemand(UUID transitRequestUUID) {
        this.transitRequestUUID = transitRequestUUID;
        this.status = Status.WAITING_FOR_DRIVER_ASSIGNMENT;
    }

    public void changePickup(double distanceFromPreviousPickup) {
        if (distanceFromPreviousPickup > 0.25) {
            throw new IllegalStateException("Address 'from' cannot be changed, id = " + getId());
        } else if (!(this.status.equals(Status.WAITING_FOR_DRIVER_ASSIGNMENT))) {
            throw new IllegalStateException("Address 'from' cannot be changed, id = " + getId());
        } else if (pickupAddressChangeCounter > 2) {
            throw new IllegalStateException("Address 'from' cannot be changed, id = " + getId());
        }
        this.pickupAddressChangeCounter = pickupAddressChangeCounter + 1;
    }

    public void accept() {
        status = Status.TRANSIT_TO_PASSENGER;
    }

    public void cancel() {
        if (this.status != Status.WAITING_FOR_DRIVER_ASSIGNMENT) {
            throw new IllegalStateException("Demand cannot be cancelled, id = " + getId());
        }
        this.status = Status.CANCELLED;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Transit))
            return false;

        Transit other = (Transit) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
