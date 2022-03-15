package io.legacyfighter.cabs.transitdetails;


import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.entity.Tariff;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class TransitDetailsFacade {

    private final TransitDetailsRepository transitDetailsRepository;

    public TransitDetailsFacade(TransitDetailsRepository transitDetailsRepository) {
        this.transitDetailsRepository = transitDetailsRepository;
    }

    public TransitDetailsDTO find(Long transitId) {
        return new TransitDetailsDTO(load(transitId));
    }

    public void transitRequested(Instant when, Long transitId, Address from, Address to, Distance distance, Client client, CarClass carClass, Money estimatedPrice, Tariff tariff) {
        TransitDetails transitDetails = new TransitDetails(when, transitId, from, to, distance, client, carClass, estimatedPrice, tariff);
        transitDetailsRepository.save(transitDetails);
    }

    @Transactional
    public void pickupChangedTo(Long transitId, Address newAddress, Distance newDistance) {
        TransitDetails details = load(transitId);
        details.pickupChangedTo(newAddress, newDistance);
    }

    @Transactional
    public void destinationChanged(Long transitId, Address newAddress, Distance newDistance) {
        TransitDetails details = load(transitId);
        details.destinationChangedTo(newAddress, newDistance);
    }

    @Transactional
    public void transitPublished(Long transitId, Instant when) {
        TransitDetails details = load(transitId);
        details.publishedAt(when);
    }

    @Transactional
    public void transitStarted(Long transitId, Instant when) {
        TransitDetails details = load(transitId);
        details.startedAt(when);
    }

    @Transactional
    public void transitAccepted(Long transitId, Instant when, Long driverId) {
        TransitDetails details = load(transitId);
        details.acceptedAt(when, driverId);
    }

    @Transactional
    public void transitCancelled(Long transitId) {
        TransitDetails details = load(transitId);
        details.cancelled();
    }

    @Transactional
    public void transitCompleted(Long transitId, Instant when, Money price, Money driverFee) {
        TransitDetails details = load(transitId);
        details.completedAt(when, price, driverFee);
    }

    public List<TransitDetailsDTO> findCompleted() {
        return transitDetailsRepository.findByStatus(Transit.Status.COMPLETED)
                .stream()
                .map(TransitDetailsDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransitDetailsDTO> findByClient(Long clientId) {
        return transitDetailsRepository.findByClientId(clientId)
                .stream()
                .map(TransitDetailsDTO::new)
                .collect(toList());
    }
    public List<TransitDetailsDTO> findByDriver(Long driverId, Instant from, Instant to) {
        return transitDetailsRepository.findAllByDriverAndDateTimeBetween(driverId, from, to)
                .stream()
                .map(TransitDetailsDTO::new)
                .collect(toList());
    }

    private TransitDetails load(Long transitId) {
        return transitDetailsRepository.findByTransitId(transitId);
    }

}
