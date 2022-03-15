package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.AddressMatcher;
import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.ride.TransitDTO;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.ride.RideService;
import io.legacyfighter.cabs.ride.details.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;

import static io.legacyfighter.cabs.carfleet.CarClass.VAN;
import static io.legacyfighter.cabs.ride.details.Status.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransitLifeCycleIntegrationTest {

    @Autowired
    Fixtures fixtures;

    @Autowired
    RideService rideService;

    @MockBean
    GeocodingService geocodingService;

    @BeforeEach
    public void setup() {
        fixtures.anActiveCarCategory(VAN);
    }

    @Test
    void canCreateTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);

        //when
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
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
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);

        //when
        AddressDTO newDestination = newAddress("Polska", "Warszawa", "Mazowiecka", 30);
        //and
        rideService.changeTransitAddressTo(transit.getRequestId(), newDestination);

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(30, loaded.getTo().getBuildingNumber());
        assertEquals("Mazowiecka", loaded.getTo().getStreet());
        assertNotNull(loaded.getEstimatedPrice());
        assertNull(loaded.getPrice());
    }

    @Test
    void cannotChangeDestinationWhenTransitIsCompleted() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());
        //and
        rideService.startTransit(driver, transit.getRequestId());
        //and
        rideService.completeTransit(driver, transit.getRequestId(), destination);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.changeTransitAddressTo(transit.getRequestId(),
                        newAddress("Polska", "Warszawa", "Żytnia", 23)));
    }

    @Test
    void canChangePickupPlace() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //when
        AddressDTO newPickup = newPickupAddress("Puławska", 28);
        //and
        rideService.changeTransitAddressFrom(transit.getRequestId(),
                newPickup);

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(28, loaded.getFrom().getBuildingNumber());
        assertEquals("Puławska", loaded.getFrom().getStreet());
    }

    @Test
    void cannotChangePickupPlaceAfterTransitIsAccepted() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        AddressDTO changedTo = newPickupAddress(10);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.changeTransitAddressFrom(transit.getRequestId(), changedTo));

        //and
        rideService.startTransit(driver, transit.getRequestId());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.changeTransitAddressFrom(transit.getRequestId(), changedTo));

        //and
        rideService.completeTransit(driver, transit.getRequestId(), destination);
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.changeTransitAddressFrom(transit.getRequestId(), changedTo));
    }

    @Test
    void cannotChangePickupPlaceMoreThanThreeTimes() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //and
        AddressDTO newPickup1 = newPickupAddress(10);
        rideService.changeTransitAddressFrom(transit.getRequestId(), newPickup1);
        //and
        AddressDTO newPickup2 = newPickupAddress(11);
        rideService.changeTransitAddressFrom(transit.getRequestId(), newPickup2);
        //and
        AddressDTO newPickup3 = newPickupAddress(12);
        rideService.changeTransitAddressFrom(transit.getRequestId(), newPickup3);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.changeTransitAddressFrom(transit.getRequestId(),
                        newPickupAddress(13)));
    }

    @Test
    void cannotChangePickupPlaceWhenItIsFarWayFromOriginal() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        rideService.changeTransitAddressFrom(transit.getRequestId(), farAwayAddress()));
    }

    @Test
    void canCancelTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //when
        rideService.cancelTransit(transit.getRequestId());

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(CANCELLED, loaded.getStatus());
    }

    @Test
    void cannotCancelTransitAfterItWasStarted() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());

        //and
        rideService.startTransit(driver, transit.getRequestId());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.cancelTransit(transit.getRequestId()));

        //and
        rideService.completeTransit(driver, transit.getRequestId(), destination);
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.cancelTransit(transit.getRequestId()));
    }

    @Test
    void canPublishTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(pickup, destination);

        //when
        rideService.publishTransit(transit.getRequestId());

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, loaded.getStatus());
        assertNotNull(loaded.getPublished());
    }

    @Test
    void canAcceptTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(pickup, destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //when
        rideService.acceptTransit(driver, transit.getRequestId());

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(TRANSIT_TO_PASSENGER, loaded.getStatus());
        assertNotNull(loaded.getAcceptedAt());
    }

    @Test
    void onlyOneDriverCanAcceptTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        Long secondDriver = aNearbyDriver(pickup);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.acceptTransit(secondDriver, transit.getRequestId()));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoAlreadyRejected() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //and
        rideService.rejectTransit(driver, transit.getRequestId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.acceptTransit(driver, transit.getRequestId()));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoHasNotSeenProposal() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long farAwayDriver = aFarAwayDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(pickup, destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.acceptTransit(farAwayDriver, transit.getRequestId()));
    }

    @Test
    void canStartTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(pickup, destination);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());
        //when
        rideService.startTransit(driver, transit.getRequestId());

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(Status.IN_TRANSIT, loaded.getStatus());
        assertNotNull(loaded.getStarted());
    }

    @Test
    void cannotStartNotAcceptedTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(pickup, destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rideService.startTransit(driver, transit.getRequestId()));
    }

    @Test
    void canCompleteTransit() {
        //given
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());
        //and
        rideService.startTransit(driver, transit.getRequestId());

        //when
        rideService.completeTransit(driver, transit.getRequestId(), destination);

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
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
        AddressDTO pickup = new AddressDTO(null, null, null, 0);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                addressTo);
        //and
        rideService.publishTransit(transit.getRequestId());
        //and
        rideService.acceptTransit(driver, transit.getRequestId());

        //expect
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rideService.completeTransit(driver, transit.getRequestId(), addressTo));
    }

    @Test
    void canRejectTransit() {
        //given
        AddressDTO pickup = new AddressDTO("Polska", "Warszawa", "Młynarska", 20);
        //and
        AddressDTO destination = new AddressDTO("Polska", "Warszawa", "Żytnia", 25);
        //and
        Long driver = aNearbyDriver(pickup);
        //and
        TransitDTO transit = requestTransitFromTo(
                pickup,
                destination);
        //and
        rideService.publishTransit(transit.getRequestId());

        //when
        rideService.rejectTransit(driver, transit.getRequestId());

        //then
        TransitDTO loaded = rideService.loadTransit(transit.getRequestId());
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, loaded.getStatus());
        assertNull(loaded.getAcceptedAt());
    }

    AddressDTO newAddress(String country, String city, String street, int buildingNumber) {
        AddressDTO addressDTO = fixtures.anAddress(geocodingService, country, city, street, buildingNumber);
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(addressDTO)))).thenReturn(new double[]{1,1});
        return addressDTO;
    }

    AddressDTO farAwayAddress() {
        AddressDTO addressDTO = newAddress("Dania", "Kopenhaga", "Mylve", 2);
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(addressDTO)))).thenReturn(new double[]{10000, 21211321});
        return addressDTO;
    }

    Long aNearbyDriver(AddressDTO from) {
        return fixtures.aNearbyDriver(geocodingService, from.toAddressEntity(), 1, 1).getId();
    }

    Long aFarAwayDriver(AddressDTO address) {
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(address)))).thenReturn(new double[]{20000000,100000000});
        return fixtures.aNearbyDriver("DW MARIO", 1000000000, 1000000000, VAN, Instant.now(), "BRAND").getId();
    }

    TransitDTO requestTransitFromTo(AddressDTO pickupDto, AddressDTO destination) {
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(destination)))).thenReturn(new double[]{1,1});
        return rideService.createTransit(fixtures.aTransitDTO(
                pickupDto,
                destination));
    }

    AddressDTO newPickupAddress(int buildingNumber) {
        AddressDTO newPickup = new AddressDTO("Polska", "Warszawa", "Mazowiecka", buildingNumber);
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(newPickup)))).thenReturn(new double[]{1, 1});
        return newPickup;
    }

    AddressDTO newPickupAddress(String street, int buildingNumber) {
        AddressDTO newPickup = new AddressDTO("Polska", "Warszawa", street, buildingNumber);
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(newPickup)))).thenReturn(new double[]{1, 1});
        return newPickup;
    }
}
