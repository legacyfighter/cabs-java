package io.legacyfighter.cabs.tracking;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.driverfleet.DriverService;
import io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance.TravelledDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class DriverTrackingService {

    @Autowired
    private DriverPositionRepository positionRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TravelledDistanceService travelledDistanceService;

    @Autowired
    private DriverSessionService driverSessionService;

    @Autowired
    GeocodingService geocodingService;

    @Autowired
    private Clock clock;

    @Transactional
    public DriverPosition registerPosition(Long driverId, double latitude, double longitude, Instant seenAt) {
        DriverDTO driver = driverService.loadDriver(driverId);
        if (!driver.getStatus().equals(Driver.Status.ACTIVE)) {
            throw new IllegalStateException("Driver is not active, cannot register position, id = " + driverId);
        }
        DriverPosition position = new DriverPosition();
        position.setDriverId(driverId);
        position.setSeenAt(seenAt);
        position.setLatitude(latitude);
        position.setLongitude(longitude);
        position = positionRepository.save(position);
        travelledDistanceService.addPosition(driverId, latitude, longitude, seenAt);
        return position;
    }

    public Distance calculateTravelledDistance(Long driverId, Instant from, Instant to) {
        return travelledDistanceService.calculateDistance(driverId, from, to);
    }

    public List<DriverPositionDTOV2> findActiveDriversNearby(AddressDTO address, Distance distance, Collection<CarClass> carClasses) {
        double[] geocoded = new double[2];

        try {
            geocoded = geocodingService.geocodeAddress(address.toAddressEntity());
        } catch (Exception e) {
            // Geocoding failed! Ask Jessica or Bryan for some help if needed.
        }

        double longitude = geocoded[1];
        double latitude = geocoded[0];

        //https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
        //Earth’s radius, sphere
        //double R = 6378;
        double R = 6371; // Changed to 6371 due to Copy&Paste pattern from different source

        //offsets in meters
        double dn = distance.toKmInDouble();
        double de = distance.toKmInDouble();

        //Coordinate offsets in radians
        double dLat = dn / R;
        double dLon = de / (R * Math.cos(Math.PI * latitude / 180));

        //Offset positions, decimal degrees
        double latitudeMin = latitude - dLat * 180 / Math.PI;
        double latitudeMax = latitude + dLat *
                180 / Math.PI;
        double longitudeMin = longitude - dLon *
                180 / Math.PI;
        double longitudeMax = longitude + dLon * 180 / Math.PI;

        return findActiveDriversNearby(latitudeMin, latitudeMax, longitudeMin, longitudeMax, latitude, longitude, carClasses);
    }

    public List<DriverPositionDTOV2> findActiveDriversNearby(double latitudeMin, double latitudeMax, double longitudeMin, double longitudeMax, double latitude, double longitude, Collection<CarClass> carClasses) {
        List<DriverPositionDTOV2> driversAvgPositions = positionRepository
                .findAverageDriverPositionSince(latitudeMin, latitudeMax, longitudeMin, longitudeMax, Instant.now(clock).minus(5, ChronoUnit.MINUTES));

        Comparator<DriverPositionDTOV2> comparator = (DriverPositionDTOV2 d1, DriverPositionDTOV2 d2) -> Double.compare(
                Math.sqrt(Math.pow(latitude - d1.getLatitude(), 2) + Math.pow(longitude - d1.getLongitude(), 2)),
                Math.sqrt(Math.pow(latitude - d2.getLatitude(), 2) + Math.pow(longitude - d2.getLongitude(), 2))
        );
        driversAvgPositions.sort(comparator);
        driversAvgPositions = driversAvgPositions.stream().limit(20).collect(toList());
        List<Long> driversIds = driversAvgPositions.stream().map(DriverPositionDTOV2::getDriverId).collect(toList());
        List<Long> activeDriverIdsInSpecificCar = driverSessionService.findCurrentlyLoggedDriverIds(driversIds, carClasses);

        driversAvgPositions = driversAvgPositions
                .stream()
                .filter(dp -> activeDriverIdsInSpecificCar.contains(dp.getDriverId())).collect(toList());

        Map<Long, DriverDTO> drivers = driverService.loadDrivers(driversIds)
                .stream()
                .collect(toMap(DriverDTO::getId, dto -> dto));
        driversAvgPositions =
                driversAvgPositions
                        .stream()
                        .filter(dap -> {
                            DriverDTO d = drivers.get(dap.getDriverId());
                            return d.getStatus().equals(Driver.Status.ACTIVE) && !d.isOccupied();
                        })
                        .collect(Collectors.toList());
        return driversAvgPositions;
    }
}
