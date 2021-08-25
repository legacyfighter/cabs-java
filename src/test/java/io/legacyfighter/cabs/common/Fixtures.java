package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.entity.Driver.Status;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.*;

import io.legacyfighter.cabs.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Component
public class Fixtures {

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    DriverFeeRepository feeRepository;

    @Autowired
    DriverService driverService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AddressRepository addressRepository;

    public Client aClient() {
        return clientRepository.save(new Client());
    }

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when) {
        Transit transit = new Transit();
        transit.setPrice(new Money(price));
        transit.setDriver(driver);
        transit.setDateTime(when.toInstant(ZoneOffset.UTC));
        return transitRepository.save(transit);
    }

    public Transit aTransit(Driver driver, Integer price) {
        return aTransit(driver, price, LocalDateTime.now());
    }

    public DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType, int amount, Integer min) {
        DriverFee driverFee = new DriverFee();
        driverFee.setDriver(driver);
        driverFee.setAmount(amount);
        driverFee.setFeeType(feeType);
        driverFee.setMin(new Money(min));
        return feeRepository.save(driverFee);
    }

    public DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType, int amount) {
        return driverHasFee(driver, feeType, amount, 0);
    }

    public Driver aDriver() {
        return driverService.createDriver("FARME100165AB5EW", "Kowalsi", "Janusz", Driver.Type.REGULAR, Status.ACTIVE, "");
    }

    public Transit aCompletedTransitAt(int price, Instant when) {
        Transit transit = aTransit(null, price);
        transit.setDateTime(when);
        transit.setTo(addressRepository.save(new Address("Polska", "Warszawa", "Zytnia", 20)));
        transit.setFrom(addressRepository.save(new Address("Polska", "Warszawa", "Młynarska", 20)));
        transit.setClient(aClient());
        return transitRepository.save(transit);
    }
}
