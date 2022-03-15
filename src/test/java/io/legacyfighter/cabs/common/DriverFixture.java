package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.driverfleet.*;
import io.legacyfighter.cabs.driverfleet.Driver.Status;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.tracking.DriverSessionService;
import io.legacyfighter.cabs.tracking.DriverTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

import static io.legacyfighter.cabs.carfleet.CarClass.VAN;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;


@Component
class DriverFixture {

    @Autowired
    DriverFeeRepository feeRepository;

    @Autowired
    DriverService driverService;

    @Autowired
    DriverAttributeRepository driverAttributeRepository;

    @Autowired
    DriverSessionService driverSessionService;

    @Autowired
    DriverTrackingService driverTrackingService;

    @Autowired
    DriverFeeService driverFeeService;

    DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType, int amount, Integer min) {
        DriverFee driverFee = feeRepository.findByDriverId(driver.getId());
        if (driverFee == null) {
            driverFee = new DriverFee();
        }
        driverFee.setDriver(driver);
        driverFee.setAmount(amount);
        driverFee.setFeeType(feeType);
        driverFee.setMin(new Money(min));
        return feeRepository.save(driverFee);
    }

    DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType, int amount) {
        return driverHasFee(driver, feeType, amount, 0);
    }

    Driver aDriver() {
        return aDriver(Status.ACTIVE, "Janusz", "Kowalsi", "FARME100165AB5EW");
    }

    Driver aDriver(Status status, String name, String lastName, String driverLicense) {
        return driverService.createDriver(driverLicense, lastName, name, Driver.Type.REGULAR, status, "");
    }

    Driver aNearbyDriver(GeocodingService stubbedGeocodingService, Address pickup, double latitude, double longitude) {
        when(stubbedGeocodingService.geocodeAddress(argThat(new AddressMatcher(pickup)))).thenReturn(new double[]{latitude, longitude});
        return aNearbyDriver("WU DAMIAN", latitude, longitude, VAN, Instant.now(), "brand");
    }

    Driver aNearbyDriver(GeocodingService stubbedGeocodingService, Address pickup) {
        Random random = new Random();
        double latitude = random.nextDouble();
        double longitude = random.nextDouble();
        return aNearbyDriver(stubbedGeocodingService, pickup, latitude, longitude);
    }

    Driver aNearbyDriver(String plateNumber, double latitude, double longitude, CarClass carClass, Instant when, String carBrand) {
        Driver driver = aDriver();
        driverHasFee(driver, DriverFee.FeeType.FLAT, 10);
        driverLogsIn(plateNumber, carClass, driver, carBrand);
        return driverIsAtGeoLocalization(plateNumber, latitude, longitude, carClass, driver, when, carBrand);
    }

    Driver driverIsAtGeoLocalization(String plateNumber, double latitude, double longitude, CarClass carClass, Driver driver, Instant when, String carBrand) {
        driverTrackingService.registerPosition(driver.getId(), latitude, longitude, when);
        return driver;
    }

    void driverLogsIn(String plateNumber, CarClass carClass, Driver driver, String carBrand) {
        driverSessionService.logIn(driver.getId(), plateNumber, carClass, carBrand);
    }

    void driverHasAttribute(Driver driver, DriverAttributeName name, String value) {
        driverAttributeRepository.save(new DriverAttribute(driver, name, value));
    }
}

