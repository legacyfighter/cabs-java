package io.legacyfighter.cabs.repair.api;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import java.util.Set;
import java.util.UUID;

public class ResolveResult {


    public enum Status {
        SUCCESS, ERROR;
    }

    private UUID handlingParty;
    private Money totalCost;
    private Set<Parts> acceptedParts;
    private Status status;


    public ResolveResult(Status status, UUID handlingParty, Money totalCost, Set<Parts> acceptedParts) {
        this.status = status;
        this.handlingParty = handlingParty;
        this.totalCost = totalCost;
        this.acceptedParts = acceptedParts;
    }

    public ResolveResult(Status status) {
        this.status = status;
    }

    public UUID getHandlingParty() {
        return handlingParty;
    }

    public Money getTotalCost() {
        return totalCost;
    }

    public Status getStatus() {
        return status;
    }

    public Set<Parts> getAcceptedParts() {
        return acceptedParts;
    }
}
