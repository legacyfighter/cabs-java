package io.legacyfighter.cabs.party.model.party;

import io.legacyfighter.cabs.party.api.PartyId;

import java.util.Optional;

public interface PartyRelationshipRepository {
    PartyRelationship put(String partyRelationship, String partyARole, Party partyA, String partyBRole, Party partyB);
    Optional<PartyRelationship> findRelationshipFor(PartyId id, String relationshipName);
}
