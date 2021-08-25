package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Contract extends BaseEntity {

    public enum Status {
        NEGOTIATIONS_IN_PROGRESS, REJECTED, ACCEPTED
    }

    public Contract() {
    }

    @OneToMany(mappedBy = "contract")
    private Set<ContractAttachment> attachments = new HashSet<>();

    private String partnerName;

    private String subject;

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEGOTIATIONS_IN_PROGRESS;

    @Column(nullable = false)
    private String contractNo;


    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

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

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Set<ContractAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<ContractAttachment> attachments) {
        this.attachments = attachments;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Contract))
            return false;

        Contract other = (Contract) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
