package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.crm.claims.ClaimDTO;
import io.legacyfighter.cabs.crm.claims.Claim;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.ride.TransitDTO;
import io.legacyfighter.cabs.ride.Transit;
import io.legacyfighter.cabs.crm.claims.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ClaimFixture {

    @Autowired
    ClaimService claimService;

    @Autowired
    ClientFixture clientFixture;

    Claim createClaim(Client client, Transit transit) {
        ClaimDTO claimDTO = claimDto("Okradli mnie na hajs", "$$$", client.getId(), transit.getId());
        claimDTO.setDraft(false);
        Claim claim = claimService.create(claimDTO);
        return claim;
    }

    Claim createClaim(Client client, TransitDTO transit, String reason) {
        ClaimDTO claimDTO = claimDto("Okradli mnie na hajs", reason, client.getId(), transit.getId());
        claimDTO.setDraft(false);
        return claimService.create(claimDTO);
    }

    Claim createAndResolveClaim(Client client, Transit transit) {
        Claim claim = createClaim(client, transit);
        claim = claimService.tryToResolveAutomatically(claim.getId());
        return claim;
    }

    ClaimDTO claimDto(String desc, String reason, Long clientId, Long transitId) {
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setClientId(clientId);
        claimDTO.setTransitId(transitId);
        claimDTO.setIncidentDescription(desc);
        claimDTO.setReason(reason);
        return claimDTO;
    }

}
