package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StubbedTransitPrice {

    private final TransitRepository transitRepository;

    public StubbedTransitPrice(TransitRepository transitRepository) {
        this.transitRepository = transitRepository;
    }

    @Transactional
    public Transit stub(Long transitId, Money faked) {
        Transit transit = transitRepository.getOne(transitId);
        transit.setPrice(faked);
        return transit;
    }
}
