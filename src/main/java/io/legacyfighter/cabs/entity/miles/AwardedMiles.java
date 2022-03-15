package io.legacyfighter.cabs.entity.miles;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.entity.Client;

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

    private Long transitId;

    @ManyToOne
    private AwardsAccount account;

    public AwardedMiles() {
    }

    public AwardedMiles(AwardsAccount awardsAccount, Long transitId, Client client, Instant when, Miles constantUntil) {
        this.account = awardsAccount;
        this.transitId = transitId;
        this.client = client;
        this.date = when;
        setMiles(constantUntil);
    }

    public Client getClient() {
        return client;
    }

    void transferTo(AwardsAccount account) {
        this.client = account.getClient();
        this.account = account;

    }

    public Miles getMiles() {
        return MilesJsonMapper.deserialize(milesJson);
    }

    public Integer getMilesAmount(Instant when) {
        return getMiles().getAmountFor(when);
    }

    private void setMiles(Miles miles) {
        milesJson = MilesJsonMapper.serialize(miles);
    }

    public Instant getDate() {
        return date;
    }

    public Instant getExpirationDate() {
        return getMiles().expiresAt();
    }

    public Boolean cantExpire() {
        return getExpirationDate().equals(Instant.MAX);
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
