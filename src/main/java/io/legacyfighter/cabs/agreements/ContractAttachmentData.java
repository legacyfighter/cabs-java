package io.legacyfighter.cabs.agreements;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
class ContractAttachmentData extends BaseEntity {

    @Column(nullable = false)
    private UUID contractAttachmentNo;

    @Lob
    @Column(name = "data", columnDefinition="BLOB")
    private byte[] data;

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    ContractAttachmentData() {
    }

    ContractAttachmentData(UUID contractAttachmentId, byte[] data) {
        this.contractAttachmentNo = contractAttachmentId;
        this.data = data;
    }

    byte[] getData() {
        return data;
    }

    Instant getCreationDate() {
        return creationDate;
    }

    UUID getContractAttachmentNo() {
        return contractAttachmentNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ContractAttachmentData))
            return false;

        ContractAttachmentData other = (ContractAttachmentData) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }

}
