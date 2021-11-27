package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.distance.Distance;
import org.junit.jupiter.api.Test;


import static io.legacyfighter.cabs.distance.Distance.ofKm;
import static io.legacyfighter.cabs.entity.Transit.Status.*;
import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class TransitLifeCycleTest {

    @Test
    void canCreateTransit() {
        //when
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));

        //then
        assertNull(transit.getCarType());
        assertNull(transit.getPrice());
        assertEquals("Polska", transit.getFrom().getCountry());
        assertEquals("Warszawa", transit.getFrom().getCity());
        assertEquals("Młynarska", transit.getFrom().getStreet());
        assertEquals(20, transit.getFrom().getBuildingNumber());
        assertEquals("Polska", transit.getTo().getCountry());
        assertEquals("Warszawa", transit.getTo().getCity());
        assertEquals("Żytnia", transit.getTo().getStreet());
        assertEquals(25, transit.getTo().getBuildingNumber());
        assertEquals(DRAFT, transit.getStatus());
        assertNotNull(transit.getTariff());
        assertNotEquals(0, transit.getTariff().getKmRate());
        assertNotNull(transit.getDateTime());
    }

    @Test
    void canChangeTransitDestination() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //when
        transit.changeDestinationTo(
                new Address("Polska", "Warszawa", "Mazowiecka", 30), ofKm(20));

        //then
        assertEquals(30, transit.getTo().getBuildingNumber());
        assertEquals("Mazowiecka", transit.getTo().getStreet());
        assertNotNull(transit.getEstimatedPrice());
        assertNull(transit.getPrice());
    }

    @Test
    void cannotChangeDestinationWhenTransitIsCompleted() {
        //given
        Address destination = new Address("Polska", "Warszawa", "Żytnia", 25);
        //and
        Driver driver = new Driver();
        //and
        Transit transit = requestTransitFromTo(new Address("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());
        //and
        transit.start(now());
        //and
        transit.completeAt(now(), destination, ofKm(20));

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.changeDestinationTo(
                        new Address("Polska", "Warszawa", "Żytnia", 23), ofKm(20)));
    }

    @Test
    void canChangePickupPlace() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));

        //when
        transit.changePickupTo(
                new Address("Polska", "Warszawa", "Puławska", 28), ofKm(20), 0.2);

        //then
        assertEquals(28, transit.getFrom().getBuildingNumber());
        assertEquals("Puławska", transit.getFrom().getStreet());
    }

    @Test
    void cannotChangePickupPlaceAfterTransitIsAccepted() {
        //given
        Address destination = new Address("Polska", "Warszawa", "Żytnia", 25);
        //and
        Driver driver = new Driver();
        //and
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        Address changedTo = new Address("Polska", "Warszawa", "Żytnia", 27);
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.changePickupTo(changedTo, ofKm(20.1f), 0.1));

        //and
        transit.start(now());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.changePickupTo(changedTo, ofKm(20.11f), 0.11));

        //and
        transit.completeAt(now(), destination, ofKm(20));
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.changePickupTo(changedTo, ofKm(20.12f), 0.12));
    }

    @Test
    void cannotChangePickupPlaceMoreThanThreeTimes() {
        //given
        Transit transit = requestTransitFromTo(new Address("Polska", "Warszawa", "Młynarska", 20), new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        transit.changePickupTo(
                new Address("Polska", "Warszawa", "Żytnia", 26), ofKm(20.1f), 0.1d);
        //and
        transit.changePickupTo(
                new Address("Polska", "Warszawa", "Żytnia", 27), ofKm(20.2f), 0.2d);
        //and
        transit.changePickupTo(
                new Address("Polska", "Warszawa", "Żytnia", 28), ofKm(20.22f), 0.22d);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        transit.changePickupTo(
                                new Address("Polska", "Warszawa", "Żytnia", 29), ofKm(20.3f), 0.23d));
    }

    @Test
    void cannotChangePickupPlaceWhenItIsFarWayFromOriginal() {
        //given
        Transit transit = requestTransitFromTo(new Address("Polska", "Warszawa", "Młynarska", 20), new Address("Polska", "Warszawa", "Żytnia", 25));

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        transit.changePickupTo(new Address(), ofKm(20), 50));
    }

    @Test
    void canCancelTransit() {
        //given
        Transit transit = requestTransitFromTo(new Address("Polska", "Warszawa", "Młynarska", 20), new Address("Polska", "Warszawa", "Żytnia", 25));

        //when
        transit.cancel();

        //then
        assertEquals(CANCELLED, transit.getStatus());
    }

    @Test
    void cannotCancelTransitAfterItWasStarted() {
        //given
        Address destination =
                new Address("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());

        //and
        transit.start(now());
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.cancel());

        //and
        transit.completeAt(now(), destination, ofKm(20));
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.cancel());
    }

    @Test
    void canPublishTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));

        //when
        transit.publishAt(now());

        //then
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, transit.getStatus());
        assertNotNull(transit.getPublished());
    }

    @Test
    void canAcceptTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);

        //when
        transit.acceptBy(driver, now());
        //then
        assertEquals(TRANSIT_TO_PASSENGER, transit.getStatus());
        assertNotNull(transit.getAcceptedAt());
    }

    @Test
    void onlyOneDriverCanAcceptTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        Driver driver = new Driver();
        //and
        Driver secondDriver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.acceptBy(secondDriver, now()));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoAlreadyRejected() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.rejectBy(driver);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.acceptBy(driver, now()));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoHasNotSeenProposal() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.acceptBy(driver, now()));
    }

    @Test
    void canStartTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());
        //when
        transit.start(now());

        //then
        assertEquals(Transit.Status.IN_TRANSIT, transit.getStatus());
        assertNotNull(transit.getStarted());
    }

    @Test
    void cannotStartNotAcceptedTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        transit.publishAt(now());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.start(now()));
    }

    @Test
    void canCompleteTransit() {
        //given
        Address destination =
                new Address("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                destination);
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());
        //and
        transit.start(now());

        //when
        transit.completeAt(now(), destination, ofKm(20));

        //then
        assertEquals(COMPLETED, transit.getStatus());
        assertNotNull(transit.getTariff());
        assertNotNull(transit.getPrice());
        assertNotNull(transit.getCompleteAt());
    }

    @Test
    void cannotCompleteNotStartedTransit() {
        //given
        Address addressTo =
                new Address("Polska", "Warszawa", "Żytnia", 25);
        //and
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                addressTo);
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());
        //and
        transit.proposeTo(driver);
        //and
        transit.acceptBy(driver, now());

        //expect
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> transit.completeAt(now(), addressTo, ofKm(20)));
    }

    @Test
    void canRejectTransit() {
        //given
        Transit transit = requestTransitFromTo(
                new Address("Polska", "Warszawa", "Młynarska", 20),
                new Address("Polska", "Warszawa", "Żytnia", 25));
        //and
        Driver driver = new Driver();
        //and
        transit.publishAt(now());

        //when
        transit.rejectBy(driver);

        //then
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, transit.getStatus());
        assertNull(transit.getAcceptedAt());
    }

    Transit requestTransitFromTo(Address pickup, Address destination) {
        return new Transit(pickup, destination, new Client(), null, now(), Distance.ZERO);
    }
}