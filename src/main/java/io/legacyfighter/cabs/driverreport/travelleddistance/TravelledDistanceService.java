package io.legacyfighter.cabs.driverreport.travelleddistance;


import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.entity.DriverPosition;
import io.legacyfighter.cabs.service.DistanceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

import static io.legacyfighter.cabs.driverreport.travelleddistance.TimeSlot.slotThatContains;

@Service
public class TravelledDistanceService {

    private final Clock clock;
    private final TravelledDistanceRepository travelledDistanceRepository;
    private final DistanceCalculator distanceCalculator;

    TravelledDistanceService(Clock clock, TravelledDistanceRepository travelledDistanceRepository, DistanceCalculator distanceCalculator) {
        this.clock = clock;
        this.travelledDistanceRepository = travelledDistanceRepository;
        this.distanceCalculator = distanceCalculator;
    }

    public Distance calculateDistance(Long driverId, Instant from, Instant to) {
        TimeSlot left = slotThatContains(from);
        TimeSlot right = slotThatContains(to);
        return Distance.ofKm(travelledDistanceRepository.calculateDistance(left.beginning(), right.end(), driverId));
    }

    @Transactional
    public void addPosition(DriverPosition driverPosition) {
        Long driverId = driverPosition.getDriver().getId();
        TravelledDistance matchedSlot = travelledDistanceRepository.findTravelledDistanceTimeSlotByTime(driverPosition.getSeenAt(), driverId);
        Instant now = clock.instant();
        if (matchedSlot != null) {
            if (matchedSlot.contains(now)) {
                addDistanceToSlot(driverPosition, matchedSlot);
            } else if (matchedSlot.isBefore(now)) {
                recalculateDistanceFor(matchedSlot, driverId);
            }
        } else {
            TimeSlot currentTimeSlot = slotThatContains(now);
            TimeSlot prev = currentTimeSlot.prev();
            TravelledDistance prevTravelledDistance = travelledDistanceRepository.findTravelledDistanceByTimeSlotAndDriverId(prev, driverId);
            if (prevTravelledDistance != null) {
                if (prevTravelledDistance.endsAt(driverPosition.getSeenAt())) {
                    addDistanceToSlot(driverPosition, prevTravelledDistance);
                }
            }
            createSlotForNow(driverPosition, driverId, currentTimeSlot);
        }
    }

    private void addDistanceToSlot(DriverPosition driverPosition, TravelledDistance aggregatedDistance) {
        Distance travelled = Distance.ofKm(distanceCalculator.calculateByGeo(
                driverPosition.getLatitude(),
                driverPosition.getLongitude(),
                aggregatedDistance.getLastLatitude(),
                aggregatedDistance.getLastLongitude()));
        aggregatedDistance.addDistance(travelled, driverPosition.getLatitude(), driverPosition.getLongitude());
    }

    private void recalculateDistanceFor(TravelledDistance aggregatedDistance, Long driverId) {
        //TODO
    }

    private void createSlotForNow(DriverPosition driverPosition, Long driverId, TimeSlot timeSlot) {
        travelledDistanceRepository.save(new TravelledDistance(driverId, timeSlot, driverPosition));
    }

}
