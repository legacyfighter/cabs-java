package io.legacyfighter.cabs.ride;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class StartTransitService {

    @Autowired
    private TransitRepository transitRepository;

    @Autowired
    private RequestTransitService requestTransitService;

    @Transactional
    public Transit start(UUID requestUUID) {
        Transit transit = new Transit(requestTransitService.findTariff(requestUUID), requestUUID);
        return transitRepository.save(transit);
    }
}
