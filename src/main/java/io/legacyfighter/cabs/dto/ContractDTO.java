package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.entity.Contract;
import io.legacyfighter.cabs.entity.Contract.Status;
import io.legacyfighter.cabs.entity.ContractAttachment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    private Status status;

    private String contractNo;

    private List<ContractAttachmentDTO> attachments = new ArrayList<>();

    public ContractDTO(Contract contract) {
        this.setContractNo(contract.getContractNo());
        this.setAcceptedAt(contract.getAcceptedAt());
        this.setRejectedAt(contract.getRejectedAt());
        this.setCreationDate(contract.getCreationDate());
        this.setChangeDate(contract.getChangeDate());
        this.setStatus(contract.getStatus());
        this.setPartnerName(contract.getPartnerName());
        this.setSubject(contract.getSubject());
        for (ContractAttachment attachment : contract.getAttachments()) {
            attachments.add(new ContractAttachmentDTO(attachment));
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
