package io.legacyfighter.cabs.ride;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransitDemandRepository extends JpaRepository<TransitDemand, Long> {

    TransitDemand findByTransitRequestUUID(UUID requestUUID);
}

