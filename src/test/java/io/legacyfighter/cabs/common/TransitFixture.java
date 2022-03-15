package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.driverfleet.Driver;
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

    @Autowired
    StubbedTransitPrice stubbedTransitPrice;

    Transit transitDetails(Driver driver, Integer price, LocalDateTime when, Client client, Address from, Address to) {
        Transit transit = transitRepository.save(new Transit());
        stubbedTransitPrice.stub(transit.getId(), new Money(price));
        Long transitId = transit.getId();
        transitDetailsFacade.transitRequested(when.toInstant(ZoneOffset.UTC), transitId, from, to, Distance.ZERO, client, CarClass.VAN, new Money(price), Tariff.ofTime(when));
        transitDetailsFacade.transitAccepted(transitId, when.toInstant(ZoneOffset.UTC), driver.getId());
        transitDetailsFacade.transitStarted(transitId, when.toInstant(ZoneOffset.UTC));
        transitDetailsFacade.transitCompleted(transitId, when.toInstant(ZoneOffset.UTC), new Money(price), null);
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
