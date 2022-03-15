package io.legacyfighter.cabs.agreements;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ContractDTO {

    private Long id;

    public ContractDTO() {
    }

    private String subject;

    private String partnerName;

    private Instant creationDate;

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    private ContractStatus status;

    private String contractNo;

    private List<ContractAttachmentDTO> attachments = new ArrayList<>();

    public ContractDTO(Contract contract, Set<ContractAttachmentData> attachments) {
        this.setContractNo(contract.getContractNo());
        this.setAcceptedAt(contract.getAcceptedAt());
        this.setRejectedAt(contract.getRejectedAt());
        this.setCreationDate(contract.getCreationDate());
        this.setChangeDate(contract.getChangeDate());
        this.setStatus(contract.getStatus());
        this.setPartnerName(contract.getPartnerName());
        this.setSubject(contract.getSubject());
        for (ContractAttachmentData attachmentData : attachments) {
            UUID contractAttachmentNo = attachmentData.getContractAttachmentNo();
            ContractAttachment attachment = contract.findAttachment(contractAttachmentNo);
            this.attachments.add(new ContractAttachmentDTO(attachment, attachmentData));
        }
        this.setId(contract.getId());
    }

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

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ContractAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ContractAttachmentDTO> attachments) {
        this.attachments = attachments;
    }
}
