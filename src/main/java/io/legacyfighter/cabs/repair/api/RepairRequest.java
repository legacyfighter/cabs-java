package io.legacyfighter.cabs.repair.api;

import io.legacyfighter.cabs.party.api.PartyId;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import java.util.Set;

public class RepairRequest {

    private PartyId vehicle;
    private Set<Parts> partsToRepair;

    public RepairRequest(PartyId vehicle, Set<Parts> parts) {
        this.vehicle = vehicle;
        this.partsToRepair = parts;
    }

    public Set<Parts> getPartsToRepair() {
        return partsToRepair;
    }

    public PartyId getVehicle() {
        return vehicle;
    }
}
