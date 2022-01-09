package io.legacyfighter.cabs.service;


import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.dto.ClaimDTO;
import io.legacyfighter.cabs.entity.Claim;
import io.legacyfighter.cabs.entity.ClaimsResolver;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.repository.ClaimRepository;
import io.legacyfighter.cabs.repository.ClaimsResolverRepository;
import io.legacyfighter.cabs.repository.ClientRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static io.legacyfighter.cabs.entity.Claim.Status.ESCALATED;
import static io.legacyfighter.cabs.entity.Claim.Status.REFUNDED;

@Service
public class ClaimService {

    @Autowired
    private Clock clock;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransitRepository transitRepository;
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
        Transit transit = transitRepository.getOne(claimDTO.getTransitId());
        if (client == null) {
            throw new IllegalStateException("Client does not exists");
        }
        if (transit == null) {
            throw new IllegalStateException("Transit does not exists");
        }
        if (claimDTO.isDraft()) {
            claim.setStatus(Claim.Status.DRAFT);
        } else {
            claim.setStatus(Claim.Status.NEW);
        }
        claim.setOwner(client);
        claim.setTransit(transit);
        claim.setCreationDate(Instant.now(clock));
        claim.setReason(claimDTO.getReason());
        claim.setIncidentDescription(claimDTO.getIncidentDescription());
        return claimRepository.save(claim);
    }


    @Transactional
    public Claim setStatus(Claim.Status newStatus, Long id) {
        Claim claim = find(id);
        claim.setStatus(newStatus);
        return claim;
    }

    @Transactional
    public Claim tryToResolveAutomatically(Long id) {
        Claim claim = find(id);

        ClaimsResolver claimsResolver = findOrCreateResolver(claim.getOwner());
        List<Transit> transitsDoneByClient = transitRepository.findByClient(claim.getOwner());
        ClaimsResolver.Result result = claimsResolver.resolve(claim, appProperties.getAutomaticRefundForVipThreshold(), transitsDoneByClient.size(), appProperties.getNoOfTransitsForClaimAutomaticRefund());

        if (result.decision == REFUNDED) {
            claim.refund();
            clientNotificationService.notifyClientAboutRefund(claim.getClaimNo(), claim.getOwner().getId());
            if (claim.getOwner().getType().equals(Client.Type.VIP)) {
                awardsService.registerSpecialMiles(claim.getOwner().getId(), 10);
            }
        }
        if (result.decision == ESCALATED) {
            claim.escalate();
        }
        if (result.whoToAsk == ClaimsResolver.WhoToAsk.ASK_DRIVER) {
            driverNotificationService.askDriverForDetailsAboutClaim(claim.getClaimNo(), claim.getTransit().getDriver().getId());
        }
        if (result.whoToAsk == ClaimsResolver.WhoToAsk.ASK_CLIENT) {
            clientNotificationService.askForMoreInformation(claim.getClaimNo(), claim.getOwner().getId());
        }
        return claim;
    }

    private ClaimsResolver findOrCreateResolver(Client client) {
        ClaimsResolver resolver = claimsResolverRepository.findByClientId(client.getId());
        if (resolver == null) {
            resolver = claimsResolverRepository.save(new ClaimsResolver(client.getId()));
        }
        return resolver;
    }
}
