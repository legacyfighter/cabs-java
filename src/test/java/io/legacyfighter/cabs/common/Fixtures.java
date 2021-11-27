package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.CarTypeDTO;
import io.legacyfighter.cabs.dto.ClientDTO;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.entity.Driver.Status;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.*;

import io.legacyfighter.cabs.service.CarTypeService;
import io.legacyfighter.cabs.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.IntStream;


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

    @Autowired
    CarTypeService carTypeService;

    public Client aClient() {
        return clientRepository.save(new Client());
    }

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when) {
        Transit transit = new Transit(null, null, null, null, when.toInstant(ZoneOffset.UTC), Distance.ZERO);
        transit.setPrice(new Money(price));
        transit.proposeTo(driver);
        transit.acceptBy(driver, Instant.now());
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
        Transit transit = new Transit(
                addressRepository.save(new Address("Polska", "Warszawa", "Młynarska", 20)),
                addressRepository.save(new Address("Polska", "Warszawa", "Zytnia", 20)),
                aClient(),
                null,
                when,
                Distance.ZERO);
        transit.publishAt(when);
        transit.setPrice(new Money(price));
        return transitRepository.save(transit);
    }

    public CarType anActiveCarCategory(CarType.CarClass carClass) {
        CarTypeDTO carTypeDTO = new CarTypeDTO();
        carTypeDTO.setCarClass(carClass);
        carTypeDTO.setDescription("opis");
        CarType carType = carTypeService.create(carTypeDTO);
        IntStream.range(1, carType.getMinNoOfCarsToActivateClass() + 1)
                .forEach(i -> carTypeService.registerCar(carType.getCarClass()));
        carTypeService.activate(carType.getId());
        return carType;
    }

    public TransitDTO aTransitDTO(Client client, AddressDTO from, AddressDTO to) {
        TransitDTO transitDTO = new TransitDTO();
        transitDTO.setClientDTO(new ClientDTO(client));
        transitDTO.setFrom(from);
        transitDTO.setTo(to);
        return transitDTO;
    }

    public TransitDTO aTransitDTO(AddressDTO from, AddressDTO to) {
        return aTransitDTO(aClient(), from, to);
    }
}
