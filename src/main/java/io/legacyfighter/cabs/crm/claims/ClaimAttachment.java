package io.legacyfighter.cabs.crm.claims;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
class ClaimAttachment extends BaseEntity {

    public ClaimAttachment() {

    }

    @ManyToOne
    private Claim claim;

    @Column(nullable = false)
    private Instant creationDate;

    private String description;

    @Lob
    @Column(name = "data", columnDefinition="BLOB")
    private byte[] data;

    Long getClientId() {
        return claim.getOwnerId();
    }

    Claim getClaim() {
        return claim;
    }

    void setClaim(Claim claim) {
        this.claim = claim;
    }

    Instant getCreationDate() {
        return creationDate;
    }

    void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    byte[] getData() {
        return data;
    }

    void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ClaimAttachment))
            return false;

        ClaimAttachment other = (ClaimAttachment) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
