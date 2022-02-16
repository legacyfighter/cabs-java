package io.legacyfighter.cabs.party.model.role;

import io.legacyfighter.cabs.party.model.party.Party;

/**
 * TODO introduce interface for an abstract class
 */
public abstract class PartyBasedRole {
    protected Party party;

    public PartyBasedRole(Party party){
        this.party = party;
    }
}
