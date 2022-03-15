package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariff;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class RequestForTransit extends BaseEntity {

    private UUID requestUUID = UUID.randomUUID();

    @Embedded
    private Tariff tariff;

    @Embedded
    private Distance distance;

    RequestForTransit() {
    }

    public RequestForTransit(Tariff tariff, Distance distance) {
        this.tariff = tariff;
        this.distance = distance;
    }

    public Money getEstimatedPrice() {
        return tariff.calculateCost(distance);
    }

    public UUID getRequestUUID() {
        return requestUUID;
    }

    public Tariff getTariff() {
        return tariff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RequestForTransit))
            return false;

        RequestForTransit other = (RequestForTransit) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }


    public Distance getDistance() {
        return distance;
    }

}
