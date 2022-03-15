package io.legacyfighter.cabs.ride;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DemandService {

    private final TransitDemandRepository transitDemandRepository;

    public DemandService(TransitDemandRepository transitDemandRepository) {
        this.transitDemandRepository = transitDemandRepository;
    }

    @Transactional
    public void publishDemand(UUID requestUUID) {
        transitDemandRepository.save(new TransitDemand(requestUUID));
    }

    @Transactional
    public void cancelDemand(UUID requestUUID) {
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);
        if (transitDemand != null) {
            transitDemand.cancel();
        }
    }

    @Transactional
    public void acceptDemand(UUID requestUUID) {
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);
        if (transitDemand != null) {
            transitDemand.accept();
        }
    }

    public boolean existsFor(UUID requestUUID) {
        return transitDemandRepository.findByTransitRequestUUID(requestUUID) != null;
    }
}
