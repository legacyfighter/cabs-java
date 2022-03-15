package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.*;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.entity.Driver.Status;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.*;

import io.legacyfighter.cabs.service.*;
import io.legacyfighter.cabs.transitdetails.TransitDetailsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import static io.legacyfighter.cabs.entity.CarType.CarClass.VAN;
import static java.util.stream.IntStream.range;
import static org.mockito.Mockito.when;


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

    @Autowired
    TransitService transitService;

    @Autowired
    DriverAttributeRepository driverAttributeRepository;

    @Autowired
    DriverSessionService driverSessionService;

    @Autowired
    DriverTrackingService driverTrackingService;

    @Autowired
    TransitDetailsFacade transitDetailsFacade;

    public Client aClient() {
        return clientRepository.save(new Client());
    }

    public Client aClient(Client.Type type) {
        Client client = new Client();
        client.setType(type);
        return clientRepository.save(client);
    }

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when, Client client) {
        Instant dateTime = when.toInstant(ZoneOffset.UTC);
        Transit transit = new Transit(dateTime, Distance.ZERO);
        transit.setPrice(new Money(price));
        transit.proposeTo(driver);
        transit.acceptBy(driver, Instant.now());
        transit = transitRepository.save(transit);
        transitDetailsFacade.transitRequested(dateTime, transit.getId(), null, null, Distance.ZERO, client, null, new Money(price), transit.getTariff());
        return transit;
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
        return aDriver(Status.ACTIVE, "Janusz", "Kowalsi", "FARME100165AB5EW");
    }

    public Driver aDriver(Status status, String name, String lastName, String driverLicense) {
        return driverService.createDriver(driverLicense, lastName, name, Driver.Type.REGULAR, status, "");
    }

    public Driver aNearbyDriver(String plateNumber) {
        Driver driver = aDriver();
        driverHasFee(driver, DriverFee.FeeType.FLAT, 10);
        driverSessionService.logIn(driver.getId(), plateNumber, VAN, "BRAND");
        driverTrackingService.registerPosition(driver.getId(), 1, 1, Instant.now());
        return driver;
    }

    public Transit aCompletedTransitAt(int price, Instant when) {
        return aCompletedTransitAt(price, when, aClient(), aDriver());
    }

    public Transit aRequestedAndCompletedTransit(int price, Instant publishedAt, Instant completedAt, Client client, Driver driver, Address from, Address destination) {
        from = addressRepository.save(from);
        destination = addressRepository.save(destination);
        Transit transit = new Transit(publishedAt, Distance.ZERO);
        transit.publishAt(publishedAt);
        transit.proposeTo(driver);
        transit.acceptBy(driver, publishedAt);
        transit.start(publishedAt);
        transit.completeAt(completedAt, destination, Distance.ofKm(1));
        transit.setPrice(new Money(price));
        transit = transitRepository.save(transit);
        transitDetailsFacade.transitRequested(publishedAt, transit.getId(), from, destination, Distance.ZERO, client, null, new Money(price), transit.getTariff());
        transitDetailsFacade.transitAccepted(transit.getId(), publishedAt, driver.getId());
        transitDetailsFacade.transitStarted(transit.getId(), publishedAt);
        transitDetailsFacade.transitCompleted(transit.getId(), publishedAt, new Money(price), new Money(0));
        return transit;
    }

    public Transit aCompletedTransitAt(int price, Instant publishedAt, Instant completedAt, Client client, Driver driver) {
        Address destination = new Address("Polska", "Warszawa", "Zytnia", 20);
        Address from = new Address("Polska", "Warszawa", "Młynarska", 20);
        return aRequestedAndCompletedTransit(price, publishedAt, completedAt, client, driver, from, destination);
    }

    public Transit aCompletedTransitAt(int price, Instant publishedAt, Client client, Driver driver) {
        return aCompletedTransitAt(price, publishedAt, publishedAt.plus(10, ChronoUnit.MINUTES), client, driver);
    }

    public Transit aRequestedAndCompletedTransit(int price, Instant publishedAt, Instant completedAt, Client client, Driver driver, Address from, Address destination, Clock clock) {
        from = addressRepository.save(from);
        destination = addressRepository.save(destination);

        when(clock.instant()).thenReturn(publishedAt);
        Transit transit = transitService.createTransit(client.getId(), from, destination, VAN);
        transitService.publishTransit(transit.getId());
        transitService.findDriversForTransit(transit.getId());
        transitService.acceptTransit(driver.getId(), transit.getId());
        transitService.startTransit(driver.getId(), transit.getId());
        when(clock.instant()).thenReturn(completedAt);
        transitService.completeTransit(driver.getId(), transit.getId(), destination);

        return transitRepository.getOne(transit.getId());
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

    public Claim createClaim(Client client, Transit transit, String reason) {
        ClaimDTO claimDTO = claimDto("Okradli mnie na hajs", reason, client.getId(), transit.getId());
        claimDTO.setDraft(false);
        return claimService.create(claimDTO);
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

    public void driverHasAttribute(Driver driver, DriverAttribute.DriverAttributeName name, String value) {
        driverAttributeRepository.save(new DriverAttribute(driver, name, value));
    }
}
