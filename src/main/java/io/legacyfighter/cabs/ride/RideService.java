package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.assignment.DriverAssignmentFacade;
import io.legacyfighter.cabs.assignment.InvolvedDriversSummary;
import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.common.EventsPublisher;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.crm.ClientRepository;
import io.legacyfighter.cabs.driverfleet.*;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.DistanceCalculator;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.geolocation.address.AddressRepository;
import io.legacyfighter.cabs.invocing.InvoiceGenerator;
import io.legacyfighter.cabs.loyalty.AwardsService;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariffs;
import io.legacyfighter.cabs.ride.details.TransitDetailsDTO;
import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import io.legacyfighter.cabs.ride.events.TransitCompleted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

// If this class will still be here in 2022 I will quit.
@Service
public class RideService {

    @Autowired
    private RequestTransitService requestTransitService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TransitRepository transitRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Autowired
    private DistanceCalculator distanceCalculator;

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
    private TransitDetailsFacade transitDetailsFacade;

    @Autowired
    private EventsPublisher eventsPublisher;

    @Autowired
    private DriverAssignmentFacade driverAssignmentFacade;

    @Autowired
    private RequestForTransitRepository requestForTransitRepository;

    @Autowired
    private TransitDemandRepository transitDemandRepository;

    @Autowired
    private Tariffs tariffs;

    @Autowired
    private DriverService driverService;

    @Transactional
    public TransitDTO createTransit(TransitDTO transitDTO) {
        return createTransit(transitDTO.getClientDTO().getId(), transitDTO.getFrom(), transitDTO.getTo(), transitDTO.getCarClass());
    }

    @Transactional
    public TransitDTO createTransit(Long clientId, AddressDTO fromDto, AddressDTO toDto, CarClass carClass) {
        Client client = findClient(clientId);
        Address from = addressFromDto(fromDto);
        Address to = addressFromDto(toDto);
        Instant now = Instant.now(clock);
        RequestForTransit requestForTransit = requestTransitService.createRequestForTransit(from, to);
        transitDetailsFacade.transitRequested(now, requestForTransit.getRequestUUID(), from, to, requestForTransit.getDistance(), client, carClass, requestForTransit.getEstimatedPrice(), requestForTransit.getTariff());
        return loadTransit(requestForTransit.getId());
    }

    private Client findClient(Long clientId) {
        Client client = clientRepository.getOne(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client does not exist, id = " + clientId);
        }
        return client;
    }

    private Address addressFromDto(AddressDTO addressDTO) {
        Address address = addressDTO.toAddressEntity();
        return addressRepository.save(address);
    }

    @Transactional
    public void changeTransitAddressFrom(UUID requestUUID, Address newAddress) {
        newAddress = addressRepository.save(newAddress);
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);
        if (transitDemand == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        if (driverAssignmentFacade.isDriverAssigned(requestUUID)) {
            throw new IllegalStateException("Driver already assigned, requestUUID = " + requestUUID);
        }
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        // FIXME later: add some exceptions handling
        double[] geoFromNew = geocodingService.geocodeAddress(newAddress);
        double[] geoFromOld = geocodingService.geocodeAddress(transitDetails.from.toAddressEntity());

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
        transitDemand.changePickup(distanceInKMeters);
        transitDetailsFacade.pickupChangedTo(requestUUID, newAddress, newDistance);
        driverAssignmentFacade.notifyProposedDriversAboutChangedDestination(requestUUID);
    }

    @Transactional
    public void changeTransitAddressTo(UUID requestUUID, AddressDTO newAddress) {
        changeTransitAddressTo(requestUUID, newAddress.toAddressEntity());
    }

    @Transactional
    public void changeTransitAddressFrom(UUID requestUUID, AddressDTO newAddress) {
        changeTransitAddressFrom(requestUUID, newAddress.toAddressEntity());
    }

    @Transactional
    public void changeTransitAddressTo(UUID requestUUID, Address newAddress) {
        addressRepository.save(newAddress);
        RequestForTransit requestForTransit = requestForTransitRepository.findByRequestUUID(requestUUID);
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        if (requestForTransit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }

        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(transitDetails.from.toAddressEntity());
        double[] geoTo = geocodingService.geocodeAddress(newAddress);
        Distance newDistance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        Transit transit = transitRepository.findByTransitRequestUUID(requestUUID);
        if (transit != null) {
            transit.changeDestination(newDistance);
        }
        driverAssignmentFacade.notifyAssignedDriverAboutChangedDestination(requestUUID);
        transitDetailsFacade.destinationChanged(requestUUID, newAddress);
    }

    @Transactional
    public void cancelTransit(UUID requestUUID) {
        RequestForTransit transit = requestForTransitRepository.findByRequestUUID(requestUUID);
        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);
        if (transitDemand != null) {
            transitDemand.cancel();
            driverAssignmentFacade.cancel(requestUUID);
        }
        transitDetailsFacade.transitCancelled(requestUUID);
    }

    @Transactional
    public Transit publishTransit(UUID requestUUID) {
        RequestForTransit requestFor = requestForTransitRepository.findByRequestUUID(requestUUID);
        TransitDetailsDTO transitDetailsDTO = transitDetailsFacade.find(requestUUID);

        if (requestFor == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }

        Instant now = Instant.now(clock);
        transitDemandRepository.save(new TransitDemand(requestFor.getRequestUUID()));
        driverAssignmentFacade.createAssignment(requestUUID, transitDetailsDTO.from, transitDetailsDTO.carType, now);
        transitDetailsFacade.transitPublished(requestUUID, now);
        return transitRepository.findByTransitRequestUUID(requestUUID);
    }

    // Abandon hope all ye who enter here...
    @Transactional
    public Transit findDriversForTransit(UUID requestUUID) {
        TransitDetailsDTO transitDetailsDTO = transitDetailsFacade.find(requestUUID);
        InvolvedDriversSummary involvedDriversSummary = driverAssignmentFacade.searchForPossibleDrivers(requestUUID, transitDetailsDTO.from, transitDetailsDTO.carType);
        transitDetailsFacade.driversAreInvolved(requestUUID, involvedDriversSummary);
        return transitRepository.findByTransitRequestUUID(requestUUID);
    }

    @Transactional
    public void acceptTransit(Long driverId, UUID requestUUID) {
        Driver driver = driverRepository.getOne(driverId);
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        } else {
            if (driverAssignmentFacade.isDriverAssigned(requestUUID)) {
                throw new IllegalStateException("Driver already assigned, requestUUID = " + requestUUID);
            }
            if (transitDemand == null) {
                throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
            } else {
                Instant now = Instant.now(clock);
                transitDemand.accepted();
                driverAssignmentFacade.acceptTransit(requestUUID, driver);
                transitDetailsFacade.transitAccepted(requestUUID, driverId, now);
                driverRepository.save(driver);
            }
        }
    }

    @Transactional
    public void startTransit(Long driverId, UUID requestUUID) {
        Driver driver = driverRepository.getOne(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);

        if (transitDemand == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        if (!driverAssignmentFacade.isDriverAssigned(requestUUID)) {
            throw new IllegalStateException("Driver not assigned, requestUUID = " + requestUUID);
        }
        Instant now = Instant.now(clock);
        Transit transit = new Transit(tariffs.choose(now), requestUUID);
        transitRepository.save(transit);
        transitDetailsFacade.transitStarted(requestUUID, transit.getId(), now);
    }

    @Transactional
    public void rejectTransit(Long driverId, UUID requestUUID) {
        Driver driver = driverRepository.getOne(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }

        driverAssignmentFacade.rejectTransit(requestUUID, driverId);
    }

    @Transactional
    public void completeTransit(Long driverId, UUID requestUUID, AddressDTO destinationAddress) {
        completeTransit(driverId, requestUUID, destinationAddress.toAddressEntity());
    }

    @Transactional
    public void completeTransit(Long driverId, UUID requestUUID, Address destinationAddress) {
        destinationAddress = addressRepository.save(destinationAddress);
        Driver driver = driverRepository.getOne(driverId);
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }

        Transit transit = transitRepository.findByTransitRequestUUID(requestUUID);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }


        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(addressRepository.getByHash(transitDetails.from.getHash()));
        double[] geoTo = geocodingService.geocodeAddress(addressRepository.getByHash(destinationAddress.getHash()));
        Distance distance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        Instant now = Instant.now(clock);
        Money finalPrice = transit.completeAt(distance);

        Money driverFee = driverFeeService.calculateDriverFee(finalPrice, driverId);
        driver.setOccupied(false);
        driverRepository.save(driver);
        awardsService.registerMiles(transitDetails.client.getId(), transit.getId());
        transitRepository.save(transit);
        transitDetailsFacade.transitCompleted(requestUUID, now, finalPrice, driverFee);
        invoiceGenerator.generate(finalPrice.toInt(), transitDetails.client.getName() + " " + transitDetails.client.getLastName());
        eventsPublisher.publish(new TransitCompleted(
                transitDetails.client.getId(), transit.getId(), transitDetails.from.getHash(), destinationAddress.getHash(), transitDetails.started, now, Instant.now(clock))
        );
    }

    @Transactional
    public TransitDTO loadTransit(UUID requestUUID) {
        InvolvedDriversSummary involvedDriversSummary = driverAssignmentFacade.loadInvolvedDrivers(requestUUID);
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        Set<DriverDTO> proposedDrivers = driverService.loadDrivers(involvedDriversSummary.proposedDrivers);
        Set<DriverDTO> driverRejections = driverService.loadDrivers(involvedDriversSummary.driverRejections);

        return new TransitDTO(transitDetails, proposedDrivers, driverRejections, involvedDriversSummary.assignedDriver);
    }

    public TransitDTO loadTransit(Long requestId) {
        UUID requestUUID = getRequestUUID(requestId);
        return loadTransit(requestUUID);
    }

    public UUID getRequestUUID(Long requestUUID) {
        return requestForTransitRepository.getOne(requestUUID).getRequestUUID();
    }
}
