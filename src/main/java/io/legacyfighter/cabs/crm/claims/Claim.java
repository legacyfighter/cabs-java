package io.legacyfighter.cabs.crm.claims;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.*;
import java.time.Instant;

import static io.legacyfighter.cabs.crm.claims.Claim.CompletionMode.AUTOMATIC;
import static io.legacyfighter.cabs.crm.claims.Claim.CompletionMode.MANUAL;
import static io.legacyfighter.cabs.crm.claims.Status.ESCALATED;
import static io.legacyfighter.cabs.crm.claims.Status.REFUNDED;


@Entity
public class Claim extends BaseEntity {

    public enum CompletionMode {
        MANUAL, AUTOMATIC
    }

    public Claim() {
    }

    private Long ownerId;

    private Long transitId;

    @Column(nullable = false)
    private Instant creationDate;

    private Instant completionDate;

    private Instant changeDate;

    @Column(nullable = false)
    private String reason;

    private String incidentDescription;

    @Enumerated(EnumType.STRING)
    private CompletionMode completionMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String claimNo;

    @Embedded
    private Money transitPrice;

    void escalate() {
        setStatus(ESCALATED);
        setCompletionDate(Instant.now());
        setChangeDate(Instant.now());
        setCompletionMode(MANUAL);
    }

    void refund() {
        setStatus(REFUNDED);
        setCompletionDate(Instant.now());
        setChangeDate(Instant.now());
        setCompletionMode(AUTOMATIC);
    }

    public String getClaimNo() {
        return claimNo;
    }

    void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }


    public Long getOwnerId() {
        return ownerId;
    }

    void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getTransitId() {
        return transitId;
    }

    void setTransit(Long transitId) {
        this.transitId = transitId;
    }

    Money getTransitPrice() {
        return transitPrice;
    }

    void setTransitPrice(Money transitPrice) {
        this.transitPrice = transitPrice;
    }


    Instant getCreationDate() {
        return creationDate;
    }

    void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    Instant getCompletionDate() {
        return completionDate;
    }

    void setCompletionDate(Instant completionDate) {
        this.completionDate = completionDate;
    }

    String getIncidentDescription() {
        return incidentDescription;
    }

    void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public CompletionMode getCompletionMode() {
        return completionMode;
    }

    void setCompletionMode(CompletionMode completionMode) {
        this.completionMode = completionMode;
    }

    public Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    Instant getChangeDate() {
        return changeDate;
    }

    void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    String getReason() {
        return reason;
    }

    void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Claim))
            return false;

        Claim other = (Claim) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }

}
