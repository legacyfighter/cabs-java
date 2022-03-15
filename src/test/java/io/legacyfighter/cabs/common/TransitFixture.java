package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.ClientDTO;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.TransitRepository;
import io.legacyfighter.cabs.service.TransitService;
import io.legacyfighter.cabs.transitdetails.TransitDetailsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
class TransitFixture {

    @Autowired
    TransitService transitService;

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    TransitDetailsFacade transitDetailsFacade;

    Transit aTransit(Driver driver, Integer price, LocalDateTime when, Client client) {
        Instant dateTime = when.toInstant(ZoneOffset.UTC);
        Transit transit = new Transit(dateTime, Distance.ZERO);
        transit.setPrice(new Money(price));
        transit.proposeTo(driver);
        transit.acceptBy(driver, Instant.now());
        transit = transitRepository.save(transit);
        transitDetailsFacade.transitRequested(when.toInstant(ZoneOffset.UTC), transit.getId(), null, null, Distance.ofKm(20), client, CarType.CarClass.VAN, new Money(price), Tariff.ofTime(when));
        return transit;
    }

    Transit aTransit(Driver driver, Integer price) {
        return aTransit(driver, price, LocalDateTime.now(), null);
    }

    TransitDTO aTransitDTO(Client client, AddressDTO from, AddressDTO to) {
        TransitDTO transitDTO = new TransitDTO();
        transitDTO.setClientDTO(new ClientDTO(client));
        transitDTO.setFrom(from);
        transitDTO.setTo(to);
        return transitDTO;
    }
}
