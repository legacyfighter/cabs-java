package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariff;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class Transit extends BaseEntity {

    enum Status {
        IN_TRANSIT,
        COMPLETED
    }

    private UUID transitRequestUUID;

    private Status status;

    @Embedded
    private Tariff tariff;

    private float km;

    public Transit() {
        status = Status.IN_TRANSIT;
    }

    public Transit(Long id) {
        this.id = id;
    }

    public Transit(Tariff tariff, UUID transitRequestUUID) {
        this(Status.IN_TRANSIT, tariff, transitRequestUUID);
    }

    public Transit(Status status, Tariff tariff, UUID transitRequestUUID) {
        this.status = status;
        this.tariff = tariff;
        this.transitRequestUUID = transitRequestUUID;
    }

    public void changeDestination(Distance newDistance) {
        if (status.equals(Status.COMPLETED)) {
            throw new IllegalStateException("Address 'to' cannot be changed, id = " + getId());
        }
        this.km = newDistance.toKmInFloat();
    }

    public Money completeAt(Distance distance) {
        if (status.equals(Status.IN_TRANSIT)) {
            this.km = distance.toKmInFloat();
            this.status = Status.COMPLETED;
            return calculateFinalCosts();
        } else {
            throw new IllegalArgumentException("Cannot complete Transit, id = " + getId());
        }
    }

    public Status getStatus() {
        return status;
    }

    public Money calculateFinalCosts() {
        if (status.equals(Status.COMPLETED)) {
            return calculateCost();
        } else {
            throw new IllegalStateException("Cannot calculate final cost if the transit is not completed");
        }
    }

    private Money calculateCost() {
        return this.tariff.calculateCost(getDistance());
    }

    public Distance getDistance() {
        return Distance.ofKm(km);
    }

    public UUID getRequestUUID() {
        return transitRequestUUID;
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
