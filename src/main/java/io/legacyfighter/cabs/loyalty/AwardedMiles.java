package io.legacyfighter.cabs.loyalty;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public
class AwardedMiles extends BaseEntity {

    private Long clientId;

    @Column(nullable = false)
    private Instant date = Instant.now();

    private String milesJson;

    private Long transitId;

    @ManyToOne
    private AwardsAccount account;

    public AwardedMiles() {
    }

    public AwardedMiles(AwardsAccount awardsAccount, Long transitId, Long clientId, Instant when, Miles constantUntil) {
        this.account = awardsAccount;
        this.transitId = transitId;
        this.clientId = clientId;
        this.date = when;
        setMiles(constantUntil);
    }

    public Long getClientId() {
        return clientId;
    }

    void transferTo(AwardsAccount account) {
        this.clientId = account.getClientId();
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
