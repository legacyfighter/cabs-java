package io.legacyfighter.cabs.entity.miles;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class AwardedMiles extends BaseEntity {

    @ManyToOne
    private Client client;

    @Column(nullable = false)
    private Instant date = Instant.now();

    private String milesJson;

    @ManyToOne
    private Transit transit;

    public AwardedMiles() {
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Miles getMiles() {
        return MilesJsonMapper.deserialize(milesJson);
    }

    public Integer getMilesAmount(Instant when) {
        return getMiles().getAmountFor(when);
    }

    public void setMiles(Miles miles) {
        milesJson = MilesJsonMapper.serialize(miles);
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getExpirationDate() {
        return getMiles().expiresAt();
    }

    public Boolean cantExpire() {
        return getExpirationDate().equals(Instant.MAX);
    }

    public void setTransit(Transit transit) {
        this.transit = transit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AwardedMiles))
            return false;

        AwardedMiles other = (AwardedMiles) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }

    public void removeAll(Instant forWhen) {
        setMiles(this.getMiles().subtract(this.getMilesAmount(forWhen), forWhen));
    }

    public void subtract(Integer miles, Instant forWhen) {
        this.setMiles(this.getMiles().subtract(miles, forWhen));
    }
}
