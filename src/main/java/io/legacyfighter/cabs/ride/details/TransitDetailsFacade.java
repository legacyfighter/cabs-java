package io.legacyfighter.cabs.ride.details;


import io.legacyfighter.cabs.assignment.InvolvedDriversSummary;
import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransitDetailsFacade {

    private final TransitDetailsRepository transitDetailsRepository;

    public TransitDetailsFacade(TransitDetailsRepository transitDetailsRepository) {
        this.transitDetailsRepository = transitDetailsRepository;
    }

    public TransitDetailsDTO find(UUID requestId) {
        return new TransitDetailsDTO(load(requestId));
    }

    public TransitDetailsDTO find(Long transitId) {
        return new TransitDetailsDTO(load(transitId));
    }

    public void transitRequested(Instant when, UUID requestId, Address from, Address to, Distance distance, Client client, CarClass carClass, Money estimatedPrice, Tariff tariff) {
        TransitDetails transitDetails = new TransitDetails(when, requestId, from, to, distance, client, carClass, estimatedPrice, tariff);
        transitDetailsRepository.save(transitDetails);
    }

    @Transactional
    public void pickupChangedTo(UUID requestId, Address newAddress, Distance newDistance) {
        TransitDetails details = load(requestId);
        details.pickupChangedTo(newAddress, newDistance);
    }

    @Transactional
    public void destinationChanged(UUID requestId, Address newAddress, Distance newDistance) {
        TransitDetails details = load(requestId);
        details.destinationChangedTo(newAddress, newDistance);
    }

    @Transactional
    public void transitStarted(UUID requestId, Long transitId, Instant when) {
        TransitDetails details = load(requestId);
        details.startedAt(when, transitId);
    }

    @Transactional
    public void transitAccepted(UUID requestId, Long driverId, Instant when) {
        TransitDetails details = load(requestId);
        details.acceptedAt(when, driverId);
    }

    @Transactional
    public void transitCompleted(UUID requestId, Instant when, Money price, Money driverFee) {
        TransitDetails details = load(requestId);
        details.completedAt(when, price, driverFee);
    }

    public List<TransitDetailsDTO> findByClient(Long clientId) {
        return transitDetailsRepository.findByClientId(clientId)
                .stream()
                .map(TransitDetailsDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransitDetailsDTO> findCompleted() {
        return transitDetailsRepository.findByStatus(Status.COMPLETED)
                .stream()
                .map(TransitDetailsDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransitDetailsDTO> findByDriver(Long driverId, Instant from, Instant to) {
        return transitDetailsRepository.findAllByDriverAndDateTimeBetween(driverId, from, to)
                .stream()
                .map(TransitDetailsDTO::new)
                .collect(Collectors.toList());
    }

    private TransitDetails load(UUID requestId) {
        return transitDetailsRepository.findByRequestUUID(requestId);
    }

    private TransitDetails load(Long transitId) {
        return transitDetailsRepository.findByTransitId(transitId);
    }

    @Transactional
    public void transitPublished(UUID requestId, Instant when) {
        TransitDetails details = load(requestId);
        details.publishedAt(when);
    }

    @Transactional
    public void driversAreInvolved(UUID requestId, InvolvedDriversSummary involvedDriversSummary) {
        TransitDetails details = load(requestId);
        details.involvedDriversAre(involvedDriversSummary);
    }

    @Transactional
    public void transitCancelled(UUID requestId) {
        TransitDetails details = load(requestId);
        details.cancelled();
    }
}
