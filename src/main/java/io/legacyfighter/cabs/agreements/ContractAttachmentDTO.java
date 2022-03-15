package io.legacyfighter.cabs.agreements;

import java.time.Instant;

public class ContractAttachmentDTO {

    public ContractAttachmentDTO() {
    }

    private Long id;

    private Long contractId;

    private byte[] data;

    private Instant creationDate;

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    private ContractAttachmentStatus status;


    public ContractAttachmentDTO(ContractAttachment attachment , ContractAttachmentData data) {
        this.id = attachment.getId();
        this.data = data.getData();
        this.contractId = attachment.getContract().getId();
        this.creationDate = data.getCreationDate();
        this.rejectedAt = attachment.getRejectedAt();
        this.acceptedAt = attachment.getAcceptedAt();
        this.changeDate = attachment.getChangeDate();
        this.status = attachment.getStatus();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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

    public ContractAttachmentStatus getStatus() {
        return status;
    }

    public void setStatus(ContractAttachmentStatus status) {
        this.status = status;
    }


}
