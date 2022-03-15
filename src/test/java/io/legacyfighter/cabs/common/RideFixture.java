package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.driverfleet.DriverFee;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.geolocation.address.AddressRepository;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.ride.Transit;
import io.legacyfighter.cabs.ride.TransitDTO;
import io.legacyfighter.cabs.ride.RideService;
import io.legacyfighter.cabs.ride.TransitRepository;
import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import io.legacyfighter.cabs.tracking.DriverSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

import static io.legacyfighter.cabs.carfleet.CarClass.VAN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Component
public class RideFixture {

    @Autowired
    RideService rideService;

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
        stubPrice(price);
        from = addressRepository.save(from);
        destination = addressRepository.save(destination);
        carTypeFixture.anActiveCarCategory(VAN);
        TransitDTO transitView = rideService.createTransit(client.getId(), new AddressDTO(from), new AddressDTO(destination), VAN);
        rideService.publishTransit(transitView.getRequestId());
        rideService.findDriversForTransit(transitView.getRequestId());
        rideService.acceptTransit(driver.getId(), transitView.getRequestId());
        rideService.startTransit(driver.getId(), transitView.getRequestId());
        rideService.completeTransit(driver.getId(), transitView.getRequestId(), destination);
        Long transitID = transitDetailsFacade.find(transitView.getRequestId()).transitId;
        return transitRepository.getOne(transitID);
    }

    private void stubPrice(int price) {
        Money fakePrice = new Money(price);
        stubbedPrice.stub(fakePrice);
    }

    public TransitDTO aRideWithFixedClock(int price, Instant publishedAt, Instant completedAt, Client client, Driver driver, Address from, Address destination, Clock clock) {
        from = addressRepository.save(from);
        destination = addressRepository.save(destination);
        when(clock.instant()).thenReturn(publishedAt);
        stubbedPrice.stub(new Money(price));

        carTypeFixture.anActiveCarCategory(VAN);
        TransitDTO transit = rideService.createTransit(client.getId(), new AddressDTO(from), new AddressDTO(destination), VAN);
        rideService.publishTransit(transit.getRequestId());
        rideService.findDriversForTransit(transit.getRequestId());
        rideService.acceptTransit(driver.getId(), transit.getRequestId());
        rideService.startTransit(driver.getId(), transit.getRequestId());
        when(clock.instant()).thenReturn(completedAt);
        rideService.completeTransit(driver.getId(), transit.getRequestId(), destination);
        return rideService.loadTransit(transit.getRequestId());
    }

    public TransitDTO driverHasDoneSessionAndPicksSomeoneUpInCar(Driver driver, Client client, CarClass carClass, String plateNumber, String carBrand, Instant when,
                                                              GeocodingService geocodingService, Clock clock) {
        when(clock.instant()).thenReturn(when);
        Address from = addressRepository.save(new Address("PL", "MAZ", "WAW", "STREET", 1));
        Address to = addressRepository.save(new Address("PL", "MAZ", "WAW", "STREET", 100));
        when(geocodingService.geocodeAddress(any())).thenReturn(new double[]{1, 1});
        driverFixture.driverLogsIn(plateNumber, carClass, driver, carBrand);
        driverFixture.driverIsAtGeoLocalization(plateNumber, 1, 1, carClass, driver, when, carBrand);
        driverFixture.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);
        TransitDTO transit = aRideWithFixedClock(30, when, when, client, driver, from, to, clock);
        driverSessionService.logOutCurrentSession(driver.getId());
        return transit;
    }
}
