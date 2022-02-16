package io.legacyfighter.cabs.party.model.party;

import java.util.UUID;

public interface PartyRepository {
    Party put(UUID id);
}
