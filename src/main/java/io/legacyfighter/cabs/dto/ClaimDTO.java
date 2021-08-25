package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.entity.Claim;

import java.time.Instant;

public class ClaimDTO {

    private Long claimID;

    private Long clientId;

    private Long transitId;

    private String reason;

    private String incidentDescription;

    private boolean isDraft;

    private Instant creationDate;

    private Instant completionDate;

    private Instant changeDate;

    private Claim.CompletionMode completionMode;

    private Claim.Status status;

    private String claimNo;

    public ClaimDTO(Claim claim) {
        if (claim.getStatus().equals(Claim.Status.DRAFT)) {
            this.setDraft(true);
        } else {
            this.setDraft(false);
        }
        this.setClaimID(claim.getId());
        this.setReason(claim.getReason());
        this.setIncidentDescription(claim.getIncidentDescription());
        this.setTransitId(claim.getTransit().getId());
        this.setClientId(claim.getOwner().getId());
        this.setCompletionDate(claim.getCompletionDate());
        this.setChangeDate(claim.getChangeDate());
        this.setClaimNo(claim.getClaimNo());
        this.setStatus(claim.getStatus());
        this.setCompletionMode(claim.getCompletionMode());
        this.setCreationDate(claim.getCreationDate());
    }

    public ClaimDTO() {

    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Instant completionDate) {
        this.completionDate = completionDate;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    public Claim.CompletionMode getCompletionMode() {
        return completionMode;
    }

    public void setCompletionMode(Claim.CompletionMode completionMode) {
        this.completionMode = completionMode;
    }

    public Claim.Status getStatus() {
        return status;
    }

    public void setStatus(Claim.Status status) {
        this.status = status;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public Long getClaimID() {
        return claimID;
    }

    public void setClaimID(Long claimID) {
        this.claimID = claimID;
    }


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getTransitId() {
        return transitId;
    }

    public void setTransitId(Long transitId) {
        this.transitId = transitId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }
}
