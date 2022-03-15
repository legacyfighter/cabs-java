package io.legacyfighter.cabs.agreements;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
class ContractAttachment extends BaseEntity {

    @Column(nullable = false)
    private UUID contractAttachmentNo = UUID.randomUUID();

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    @Enumerated(EnumType.STRING)
    private ContractAttachmentStatus status = ContractAttachmentStatus.PROPOSED;

    @ManyToOne
    private Contract contract;

    Instant getAcceptedAt() {
        return acceptedAt;
    }

    void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    Instant getRejectedAt() {
        return rejectedAt;
    }

    void setRejectedAt(Instant rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    Instant getChangeDate() {
        return changeDate;
    }

    void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    ContractAttachmentStatus getStatus() {
        return status;
    }

    void setStatus(ContractAttachmentStatus status) {
        this.status = status;
    }

    Contract getContract() {
        return contract;
    }

    void setContract(Contract contract) {
        this.contract = contract;
    }

    UUID getContractAttachmentNo() {
        return contractAttachmentNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ContractAttachment))
            return false;

        ContractAttachment other = (ContractAttachment) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
