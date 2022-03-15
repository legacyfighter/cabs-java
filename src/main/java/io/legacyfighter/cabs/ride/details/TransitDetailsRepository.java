package io.legacyfighter.cabs.ride.details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface TransitDetailsRepository extends JpaRepository<TransitDetails, Long> {

    TransitDetails findByRequestUUID(UUID requestUUID);

    List<TransitDetails> findByClientId(Long clientId);

    @Query("select TD from TransitDetails TD where TD.driverId = ?1 and TD.dateTime between ?2 and ?3")
    List<TransitDetails> findAllByDriverAndDateTimeBetween(Long driverID, Instant from, Instant to);

    List<TransitDetails> findByStatus(Status completed);

    TransitDetails findByTransitId(Long transitId);
}
