package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.crm.ClientDTO;
import io.legacyfighter.cabs.ride.TransitDTO;
import io.legacyfighter.cabs.ride.Transit;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariff;
import io.legacyfighter.cabs.ride.TransitRepository;
import io.legacyfighter.cabs.ride.RideService;
import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


@Component
class TransitFixture {

    @Autowired
    RideService rideService;

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    TransitDetailsFacade transitDetailsFacade;

    @Autowired
    StubbedTransitPrice stubbedTransitPrice;

    Transit transitDetails(Driver driver, Integer price, LocalDateTime when, Client client, Address from, Address to) {
        Transit transit = transitRepository.save(new Transit(null, UUID.randomUUID()));
        stubbedTransitPrice.stub(new Money(price));
        transitDetailsFacade.transitRequested(when.toInstant(ZoneOffset.UTC), transit.getRequestUUID(), from, to, Distance.ofKm(20), client, CarClass.VAN, new Money(price), Tariff.ofTime(when));
        transitDetailsFacade.transitAccepted(transit.getRequestUUID(), driver.getId(), when.toInstant(ZoneOffset.UTC));
        transitDetailsFacade.transitStarted(transit.getRequestUUID(), transit.getId(), when.toInstant(ZoneOffset.UTC));
        transitDetailsFacade.transitCompleted(transit.getRequestUUID(), when.toInstant(ZoneOffset.UTC), new Money(price), null);
        return transit;
    }

    TransitDTO aTransitDTO(Client client, AddressDTO from, AddressDTO to) {
        TransitDTO transitDTO = new TransitDTO();
        transitDTO.setClientDTO(new ClientDTO(client));
        transitDTO.setFrom(from);
        transitDTO.setTo(to);
        return transitDTO;
    }
}
