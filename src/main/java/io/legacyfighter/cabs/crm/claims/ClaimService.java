package io.legacyfighter.cabs.crm.claims;


import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.crm.ClientRepository;
import io.legacyfighter.cabs.loyalty.AwardsService;
import io.legacyfighter.cabs.notification.ClientNotificationService;
import io.legacyfighter.cabs.notification.DriverNotificationService;
import io.legacyfighter.cabs.ride.details.TransitDetailsDTO;
import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static io.legacyfighter.cabs.crm.claims.Status.*;

@Service
public class ClaimService {

    @Autowired
    private Clock clock;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransitDetailsFacade transitDetailsFacade;
    @Autowired
    private ClaimRepository claimRepository;
    @Autowired
    private ClaimNumberGenerator claimNumberGenerator;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private AwardsService awardsService;
    @Autowired
    private ClientNotificationService clientNotificationService;
    @Autowired
    private DriverNotificationService driverNotificationService;
    @Autowired
    private ClaimsResolverRepository claimsResolverRepository;

    @Transactional
    public Claim create(ClaimDTO claimDTO) {
        Claim claim = new Claim();
        claim.setCreationDate(Instant.now(clock));
        claim.setClaimNo(claimNumberGenerator.generate(claim));
        claim = update(claimDTO, claim);
        return claim;
    }

    public Claim find(Long id) {
        Claim claim = claimRepository.getOne(id);

        if (claim == null) {
            throw new IllegalStateException("Claim does not exists");
        }
        return claim;
    }

    public Claim update(ClaimDTO claimDTO, Claim claim) {
        Client client = clientRepository.getOne(claimDTO.getClientId());
        TransitDetailsDTO transit = transitDetailsFacade.find(claimDTO.getTransitId());
        if (client == null) {
            throw new IllegalStateException("Client does not exists");
        }
        if (transit == null) {
            throw new IllegalStateException("Transit does not exists");
        }
        if (claimDTO.isDraft()) {
            claim.setStatus(DRAFT);
        } else {
            claim.setStatus(NEW);
        }
        claim.setOwnerId(client.getId());
        claim.setTransit(transit.transitId);
        claim.setTransitPrice(transit.price);
        claim.setCreationDate(Instant.now(clock));
        claim.setReason(claimDTO.getReason());
        claim.setIncidentDescription(claimDTO.getIncidentDescription());
        return claimRepository.save(claim);
    }


    @Transactional
    public Claim setStatus(Status newStatus, Long id) {
        Claim claim = find(id);
        claim.setStatus(newStatus);
        return claim;
    }

    @Transactional
    public Claim tryToResolveAutomatically(Long id) {
        Claim claim = find(id);

        ClaimsResolver claimsResolver = findOrCreateResolver(claim.getOwnerId());
        List<TransitDetailsDTO> transitsDoneByClient = transitDetailsFacade.findByClient(claim.getOwnerId());
        Client.Type clientType = clientRepository.getOne(claim.getOwnerId()).getType();
        ClaimsResolver.Result result = claimsResolver.resolve(claim, clientType, appProperties.getAutomaticRefundForVipThreshold(), transitsDoneByClient.size(), appProperties.getNoOfTransitsForClaimAutomaticRefund());
        if (result.decision == REFUNDED) {
            claim.refund();
            clientNotificationService.notifyClientAboutRefund(claim.getClaimNo(), claim.getOwnerId());
            if (clientType.equals(Client.Type.VIP)) {
                awardsService.registerNonExpiringMiles(claim.getOwnerId(), 10);
            }
        }
        if (result.decision == ESCALATED) {
            claim.escalate();
        }
        if (result.whoToAsk == ClaimsResolver.WhoToAsk.ASK_DRIVER) {
            TransitDetailsDTO transitDetailsDTO = transitDetailsFacade.find(claim.getTransitId());
            driverNotificationService.askDriverForDetailsAboutClaim(claim.getClaimNo(), transitDetailsDTO.driverId);
        }
        if (result.whoToAsk == ClaimsResolver.WhoToAsk.ASK_CLIENT) {
            clientNotificationService.askForMoreInformation(claim.getClaimNo(), claim.getOwnerId());
        }
        return claim;
    }

    private ClaimsResolver findOrCreateResolver(Long clientId) {
        ClaimsResolver resolver = claimsResolverRepository.findByClientId(clientId);
        if (resolver == null) {
            resolver = claimsResolverRepository.save(new ClaimsResolver(clientId));
        }
        return resolver;
    }

    public Integer getNumberOfClaims(Long clientId) {
        return claimRepository.findAllByOwnerId(clientId).size();
    }
}
