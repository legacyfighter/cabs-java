package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.common.EventsPublisher;
import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.DriverPositionDTOV2;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.entity.events.TransitCompleted;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.toList;

// If this class will still be here in 2022 I will quit.
@Service
public class TransitService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TransitRepository transitRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Autowired
    private DriverNotificationService notificationService;

    @Autowired
    private DistanceCalculator distanceCalculator;

    @Autowired
    private DriverPositionRepository driverPositionRepository;

    @Autowired
    private DriverSessionRepository driverSessionRepository;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DriverFeeService driverFeeService;

    @Autowired
    private Clock clock;

    @Autowired
    private AwardsService awardsService;

    @Autowired
    private EventsPublisher eventsPublisher;

    @Transactional
    public Transit createTransit(TransitDTO transitDTO) {
        Address from = addressFromDto(transitDTO.getFrom());
        Address to = addressFromDto(transitDTO.getTo());
        return createTransit(transitDTO.getClientDTO().getId(), from, to, transitDTO.getCarClass());
    }

    private Address addressFromDto(AddressDTO addressDTO) {
        Address address = addressDTO.toAddressEntity();
        return addressRepository.save(address);

    }

    @Transactional
    public Transit createTransit(Long clientId, Address from, Address to, CarType.CarClass carClass) {
        Client client = clientRepository.getOne(clientId);

        if (client == null) {
            throw new IllegalArgumentException("Client does not exist, id = " + clientId);
        }

        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(from);
        double[] geoTo = geocodingService.geocodeAddress(to);
        Distance km = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        Transit transit = new Transit(from, to, client, carClass, Instant.now(clock), km);
        transit.estimateCost();
        return transitRepository.save(transit);
    }

    @Transactional
    public void changeTransitAddressFrom(Long transitId, Address newAddress) {
        newAddress = addressRepository.save(newAddress);
        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }

        // FIXME later: add some exceptions handling
        double[] geoFromNew = geocodingService.geocodeAddress(newAddress);
        double[] geoFromOld = geocodingService.geocodeAddress(transit.getFrom());

        // https://www.geeksforgeeks.org/program-distance-two-points-earth/
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double lon1 = Math.toRadians(geoFromNew[1]);
        double lon2 = Math.toRadians(geoFromOld[1]);
        double lat1 = Math.toRadians(geoFromNew[0]);
        double lat2 = Math.toRadians(geoFromOld[0]);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956 for miles
        double r = 6371;

        // calculate the result
        double distanceInKMeters = c * r;

        Distance newDistance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFromNew[0], geoFromNew[1], geoFromOld[0], geoFromOld[1]));
        transit.changePickupTo(newAddress, newDistance, distanceInKMeters);
        transitRepository.save(transit);

        for (Driver driver : transit.getProposedDrivers()) {
            notificationService.notifyAboutChangedTransitAddress(driver.getId(), transitId);
        }
    }

    @Transactional
    public void changeTransitAddressTo(Long transitId, AddressDTO newAddress) {
        changeTransitAddressTo(transitId, newAddress.toAddressEntity());
    }

    @Transactional
    public void changeTransitAddressFrom(Long transitId, AddressDTO newAddress) {
        changeTransitAddressFrom(transitId, newAddress.toAddressEntity());
    }

    @Transactional
    public void changeTransitAddressTo(Long transitId, Address newAddress) {
        addressRepository.save(newAddress);
        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }

        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(transit.getFrom());
        double[] geoTo = geocodingService.geocodeAddress(newAddress);

        Distance newDistance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        transit.changeDestinationTo(newAddress, newDistance);

        if (transit.getDriver() != null) {
            notificationService.notifyAboutChangedTransitAddress(transit.getDriver().getId(), transitId);
        }
    }

    @Transactional
    public void cancelTransit(Long transitId) {
        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }

        if (transit.getDriver() != null) {
            notificationService.notifyAboutCancelledTransit(transit.getDriver().getId(), transitId);
        }

        transit.cancel();
        transitRepository.save(transit);
    }

    @Transactional
    public Transit publishTransit(Long transitId) {
        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }

        transit.publishAt(Instant.now(clock));
        transitRepository.save(transit);
        return findDriversForTransit(transitId);
    }

    // Abandon hope all ye who enter here...
    @Transactional
    public Transit findDriversForTransit(Long transitId) {
        Transit transit = transitRepository.getOne(transitId);

        if (transit != null) {
            if (transit.getStatus()
                    .equals(Transit.Status.WAITING_FOR_DRIVER_ASSIGNMENT)) {


                Integer distanceToCheck = 0;

                // Tested on production, works as expected.
                // If you change this code and the system will collapse AGAIN, I'll find you...
                while (true) {
                    if (transit.getAwaitingDriversResponses()
                            > 4) {
                        return transit;
                    }

                    distanceToCheck++;

                    // FIXME: to refactor when the final business logic will be determined
                    if (transit.shouldNotWaitForDriverAnyMore(Instant.now(clock)) || distanceToCheck >= 20) {
                        transit.failDriverAssignment();
                        transitRepository.save(transit);
                        return transit;
                    }
                    double[] geocoded = new double[2];


                    try {
                        geocoded = geocodingService.geocodeAddress(transit.getFrom());
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
                    double dn = distanceToCheck;
                    double de = distanceToCheck;

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

                    List<DriverPositionDTOV2> driversAvgPositions = driverPositionRepository
                            .findAverageDriverPositionSince(latitudeMin, latitudeMax, longitudeMin, longitudeMax, Instant.now(clock).minus(5, ChronoUnit.MINUTES));

                    if (!driversAvgPositions.isEmpty()) {
                        Comparator<DriverPositionDTOV2> comparator = (DriverPositionDTOV2 d1, DriverPositionDTOV2 d2) -> Double.compare(
                                Math.sqrt(Math.pow(latitude - d1.getLatitude(), 2) + Math.pow(longitude - d1.getLongitude(), 2)),
                                Math.sqrt(Math.pow(latitude - d2.getLatitude(), 2) + Math.pow(longitude - d2.getLongitude(), 2))
                        );
                        driversAvgPositions.sort(comparator);
                        driversAvgPositions = driversAvgPositions.stream().limit(20).collect(toList());

                        List<CarType.CarClass> carClasses = new ArrayList<>();
                        List<CarType.CarClass> activeCarClasses = carTypeService.findActiveCarClasses();
                        if (activeCarClasses.isEmpty()) {
                            return transit;
                        }
                        if (transit.getCarType()

                                != null) {
                            if (activeCarClasses.contains(transit.getCarType())) {
                                carClasses.add(transit.getCarType());
                            } else {
                                return transit;
                            }
                        } else {
                            carClasses.addAll(activeCarClasses);
                        }

                        List<Driver> drivers = driversAvgPositions.stream().map(DriverPositionDTOV2::getDriver).collect(toList());

                        List<Long> activeDriverIdsInSpecificCar = driverSessionRepository.findAllByLoggedOutAtNullAndDriverInAndCarClassIn(drivers, carClasses)

                                .stream()
                                .map(ds -> ds.getDriver().getId()).collect(toList());

                        driversAvgPositions = driversAvgPositions
                                .stream()
                                .filter(dp -> activeDriverIdsInSpecificCar.contains(dp.getDriver().getId())).collect(toList());

                        // Iterate across average driver positions
                        for (DriverPositionDTOV2 driverAvgPosition : driversAvgPositions) {
                            Driver driver = driverAvgPosition.getDriver();
                            if (driver.getStatus().equals(Driver.Status.ACTIVE) &&
                                    driver.getOccupied() == false) {
                                if (transit.canProposeTo(driver)) {
                                    transit.proposeTo(driver);
                                    notificationService.notifyAboutPossibleTransit(driver.getId(), transitId);
                                }
                            } else {
                                // Not implemented yet!
                            }
                        }

                        transitRepository.save(transit);

                    } else {
                        // Next iteration, no drivers at specified area
                        continue;
                    }
                }
            } else {
                throw new IllegalStateException("..., id = " + transitId);
            }
        } else {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }

    }

    @Transactional
    public void acceptTransit(Long driverId, Long transitId) {
        Driver driver = driverRepository.getOne(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        } else {
            Transit transit = transitRepository.getOne(transitId);

            if (transit == null) {
                throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
            } else {
                transit.acceptBy(driver, Instant.now(clock));
                transitRepository.save(transit);
                driverRepository.save(driver);
            }
        }
    }


    @Transactional
    public void startTransit(Long driverId, Long transitId) {
        Driver driver = driverRepository.getOne(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }

        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }
        transit.start(Instant.now(clock));
        transitRepository.save(transit);
    }

    @Transactional
    public void rejectTransit(Long driverId, Long transitId) {
        Driver driver = driverRepository.getOne(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }

        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }

        transit.rejectBy(driver);
        transitRepository.save(transit);
    }

    @Transactional
    public void completeTransit(Long driverId, Long transitId, AddressDTO destinationAddress) {
        completeTransit(driverId, transitId, destinationAddress.toAddressEntity());
    }

    @Transactional
    public void completeTransit(Long driverId, Long transitId, Address destinationAddress) {
        destinationAddress = addressRepository.save(destinationAddress);
        Driver driver = driverRepository.getOne(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }

        Transit transit = transitRepository.getOne(transitId);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitId);
        }


        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(transit.getFrom());
        double[] geoTo = geocodingService.geocodeAddress(transit.getTo());
        Distance distance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        transit.completeAt(Instant.now(clock), destinationAddress, distance);
        Money driverFee = driverFeeService.calculateDriverFee(transitId);
        transit.setDriversFee(driverFee);
        driver.setOccupied(false);
        driverRepository.save(driver);
        awardsService.registerMiles(transit.getClient().getId(), transitId);
        transitRepository.save(transit);
        invoiceGenerator.generate(transit.getPrice().toInt(), transit.getClient().getName() + " " + transit.getClient().getLastName());
        eventsPublisher.publish(new TransitCompleted(
                transit.getClient().getId(), transitId, transit.getFrom().getHash(), transit.getTo().getHash(), transit.getStarted(), transit.getCompleteAt(), Instant.now(clock))
        );

    }

    @Transactional
    public TransitDTO loadTransit(Long id) {
        return new TransitDTO(transitRepository.getOne(id));
    }
}
