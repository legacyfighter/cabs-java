package io.legacyfighter.cabs.ride;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestForTransitRepository extends JpaRepository<RequestForTransit, Long> {

    RequestForTransit findByRequestUUID(UUID requestUUID);
}

