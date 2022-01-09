package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class ContractAttachment extends BaseEntity {

    public enum Status {
        PROPOSED, ACCEPTED_BY_ONE_SIDE, ACCEPTED_BY_BOTH_SIDES, REJECTED
    }

    @Column(nullable = false)
    private UUID contractAttachmentNo = UUID.randomUUID();

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PROPOSED;

    @ManyToOne
    private Contract contract;

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(Instant rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public UUID getContractAttachmentNo() {
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
