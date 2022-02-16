package io.legacyfighter.cabs.repair.model.roles.empty;

import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.role.PartyBasedRole;

public class Insured extends PartyBasedRole {
    public Insured(Party party) {
        super(party);
    }
}
