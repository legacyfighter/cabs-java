package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.service.DriverSessionService;
import io.legacyfighter.cabs.service.DriverTrackingService;
import io.legacyfighter.cabs.service.GeocodingService;
import io.legacyfighter.cabs.service.TransitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;

import static io.legacyfighter.cabs.entity.CarType.CarClass.VAN;
import static io.legacyfighter.cabs.entity.Transit.Status.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransitLifeCycleIntegrationTest {

    @Autowired
    Fixtures fixtures;

    @Autowired
    TransitService transitService;

    @MockBean
    GeocodingService geocodingService;

    @Autowired
    DriverSessionService driverSessionService;

    @Autowired
    DriverTrackingService driverTrackingService;

    @BeforeEach
    public void setup() {
        fixtures.anActiveCarCategory(VAN);
        when(geocodingService.geocodeAddress(any(Address.class))).thenReturn(new double[]{1, 1});
    }

    @Test
    void canCreateTransit() {
        //when
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertNull(loaded.getCarClass());
        assertNull(loaded.getClaimDTO());
        assertNotNull(loaded.getEstimatedPrice());
        assertNull(loaded.getPrice());
        assertEquals("Polska", loaded.getFrom().getCountry());
        assertEquals("Warszawa", loaded.getFrom().getCity());
        assertEquals("Młynarska", loaded.getFrom().getStreet());
        assertEquals(20, loaded.getFrom().getBuildingNumber());
        assertEquals("Polska", loaded.getTo().getCountry());
        assertEquals("Warszawa", loaded.getTo().getCity());
        assertEquals("Żytnia", loaded.getTo().getStreet());
        assertEquals(25, loaded.getTo().getBuildingNumber());
        assertEquals(DRAFT, loaded.getStatus());
        assertNotNull(loaded.getTariff());
        assertNotEquals(0, loaded.getKmRate());
        assertNotNull(loaded.getDateTime());
    }

    @Test
    void canChangeTransitDestination() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));

        //when
        transitService.changeTransitAddressTo(transit.getId(),
                new AddressDTO("Polska", "Warszawa", "Mazowiecka", 30));

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(30, loaded.getTo().getBuildingNumber());
        assertEquals("Mazowiecka", loaded.getTo().getStreet());
        assertNotNull(loaded.getEstimatedPrice());
        assertNull(loaded.getPrice());
    }

    @Test
    void cannotChangeDestinationWhenTransitIsCompleted() {
        //given
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());
        //and
        transitService.startTransit(driver, transit.getId());
        //and
        transitService.completeTransit(driver, transit.getId(), destination);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.changeTransitAddressTo(transit.getId(),
                        new AddressDTO("Polska", "Warszawa", "Żytnia", 23)));
    }

    @Test
    void canChangePickupPlace() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));

        //when
        transitService.changeTransitAddressFrom(transit.getId(),
                new AddressDTO("Polska", "Warszawa", "Puławska", 28));

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(28, loaded.getFrom().getBuildingNumber());
        assertEquals("Puławska", loaded.getFrom().getStreet());
    }

    @Test
    void cannotChangePickupPlaceAfterTransitIsAccepted() {
        //given
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        AddressDTO changedTo = new AddressDTO("Polska", "Warszawa", "Żytnia", 27);
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.changeTransitAddressFrom(transit.getId(), changedTo));

        //and
        transitService.startTransit(driver, transit.getId());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.changeTransitAddressFrom(transit.getId(), changedTo));

        //and
        transitService.completeTransit(driver, transit.getId(), destination);
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.changeTransitAddressFrom(transit.getId(), changedTo));
    }

    @Test
    void cannotChangePickupPlaceMoreThanThreeTimes() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        transitService.changeTransitAddressFrom(transit.getId(),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 26));
        //and
        transitService.changeTransitAddressFrom(transit.getId(),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 27));
        //and
        transitService.changeTransitAddressFrom(transit.getId(),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 28));

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        transitService.changeTransitAddressFrom(transit.getId(),
                                new AddressDTO("Polska", "Warszawa", "Żytnia", 29)));
    }

    @Test
    void cannotChangePickupPlaceWhenItIsFarWayFromOriginal() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        transitService.changeTransitAddressFrom(transit.getId(), farAwayAddress(transit)));
    }

    @Test
    void canCancelTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));

        //when
        transitService.cancelTransit(transit.getId());

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(CANCELLED, loaded.getStatus());
    }

    @Test
    void cannotCancelTransitAfterItWasStarted() {
        //given
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());

        //and
        transitService.startTransit(driver, transit.getId());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.cancelTransit(transit.getId()));

        //and
        transitService.completeTransit(driver, transit.getId(), destination);
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.cancelTransit(transit.getId()));
    }

    @Test
    void canPublishTransit() {
        //given
        Transit transit = requestTransitFromTo(new AddressDTO("Polska", "Warszawa", "Młynarska", 20), new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        aNearbyDriver("WU1212");

        //when
        transitService.publishTransit(transit.getId());

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, loaded.getStatus());
        assertNotNull(loaded.getPublished());
    }

    @Test
    void canAcceptTransit() {
        //given
        Transit transit = requestTransitFromTo(new AddressDTO("Polska", "Warszawa", "Młynarska", 20), new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());

        //when
        transitService.acceptTransit(driver, transit.getId());

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(TRANSIT_TO_PASSENGER, loaded.getStatus());
        assertNotNull(loaded.getAcceptedAt());
    }

    @Test
    void onlyOneDriverCanAcceptTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        Long secondDriver = aNearbyDriver("DW MARIO");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.acceptTransit(secondDriver, transit.getId()));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoAlreadyRejected() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());

        //and
        transitService.rejectTransit(driver, transit.getId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.acceptTransit(driver, transit.getId()));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoHasNotSeenProposal() {
        //given
        Transit transit = requestTransitFromTo(new AddressDTO("Polska", "Warszawa", "Młynarska", 20), new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long farAwayDriver = aFarAwayDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.acceptTransit(farAwayDriver, transit.getId()));
    }

    @Test
    void canStartTransit() {
        //given
        Transit transit = requestTransitFromTo(new AddressDTO("Polska", "Warszawa", "Młynarska", 20), new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());
        //when
        transitService.startTransit(driver, transit.getId());

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(Transit.Status.IN_TRANSIT, loaded.getStatus());
        assertNotNull(loaded.getStarted());
    }

    @Test
    void cannotStartNotAcceptedTransit() {
        //given
        Transit transit = requestTransitFromTo(new AddressDTO("Polska", "Warszawa", "Młynarska", 20), new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitService.startTransit(driver, transit.getId()));
    }

    @Test
    void canCompleteTransit() {
        //given
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());
        //and
        transitService.startTransit(driver, transit.getId());

        //when
        transitService.completeTransit(driver, transit.getId(), destination);

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(COMPLETED, loaded.getStatus());
        assertNotNull(loaded.getTariff());
        assertNotNull(loaded.getPrice());
        assertNotNull(loaded.getDriverFee());
        assertNotNull(loaded.getCompleteAt());
    }

    @Test
    void cannotCompleteNotStartedTransit() {
        //given
        AddressDTO addressTo = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                addressTo);
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());
        //and
        transitService.acceptTransit(driver, transit.getId());

        //expect
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> transitService.completeTransit(driver, transit.getId(), addressTo));

    }

    @Test
    void canRejectTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new AddressDTO("Polska", "Warszawa", "Młynarska", 20),
                new AddressDTO("Polska", "Warszawa", "Żytnia", 25));
        //and
        Long driver = aNearbyDriver("WU1212");
        //and
        transitService.publishTransit(transit.getId());

        //when
        transitService.rejectTransit(driver, transit.getId());

        //then
        TransitDTO loaded = transitService.loadTransit(transit.getId());
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, loaded.getStatus());
        assertNull(loaded.getAcceptedAt());
    }

    AddressDTO farAwayAddress(Transit t) {
        AddressDTO addressDTO = new AddressDTO("Dania", "Kopenhaga", "Mylve", 2);
        when(geocodingService.geocodeAddress(any())).thenReturn(new double[]{1000, 1000});
        when(geocodingService.geocodeAddress(t.getFrom())).thenReturn(new double[]{1, 1});
        return addressDTO;
    }

    Long aNearbyDriver(String plateNumber) {
        Driver driver = fixtures.aDriver();
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);
        driverSessionService.logIn(driver.getId(), plateNumber, VAN, "BRAND");
        driverTrackingService.registerPosition(driver.getId(), 1, 1, Instant.now());
        return driver.getId();
    }

    Long aFarAwayDriver(String plateNumber) {
        Driver driver = fixtures.aDriver();
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);
        driverSessionService.logIn(driver.getId(), plateNumber, VAN, "BRAND");
        driverTrackingService.registerPosition(driver.getId(), 1000, 1000, Instant.now());
        return driver.getId();
    }

    Transit requestTransitFromTo(AddressDTO pickup, AddressDTO destination) {
        return transitService.createTransit(fixtures.aTransitDTO(
                pickup,
                destination));
    }
}