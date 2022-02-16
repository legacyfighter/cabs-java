package io.legacyfighter.cabs.repair.model.roles.repair;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import java.util.Set;
import java.util.UUID;

public class RepairingResult {
    private final UUID handlingParty;
    private final Money totalCost;
    private final Set<Parts> handledParts;

    public RepairingResult(UUID handlingParty, Money totalCost, Set<Parts> handledParts) {
        this.handlingParty = handlingParty;
        this.totalCost = totalCost;
        this.handledParts = handledParts;
    }

    public UUID getHandlingParty() {
        return handlingParty;
    }

    public Money getTotalCost() {
        return totalCost;
    }

    public Set<Parts> getHandledParts() {
        return handledParts;
    }
}
