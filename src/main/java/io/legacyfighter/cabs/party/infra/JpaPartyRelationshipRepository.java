package io.legacyfighter.cabs.party.infra;

import io.legacyfighter.cabs.party.api.PartyId;
import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.party.PartyRelationship;
import io.legacyfighter.cabs.party.model.party.PartyRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaPartyRelationshipRepository implements PartyRelationshipRepository {

    @Autowired
    private EntityManager entityManager;


    @Override
    public PartyRelationship put(String partyRelationship, String partyARole, Party partyA, String partyBRole, Party partyB){
        List<PartyRelationship> parties = entityManager.createQuery("SELECT r FROM PartyRelationship r " +
                "WHERE r.name = :name " +
                "AND (" +
                    "(r.partyA = :partyA AND r.partyB = :partyB) " +
                    "OR " +
                    "(r.partyA = : partyB AND r.partyB = :partyA)" +
                ")")

                .setParameter("name", partyRelationship)
                .setParameter("partyA", partyA)
                .setParameter("partyB", partyB)
                .getResultList();
        PartyRelationship relationship;

        if (parties.size() == 0){
            relationship = new PartyRelationship();
            entityManager.persist(relationship);
        }
        else{
            relationship = parties.get(0);
        }

        relationship.setName(partyRelationship);
        relationship.setPartyA(partyA);
        relationship.setPartyB(partyB);
        relationship.setRoleA(partyARole);
        relationship.setRoleB(partyBRole);

        return relationship;
    }

    @Override
    public Optional<PartyRelationship> findRelationshipFor(PartyId id, String relationshipName) {
        List<PartyRelationship> parties = entityManager.createQuery("SELECT r FROM PartyRelationship r " +
                        "WHERE r.name = :name " +
                        "AND " +
                        "(r.partyA.id = :id OR r.partyB.id = :id)")
                .setParameter("name", relationshipName)
                .setParameter("id", id.toUUID())
                .getResultList();
        if (parties.size() == 0)
            return Optional.empty();
        return Optional.of(parties.get(0));
    }
}
