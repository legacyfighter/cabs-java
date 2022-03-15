package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.AddressRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import io.legacyfighter.cabs.service.DriverSessionService;
import io.legacyfighter.cabs.service.TransitService;
import io.legacyfighter.cabs.transitdetails.TransitDetailsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

import static io.legacyfighter.cabs.entity.CarType.CarClass.VAN;
import static org.mockito.Mockito.when;

@Component
public class RideFixture {

    @Autowired
    TransitService transitService;

    @Autowired
    TransitDetailsFacade transitDetailsFacade;

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    DriverFixture driverFixture;

    @Autowired
    CarTypeFixture carTypeFixture;

    @Autowired
    StubbedTransitPrice stubbedPrice;

    @Autowired
    DriverSessionService driverSessionService;

    public Transit aRide(int price, Client client, Driver driver, Address from, Address destination) {
        from = addressRepository.save(from);
        destination = addressRepository.save(destination);
        carTypeFixture.anActiveCarCategory(VAN);
        Transit transit = transitService.createTransit(client.getId(), from, destination, VAN);
        transitService.publishTransit(transit.getId());
        transitService.findDriversForTransit(transit.getId());
        transitService.acceptTransit(driver.getId(), transit.getId());
        transitService.startTransit(driver.getId(), transit.getId());
        transitService.completeTransit(driver.getId(), transit.getId(), destination);
        stubPrice(price, transit);
        return transitRepository.getOne(transit.getId());
    }

    private void stubPrice(int price, Transit transit) {
        Money fakePrice = new Money(price);
        stubbedPrice.stub(transit.getId(), fakePrice);
        transitDetailsFacade.transitCompleted(transit.getId(), Instant.now(), fakePrice, fakePrice);
    }

    public Transit aRideWithFixedClock(int price, Instant publishedAt, Instant completedAt, Client client, Driver driver, Address from, Address destination, Clock clock) {
        from = addressRepository.save(from);
        destination = addressRepository.save(destination);
        when(clock.instant()).thenReturn(publishedAt);
        carTypeFixture.anActiveCarCategory(VAN);
        Transit transit = transitService.createTransit(client.getId(), from, destination, VAN);
        transitService.publishTransit(transit.getId());
        transitService.findDriversForTransit(transit.getId());
        transitService.acceptTransit(driver.getId(), transit.getId());
        transitService.startTransit(driver.getId(), transit.getId());
        when(clock.instant()).thenReturn(completedAt);
        transitService.completeTransit(driver.getId(), transit.getId(), destination);
        stubbedPrice.stub(transit.getId(), new Money(price));
        return transitRepository.getOne(transit.getId());
    }

}
