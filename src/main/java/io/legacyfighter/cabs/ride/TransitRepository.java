package io.legacyfighter.cabs.ride;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransitRepository extends JpaRepository<Transit, Long> {

    Transit findByTransitRequestUUID(UUID transitRequestUUID);

    @Query("select T from Transit T join TransitDetails TD ON T.transitRequestUUID = TD.requestUUID where TD.client.id = ?1")
    List<Transit> findByClientId(Long clientId);
}
