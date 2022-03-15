package io.legacyfighter.cabs.transitdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

interface TransitDetailsRepository extends JpaRepository<TransitDetails, Long> {

    TransitDetails findByTransitId(Long transitId);

    List<TransitDetails> findByClientId(Long clientId);

    @Query("select TD from TransitDetails TD where TD.driverId = ?1 and TD.dateTime between ?2 and ?3")
    List<TransitDetails> findAllByDriverAndDateTimeBetween(Long driverID, Instant from, Instant to);

}
