package io.legacyfighter.cabs.party.api;

import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.party.PartyRelationship;
import io.legacyfighter.cabs.party.model.role.PartyBasedRole;
import io.legacyfighter.cabs.party.utils.PolymorphicHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * Sample impl based on Class-Instance map.
 * More advanced impls can be case on a DI container: getRole can obtain role instance from the container.
 *
 * TODO introduce an interface to convert to Abstract Factory Pattern to be able to choose factory impl
 */
public class RoleObjectFactory {

    Map<Class<? extends PartyBasedRole>, PartyBasedRole> roles = new PolymorphicHashMap<>();

    public boolean hasRole(Class<? extends PartyBasedRole> role){
        return roles.containsKey(role);
    }

    public static RoleObjectFactory from(PartyRelationship partyRelationship){
        RoleObjectFactory roleObject = new RoleObjectFactory();
        roleObject.add(partyRelationship);
        return roleObject;
    }

    private void add(PartyRelationship partyRelationship){
        add(partyRelationship.getRoleA(), partyRelationship.getPartyA());
        add(partyRelationship.getRoleB(), partyRelationship.getPartyB());
    }

    private void add(String role, Party party)  {
        try {
            //in sake of simplicity: a role name is same as a class name with no mapping between them
            Class<PartyBasedRole> clazz = (Class<PartyBasedRole>) Class.forName(role);
            PartyBasedRole instance = clazz.getConstructor(Party.class).newInstance(party);
            roles.put(clazz, instance);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    public <T extends PartyBasedRole> Optional<T> getRole(Class<T> role){
        return (Optional<T>) Optional.ofNullable(roles.get(role));
    }
}
