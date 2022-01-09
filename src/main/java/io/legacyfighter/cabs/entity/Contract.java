package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Contract extends BaseEntity {

    public enum Status {
        NEGOTIATIONS_IN_PROGRESS, REJECTED, ACCEPTED
    }

    public Contract() {
    }

    public Contract(String partnerName, String subject, String contractNo) {
        this.partnerName = partnerName;
        this.subject = subject;
        this.contractNo = contractNo;
    }

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.JOIN)
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

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public Status getStatus() {
        return status;
    }

    public String getContractNo() {
        return contractNo;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getSubject() {
        return subject;
    }

    public List<UUID> getAttachmentIds() {
        return attachments
                .stream()
                .map(ContractAttachment::getContractAttachmentNo)
                .collect(Collectors.toList());
    }

    public ContractAttachment proposeAttachment() {
        ContractAttachment contractAttachment = new ContractAttachment();
        contractAttachment.setContract(this);
        attachments.add(contractAttachment);
        return contractAttachment;
    }

    public void accept() {
        if (attachments.stream().allMatch(a -> a.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES))) {
            this.status = Contract.Status.ACCEPTED;
        } else {
            throw new IllegalStateException("Not all attachments accepted by both sides");
        }
    }

    public void reject() {
        this.status = Status.REJECTED;
    }

    public void acceptAttachment(UUID contractAttachmentNo) {
        ContractAttachment contractAttachment = findAttachment(contractAttachmentNo);
        if (contractAttachment.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_ONE_SIDE) || contractAttachment.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES)) {
            contractAttachment.setStatus(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES);
        } else {
            contractAttachment.setStatus(ContractAttachment.Status.ACCEPTED_BY_ONE_SIDE);
        }
    }

    public void rejectAttachment(UUID contractAttachmentNo) {
        ContractAttachment contractAttachment = findAttachment(contractAttachmentNo);
        contractAttachment.setStatus(ContractAttachment.Status.REJECTED);
    }

    public void remove(UUID contractAttachmentNo) {
        attachments.removeIf(attachment -> attachment.getContractAttachmentNo().equals(contractAttachmentNo));
    }

    public ContractAttachment findAttachment(UUID attachmentNo) {
        return attachments
                .stream()
                .filter(a -> a.getContractAttachmentNo().equals(attachmentNo))
                .findFirst()
                .orElse(null);
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
