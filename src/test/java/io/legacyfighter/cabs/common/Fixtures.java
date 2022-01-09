package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.*;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.entity.Driver.Status;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.*;

import io.legacyfighter.cabs.service.AwardsService;
import io.legacyfighter.cabs.service.CarTypeService;
import io.legacyfighter.cabs.service.ClaimService;
import io.legacyfighter.cabs.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;


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

    @Autowired
    ClaimService claimService;

    @Autowired
    AwardsService awardsService;

    public Client aClient() {
        return clientRepository.save(new Client());
    }

    public Client aClient(Client.Type type) {
        Client client = new Client();
        client.setType(type);
        return clientRepository.save(client);
    }

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when, Client client) {
        Transit transit = new Transit(null, null, client, null, when.toInstant(ZoneOffset.UTC), Distance.ZERO);
        transit.setPrice(new Money(price));
        transit.proposeTo(driver);
        transit.acceptBy(driver, Instant.now());
        return transitRepository.save(transit);
    }

    public Transit aTransit(Money price) {
        return aTransit(aDriver(), price.toInt());
    }

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when) {
        return aTransit(driver, price, when, null);
    }

    public Transit aTransit(Driver driver, Integer price) {
        return aTransit(driver, price, LocalDateTime.now(), null);
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
        return aCompletedTransitAt(price, when, aClient(), aDriver());
    }

    public Transit aCompletedTransitAt(int price, Instant when, Client client, Driver driver) {
        Address destination = addressRepository.save(new Address("Polska", "Warszawa", "Zytnia", 20));
        Transit transit = new Transit(
                addressRepository.save(new Address("Polska", "Warszawa", "Młynarska", 20)),
                destination,
                client,
                null,
                when,
                Distance.ZERO);
        transit.publishAt(when);
        transit.proposeTo(driver);
        transit.acceptBy(driver, Instant.now());
        transit.start(Instant.now());
        transit.completeAt(Instant.now(), destination, Distance.ofKm(20));
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

    public void clientHasDoneTransits(Client client, int noOfTransits) {
        range(1, noOfTransits + 1)
                .forEach(i -> transitRepository.save(aCompletedTransitAt(10, Instant.now(), client, aDriver())));
    }

    public Claim createClaim(Client client, Transit transit) {
        ClaimDTO claimDTO = claimDto("Okradli mnie na hajs", "$$$", client.getId(), transit.getId());
        claimDTO.setDraft(false);
        Claim claim = claimService.create(claimDTO);
        return claim;
    }

    public Claim createAndResolveClaim(Client client, Transit transit) {
        Claim claim = createClaim(client, transit);
        claim = claimService.tryToResolveAutomatically(claim.getId());
        return claim;
    }

    public ClaimDTO claimDto(String desc, String reason, Long clientId, Long transitId) {
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setClientId(clientId);
        claimDTO.setTransitId(transitId);
        claimDTO.setIncidentDescription(desc);
        claimDTO.setReason(reason);
        return claimDTO;
    }

    public void clientHasDoneClaims(Client client, int howMany) {
        IntStream
                .range(1, howMany + 1).forEach(i -> createAndResolveClaim(client, aTransit(aDriver(), 20, LocalDateTime.now(), client)));
    }

    public Client aClientWithClaims(Client.Type type, int howManyClaims) {
        Client client = aClient(type);
        clientHasDoneClaims(client, howManyClaims);
        return client;
    }

    public void awardsAccount(Client client) {
        awardsService.registerToProgram(client.getId());
    }

    public void activeAwardsAccount(Client client) {
        awardsAccount(client);
        awardsService.activateAccount(client.getId());
    }
}
