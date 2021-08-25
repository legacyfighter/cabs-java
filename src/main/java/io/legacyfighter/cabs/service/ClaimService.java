package io.legacyfighter.cabs.service;


import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.dto.ClaimDTO;
import io.legacyfighter.cabs.entity.Claim;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.repository.ClaimRepository;
import io.legacyfighter.cabs.repository.ClientRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

import static io.legacyfighter.cabs.entity.Claim.CompletionMode.AUTOMATIC;
import static io.legacyfighter.cabs.entity.Claim.CompletionMode.MANUAL;
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
        if(claimRepository.findByOwnerAndTransit(claim.getOwner(), claim.getTransit()).size() > 1) {
            claim.setStatus(ESCALATED);
            claim.setCompletionDate(Instant.now());
            claim.setChangeDate(Instant.now());
            claim.setCompletionMode(MANUAL);
            return claim;
        }
        if (claimRepository.findByOwner(claim.getOwner()).size() <= 3) {
            claim.setStatus(REFUNDED);
            claim.setCompletionDate(Instant.now());
            claim.setChangeDate(Instant.now());
            claim.setCompletionMode(AUTOMATIC);
            clientNotificationService.notifyClientAboutRefund(claim.getClaimNo(), claim.getOwner().getId());
            return claim;
        }
        if (claim.getOwner().getType().equals(Client.Type.VIP)) {
            if (claim.getTransit().getPrice().toInt() < appProperties.getAutomaticRefundForVipThreshold()) {
                claim.setStatus(REFUNDED);
                claim.setCompletionDate(Instant.now());
                claim.setChangeDate(Instant.now());
                claim.setCompletionMode(AUTOMATIC);
                clientNotificationService.notifyClientAboutRefund(claim.getClaimNo(), claim.getOwner().getId());
                awardsService.registerSpecialMiles(claim.getOwner().getId(), 10);
            } else {
                claim.setStatus(ESCALATED);
                claim.setCompletionDate(Instant.now());
                claim.setChangeDate(Instant.now());
                claim.setCompletionMode(MANUAL);
                driverNotificationService.askDriverForDetailsAboutClaim(claim.getClaimNo(), claim.getTransit().getDriver().getId());
            }
        } else {
            if (transitRepository.findByClient(claim.getOwner()).size() >= appProperties.getNoOfTransitsForClaimAutomaticRefund()) {
                if (claim.getTransit().getPrice().toInt() < appProperties.getAutomaticRefundForVipThreshold()) {
                    claim.setStatus(REFUNDED);
                    claim.setCompletionDate(Instant.now());
                    claim.setChangeDate(Instant.now());
                    claim.setCompletionMode(AUTOMATIC);
                    clientNotificationService.notifyClientAboutRefund(claim.getClaimNo(), claim.getOwner().getId());
                } else {
                    claim.setStatus(ESCALATED);
                    claim.setCompletionDate(Instant.now());
                    claim.setChangeDate(Instant.now());
                    claim.setCompletionMode(MANUAL);
                    clientNotificationService.askForMoreInformation(claim.getClaimNo(), claim.getOwner().getId());
                }
            } else {
                claim.setStatus(ESCALATED);
                claim.setCompletionDate(Instant.now());
                claim.setChangeDate(Instant.now());
                claim.setCompletionMode(MANUAL);
                driverNotificationService.askDriverForDetailsAboutClaim(claim.getClaimNo(), claim.getOwner().getId());
            }
        }

        return claim;
    }
}
