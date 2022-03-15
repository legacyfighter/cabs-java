package io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.UUID;

interface TravelledDistanceRepository extends CrudRepository<TravelledDistance, UUID> {

    @Query("select td from TravelledDistance td where td.timeSlot.beginning <= :when and :when < td.timeSlot.end and td.driverId = :driverId")
    TravelledDistance findTravelledDistanceTimeSlotByTime(Instant when, Long driverId);

    TravelledDistance findTravelledDistanceByTimeSlotAndDriverId(TimeSlot timeSlot, Long driverId);

    @Query(value = "SELECT COALESCE(SUM(_inner.km), 0) FROM " +
            "( (SELECT * FROM travelled_distance td WHERE td.beginning >= :beginning AND td.driver_id = :driverId)) " +
            "AS _inner WHERE end <= :to ", nativeQuery = true)
    double calculateDistance(Instant beginning, Instant to, Long driverId);

}

