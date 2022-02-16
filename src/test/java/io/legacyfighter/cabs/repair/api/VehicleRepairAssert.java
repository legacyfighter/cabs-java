package io.legacyfighter.cabs.repair.api;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.party.api.PartyId;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehicleRepairAssert {
    private ResolveResult result;

    public VehicleRepairAssert(ResolveResult result) {
        this(result, true);
    }

    public VehicleRepairAssert(ResolveResult result, boolean demandSuccess) {
        this.result = result;
        if (demandSuccess)
            assertEquals(ResolveResult.Status.SUCCESS, result.getStatus());
        else
            assertEquals(ResolveResult.Status.ERROR, result.getStatus());
    }

    public VehicleRepairAssert free() {
        assertEquals(Money.ZERO, result.getTotalCost());
        return this;
    }

    public VehicleRepairAssert allParts(Set<Parts> parts) {
        assertEquals(parts, result.getAcceptedParts());
        return this;
    }

    public VehicleRepairAssert by(PartyId handlingParty) {
        assertEquals(handlingParty.toUUID(), result.getHandlingParty());
        return this;
    }

    public VehicleRepairAssert allPartsBut(Set<Parts> parts, Parts[] excludedParts) {
        Set<Parts> exptectedParts = new HashSet<>(parts);
        exptectedParts.removeAll(Arrays.stream(excludedParts).collect(Collectors.toSet()));

        assertEquals(exptectedParts, result.getAcceptedParts());
        return this;
    }
}
