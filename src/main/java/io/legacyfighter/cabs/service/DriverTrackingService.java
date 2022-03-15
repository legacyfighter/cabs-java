package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance.TravelledDistanceService;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.entity.DriverPosition;
import io.legacyfighter.cabs.repository.DriverPositionRepository;
import io.legacyfighter.cabs.driverfleet.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DriverTrackingService {
    @Autowired
    private DriverPositionRepository positionRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TravelledDistanceService travelledDistanceService;

    @Transactional
    public DriverPosition registerPosition(Long driverId, double latitude, double longitude, Instant seenAt) {
        Driver driver = driverRepository.getOne(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exists, id = " + driverId);
        }
        if (!driver.getStatus().equals(Driver.Status.ACTIVE)) {
            throw new IllegalStateException("Driver is not active, cannot register position, id = " + driverId);
        }
        DriverPosition position = new DriverPosition();
        position.setDriver(driver);
        position.setSeenAt(seenAt);
        position.setLatitude(latitude);
        position.setLongitude(longitude);
        position = positionRepository.save(position);
        travelledDistanceService.addPosition(driverId, latitude, longitude, seenAt);
        return position;
    }

    public Distance calculateTravelledDistance(Long driverId, Instant from, Instant to) {
        Driver driver = driverRepository.getOne(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exists, id = " + driverId);
        }
        return travelledDistanceService.calculateDistance(driverId, from, to);
    }
}
