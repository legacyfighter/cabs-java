package io.legacyfighter.cabs.party.model.party;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class PartyRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String roleA;//String in sake of simplicity, each domain will use own ENUM

    private String roleB;//String in sake of simplicity, each domain will use own ENUM

    @ManyToOne
    private Party partyA;

    @ManyToOne
    private Party partyB;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleA() {
        return roleA;
    }

    public void setRoleA(String roleA) {
        this.roleA = roleA;
    }

    public String getRoleB() {
        return roleB;
    }

    public void setRoleB(String roleB) {
        this.roleB = roleB;
    }

    public Party getPartyA() {
        return partyA;
    }

    public void setPartyA(Party partyA) {
        this.partyA = partyA;
    }

    public Party getPartyB() {
        return partyB;
    }

    public void setPartyB(Party partyB) {
        this.partyB = partyB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyRelationship that = (PartyRelationship) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
