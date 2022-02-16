package io.legacyfighter.cabs.repair.model.roles.assistance;

import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.role.PartyBasedRole;
import io.legacyfighter.cabs.repair.api.AssistanceRequest;

/**
 * Base class for all commands that are able to handle @{@link AssistanceRequest}
 */
public abstract class RoleForAssistance extends PartyBasedRole {
    public RoleForAssistance(Party party) {
        super(party);
    }

    public abstract void handle(AssistanceRequest request);
}
