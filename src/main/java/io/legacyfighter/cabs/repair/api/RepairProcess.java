package io.legacyfighter.cabs.repair.api;

import io.legacyfighter.cabs.party.api.PartyMapper;
import io.legacyfighter.cabs.party.api.RoleObjectFactory;
import io.legacyfighter.cabs.party.model.party.PartyRelationship;
import io.legacyfighter.cabs.repair.model.dict.PartyRelationshipsDictionary;
import io.legacyfighter.cabs.repair.model.roles.repair.RepairingResult;
import io.legacyfighter.cabs.repair.model.roles.repair.RoleForRepairer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RepairProcess {

    private final PartyMapper partyMapper;

    @Autowired
    public RepairProcess(PartyMapper partyMapper){
        this.partyMapper = partyMapper;
    }

    public ResolveResult resolve(RepairRequest repairRequest) {
         return partyMapper.mapRelation(repairRequest.getVehicle(), PartyRelationshipsDictionary.REPAIR.name())
                .map(RoleObjectFactory::from)
                .flatMap(rof -> rof.getRole(RoleForRepairer.class))
                .map(role -> role.handle(repairRequest))
                .map(repairingResult -> new ResolveResult(ResolveResult.Status.SUCCESS, repairingResult.getHandlingParty(), repairingResult.getTotalCost(), repairingResult.getHandledParts()))
                .orElseGet(() -> new ResolveResult(ResolveResult.Status.ERROR));
    }

    public ResolveResult resolve_oldschool_version(RepairRequest repairRequest) {
        //who is responsible for repairing the vehicle
        Optional<PartyRelationship> relationship = partyMapper.mapRelation(repairRequest.getVehicle(), PartyRelationshipsDictionary.REPAIR.name());
        if (relationship.isPresent()){
            RoleObjectFactory roleObjectFactory = RoleObjectFactory.from(relationship.get());
            //dynamically assigned rules
            Optional<RoleForRepairer> role = roleObjectFactory.getRole(RoleForRepairer.class);
            if (role.isPresent()) {
                //actual repair request handling
                RepairingResult repairingResult = role.get().handle(repairRequest);
                return new ResolveResult(ResolveResult.Status.SUCCESS, repairingResult.getHandlingParty(), repairingResult.getTotalCost(), repairingResult.getHandledParts());
            }
        }
        return new ResolveResult(ResolveResult.Status.ERROR);
    }
}
