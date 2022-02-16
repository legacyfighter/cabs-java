package io.legacyfighter.cabs.party.infra;

import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.party.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class JpaPartyRepository implements PartyRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Party put(UUID id) {
        Party party = entityManager.find(Party.class, id);
        if (party == null){
            party = new Party();
            party.setId(id);
            entityManager.persist(party);
        }
        return party;
    }
}
