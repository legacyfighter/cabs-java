package io.legacyfighter.cabs.repair.model.roles.empty;

import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.role.PartyBasedRole;


public class Customer extends PartyBasedRole {
    public Customer(Party party) {
        super(party);
    }
}
