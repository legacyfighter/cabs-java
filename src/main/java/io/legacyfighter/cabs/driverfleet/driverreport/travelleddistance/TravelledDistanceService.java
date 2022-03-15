package io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance;


import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.DistanceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

import static io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance.TimeSlot.slotThatContains;

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
    public void addPosition(Long driverId, double latitude, double longitude, Instant seenAt) {
        TravelledDistance matchedSlot = travelledDistanceRepository.findTravelledDistanceTimeSlotByTime(seenAt, driverId);
        Instant now = clock.instant();
        if (matchedSlot != null) {
            if (matchedSlot.contains(now)) {
                addDistanceToSlot(matchedSlot, latitude, longitude);
            } else if (matchedSlot.isBefore(now)) {
                recalculateDistanceFor(matchedSlot, driverId);
            }
        } else {
            TimeSlot currentTimeSlot = slotThatContains(now);
            TimeSlot prev = currentTimeSlot.prev();
            TravelledDistance prevTravelledDistance = travelledDistanceRepository.findTravelledDistanceByTimeSlotAndDriverId(prev, driverId);
            if (prevTravelledDistance != null) {
                if (prevTravelledDistance.endsAt(seenAt)) {
                    addDistanceToSlot(prevTravelledDistance, latitude, longitude);
                }
            }
            createSlotForNow(driverId, currentTimeSlot, latitude, longitude);
        }
    }

    private void addDistanceToSlot(TravelledDistance aggregatedDistance, double latitude, double longitude) {
        Distance travelled = Distance.ofKm(distanceCalculator.calculateByGeo(
                latitude,
                longitude,
                aggregatedDistance.getLastLatitude(),
                aggregatedDistance.getLastLongitude()));
        aggregatedDistance.addDistance(travelled, latitude, longitude);
    }

    private void recalculateDistanceFor(TravelledDistance aggregatedDistance, Long driverId) {
        //TODO
    }

    private void createSlotForNow(Long driverId, TimeSlot timeSlot, double latitude, double longitude) {
        travelledDistanceRepository.save(new TravelledDistance(driverId, timeSlot, latitude, longitude));
    }

}
