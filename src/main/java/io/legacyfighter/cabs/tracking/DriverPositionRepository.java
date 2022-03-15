package io.legacyfighter.cabs.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface DriverPositionRepository extends JpaRepository<DriverPosition, Long> {

    @Query(value = "SELECT new io.legacyfighter.cabs.tracking.DriverPositionDTOV2(p.driverId, avg(p.latitude), avg(p.longitude), max(p.seenAt)) FROM DriverPosition p where p.latitude between ?1 and ?2 and p.longitude between ?3 and ?4 and p.seenAt >= ?5 group by p.driverId")
    List<DriverPositionDTOV2> findAverageDriverPositionSince(double latitudeMin, double latitudeMax, double longitudeMin, double longitudeMax, Instant date);
}
