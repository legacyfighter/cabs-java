package io.legacyfighter.cabs.crm.claims;

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

    private Status status;

    public ClaimDTO(Long claimID, Long clientId, Long transitId, String reason, String incidentDescription, Instant creationDate, Instant completionDate, Instant changeDate, Claim.CompletionMode completionMode, Status status, String claimNo) {
        this.claimID = claimID;
        this.clientId = clientId;
        this.transitId = transitId;
        this.reason = reason;
        this.incidentDescription = incidentDescription;
        this.isDraft = status.equals(Status.DRAFT);
        this.creationDate = creationDate;
        this.completionDate = completionDate;
        this.changeDate = changeDate;
        this.completionMode = completionMode;
        this.status = status;
        this.claimNo = claimNo;
    }

    private String claimNo;

    public ClaimDTO(Claim claim) {
        this(claim.getId(), claim.getOwnerId(), claim.getTransitId(), claim.getReason(),
                claim.getIncidentDescription(), claim.getCreationDate(),
                claim.getCompletionDate(), claim.getChangeDate(), claim.getCompletionMode(), claim.getStatus(), claim.getClaimNo());
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
