package io.legacyfighter.cabs.party.api;

import java.util.Objects;
import java.util.UUID;

public class PartyId {
    private UUID id;

    public PartyId(){
        this.id = UUID.randomUUID();
    }

    public PartyId(UUID id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartyId)) return false;
        PartyId baseId = (PartyId) o;
        return id.equals(baseId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UUID toUUID() {
        return id;
    }
}
