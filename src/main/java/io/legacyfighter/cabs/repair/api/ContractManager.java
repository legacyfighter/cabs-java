package io.legacyfighter.cabs.repair.api;

import io.legacyfighter.cabs.party.api.PartyId;
import io.legacyfighter.cabs.party.model.party.Party;
import io.legacyfighter.cabs.party.model.party.PartyRelationshipRepository;
import io.legacyfighter.cabs.party.model.party.PartyRepository;
import io.legacyfighter.cabs.repair.model.dict.PartyRelationshipsDictionary;
import io.legacyfighter.cabs.repair.model.dict.PartyRolesDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContractManager {

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private PartyRelationshipRepository partyRelationshipRepository;

    public void extendedWarrantyContractSigned(PartyId insurerId, PartyId vehicleId){
        Party insurer = partyRepository.put(insurerId.toUUID());
        Party vehicle = partyRepository.put(vehicleId.toUUID());

        partyRelationshipRepository.put(PartyRelationshipsDictionary.REPAIR.toString(),
                PartyRolesDictionary.INSURER.getRoleName(), insurer,
                PartyRolesDictionary.INSURED.getRoleName(),  vehicle);
    }

    public void manufacturerWarrantyRegistered(PartyId distributorId, PartyId vehicleId) {
        Party distributor = partyRepository.put(distributorId.toUUID());
        Party vehicle = partyRepository.put(vehicleId.toUUID());

        partyRelationshipRepository.put(PartyRelationshipsDictionary.REPAIR.toString(),
                PartyRolesDictionary.GUARANTOR.getRoleName(), distributor,
                PartyRolesDictionary.CUSTOMER.getRoleName(),  vehicle);
    }
}
