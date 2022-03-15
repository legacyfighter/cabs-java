package io.legacyfighter.cabs.agreements;

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
class Contract extends BaseEntity {

    Contract() {
    }

    Contract(String partnerName, String subject, String contractNo) {
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
    private ContractStatus status = ContractStatus.NEGOTIATIONS_IN_PROGRESS;

    @Column(nullable = false)
    private String contractNo;


    Instant getCreationDate() {
        return creationDate;
    }

    Instant getAcceptedAt() {
        return acceptedAt;
    }

    Instant getRejectedAt() {
        return rejectedAt;
    }

    Instant getChangeDate() {
        return changeDate;
    }

    ContractStatus getStatus() {
        return status;
    }

    String getContractNo() {
        return contractNo;
    }

    String getPartnerName() {
        return partnerName;
    }

    String getSubject() {
        return subject;
    }

    List<UUID> getAttachmentIds() {
        return attachments
                .stream()
                .map(ContractAttachment::getContractAttachmentNo)
                .collect(Collectors.toList());
    }

    ContractAttachment proposeAttachment() {
        ContractAttachment contractAttachment = new ContractAttachment();
        contractAttachment.setContract(this);
        attachments.add(contractAttachment);
        return contractAttachment;
    }

    void accept() {
        if (attachments.stream().allMatch(a -> a.getStatus().equals(ContractAttachmentStatus.ACCEPTED_BY_BOTH_SIDES))) {
            this.status = ContractStatus.ACCEPTED;
        } else {
            throw new IllegalStateException("Not all attachments accepted by both sides");
        }
    }

    void reject() {
        this.status = ContractStatus.REJECTED;
    }

    void acceptAttachment(UUID contractAttachmentNo) {
        ContractAttachment contractAttachment = findAttachment(contractAttachmentNo);
        if (contractAttachment.getStatus().equals(ContractAttachmentStatus.ACCEPTED_BY_ONE_SIDE) || contractAttachment.getStatus().equals(ContractAttachmentStatus.ACCEPTED_BY_BOTH_SIDES)) {
            contractAttachment.setStatus(ContractAttachmentStatus.ACCEPTED_BY_BOTH_SIDES);
        } else {
            contractAttachment.setStatus(ContractAttachmentStatus.ACCEPTED_BY_ONE_SIDE);
        }
    }

    void rejectAttachment(UUID contractAttachmentNo) {
        ContractAttachment contractAttachment = findAttachment(contractAttachmentNo);
        contractAttachment.setStatus(ContractAttachmentStatus.REJECTED);
    }

    void remove(UUID contractAttachmentNo) {
        attachments.removeIf(attachment -> attachment.getContractAttachmentNo().equals(contractAttachmentNo));
    }

    ContractAttachment findAttachment(UUID attachmentNo) {
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
