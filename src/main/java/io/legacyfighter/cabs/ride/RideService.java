package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.assignment.DriverAssignmentFacade;
import io.legacyfighter.cabs.assignment.InvolvedDriversSummary;
import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.common.EventsPublisher;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.crm.ClientRepository;
import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.driverfleet.DriverFeeService;
import io.legacyfighter.cabs.driverfleet.DriverService;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.geolocation.address.AddressRepository;
import io.legacyfighter.cabs.invocing.InvoiceGenerator;
import io.legacyfighter.cabs.loyalty.AwardsService;
import io.legacyfighter.cabs.money.Money;
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
// 20.01.22 - It's a bit better now.
@Service
public class RideService {

    @Autowired
    private RequestTransitService requestTransitService;

    @Autowired
    private ChangePickupService changePickupService;

    @Autowired
    private ChangeDestinationService changeDestinationService;

    @Autowired
    private DemandService demandService;

    @Autowired
    private CompleteTransitService completeTransitService;

    @Autowired
    private StartTransitService startTransitService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

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

    @Transactional
    public void changeTransitAddressFrom(UUID requestUUID, Address newAddress) {
        if (driverAssignmentFacade.isDriverAssigned(requestUUID)) {
            throw new IllegalStateException("Driver already assigned, requestUUID = " + requestUUID);
        }
        newAddress = addressRepository.save(newAddress);
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        Address oldAddress = transitDetails.from.toAddressEntity();
        Distance newDistance = changePickupService.changeTransitAddressFrom(requestUUID, newAddress, oldAddress);
        transitDetailsFacade.pickupChangedTo(requestUUID, newAddress, newDistance);
        driverAssignmentFacade.notifyProposedDriversAboutChangedDestination(requestUUID);
    }

    @Transactional
    public void changeTransitAddressFrom(UUID requestUUID, AddressDTO newAddress) {
        changeTransitAddressFrom(requestUUID, newAddress.toAddressEntity());
    }

    @Transactional
    public void changeTransitAddressTo(UUID requestUUID, Address newAddress) {
        newAddress = addressRepository.save(newAddress);
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        if (transitDetails == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        Address oldAddress = transitDetails.from.toAddressEntity();
        Distance distance = changeDestinationService.changeTransitAddressTo(requestUUID, newAddress, oldAddress);
        driverAssignmentFacade.notifyAssignedDriverAboutChangedDestination(requestUUID);
        transitDetailsFacade.destinationChanged(requestUUID, newAddress, distance);
    }

    @Transactional
    public void changeTransitAddressTo(UUID requestUUID, AddressDTO newAddress) {
        changeTransitAddressTo(requestUUID, newAddress.toAddressEntity());
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
    public void publishTransit(UUID requestUUID) {
        TransitDetailsDTO transitDetailsDTO = transitDetailsFacade.find(requestUUID);
        if (transitDetailsDTO == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        demandService.publishDemand(requestUUID);
        driverAssignmentFacade.startAssigningDrivers(requestUUID, transitDetailsDTO.from, transitDetailsDTO.carType, Instant.now(clock));
        transitDetailsFacade.transitPublished(requestUUID, Instant.now(clock));
    }

    @Transactional
    public void cancelTransit(UUID requestUUID) {
        TransitDetailsDTO transitDetailsDTO = transitDetailsFacade.find(requestUUID);
        if (transitDetailsDTO == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        demandService.cancelDemand(requestUUID);
        driverAssignmentFacade.cancel(requestUUID);
        transitDetailsFacade.transitCancelled(requestUUID);
    }

    @Transactional
    public TransitDetailsDTO findDriversForTransit(UUID requestUUID) {
        TransitDetailsDTO transitDetailsDTO = transitDetailsFacade.find(requestUUID);
        InvolvedDriversSummary involvedDriversSummary = driverAssignmentFacade.searchForPossibleDrivers(requestUUID, transitDetailsDTO.from, transitDetailsDTO.carType);
        transitDetailsFacade.driversAreInvolved(requestUUID, involvedDriversSummary);
        return transitDetailsFacade.find(requestUUID);
    }

    @Transactional
    public void acceptTransit(Long driverId, UUID requestUUID) {
        if (!driverService.exists(driverId)) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        } else {
            if (driverAssignmentFacade.isDriverAssigned(requestUUID)) {
                throw new IllegalStateException("Driver already assigned, requestUUID = " + requestUUID);
            }
            demandService.acceptDemand(requestUUID);
            driverAssignmentFacade.acceptTransit(requestUUID, driverId);
            driverService.markOccupied(driverId);
            transitDetailsFacade.transitAccepted(requestUUID, driverId, Instant.now(clock));
        }
    }

    @Transactional
    public void startTransit(Long driverId, UUID requestUUID) {
        if (!driverService.exists(driverId)) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }

        if (!demandService.existsFor(requestUUID)) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }
        if (!driverAssignmentFacade.isDriverAssigned(requestUUID)) {
            throw new IllegalStateException("Driver not assigned, requestUUID = " + requestUUID);
        }
        Instant now = Instant.now(clock);
        Transit transit = startTransitService.start(requestUUID);
        transitDetailsFacade.transitStarted(requestUUID, transit.getId(), now);
    }

    @Transactional
    public void rejectTransit(Long driverId, UUID requestUUID) {
        if (!driverService.exists(driverId)) {
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
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(requestUUID);
        if (!driverService.exists(driverId)) {
            throw new IllegalArgumentException("Driver does not exist, id = " + driverId);
        }
        Address from = addressRepository.getByHash(transitDetails.from.getHash());
        Address to = addressRepository.getByHash(destinationAddress.getHash());
        Money finalPrice = completeTransitService.completeTransit(driverId, requestUUID, from, to);
        Money driverFee = driverFeeService.calculateDriverFee(finalPrice, driverId);
        driverService.markNotOccupied(driverId);
        transitDetailsFacade.transitCompleted(requestUUID, Instant.now(clock), finalPrice, driverFee);
        awardsService.registerMiles(transitDetails.client.getId(), transitDetails.transitId);
        invoiceGenerator.generate(finalPrice.toInt(), transitDetails.client.getName() + " " + transitDetails.client.getLastName());
        eventsPublisher.publish(new TransitCompleted(
                transitDetails.client.getId(), transitDetails.transitId, transitDetails.from.getHash(), destinationAddress.getHash(), transitDetails.started, Instant.now(clock), Instant.now(clock))
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

    public UUID getRequestUUID(Long requestId) {
        return requestTransitService.findCalculationUUID(requestId);
    }
}
