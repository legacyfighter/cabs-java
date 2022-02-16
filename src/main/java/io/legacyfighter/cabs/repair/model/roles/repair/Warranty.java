package io.legacyfighter.cabs.repair.model.roles.repair;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.repair.api.RepairRequest;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import java.util.HashSet;
import java.util.Set;

public class Warranty extends RoleForRepairer {

    public Warranty(Party party) {
        super(party);
    }

    public RepairingResult handle(RepairRequest repairRequest) {
        Set<Parts> handledParts = new HashSet<>(repairRequest.getPartsToRepair());

        return new RepairingResult(party.getId(), Money.ZERO, handledParts);
    }
}
