package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class ContractAttachmentData extends BaseEntity {

    @Column(nullable = false)
    private UUID contractAttachmentNo;

    @Lob
    @Column(name = "data", columnDefinition="BLOB")
    private byte[] data;

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    public ContractAttachmentData() {
    }

    public ContractAttachmentData(UUID contractAttachmentId, byte[] data) {
        this.contractAttachmentNo = contractAttachmentId;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public UUID getContractAttachmentNo() {
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
