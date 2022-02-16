package io.legacyfighter.cabs.party.api;

import io.legacyfighter.cabs.party.model.party.PartyRelationship;
import io.legacyfighter.cabs.party.model.party.PartyRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PartyMapper {

    @Autowired
    private PartyRelationshipRepository partyRelationshipRepository;

    public Optional<PartyRelationship> mapRelation(PartyId id, String relationshipName) {
        return partyRelationshipRepository.findRelationshipFor(id, relationshipName);
    }
}
