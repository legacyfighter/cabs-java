package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance.TravelledDistanceService;
import io.legacyfighter.cabs.driverfleet.Driver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class CalculateDriverTravelledDistanceIntegrationTest {

    static Instant NOON = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);
    static Instant NOON_FIVE = NOON.plus(5, ChronoUnit.MINUTES);
    static final Instant NOON_TEN = NOON_FIVE.plus(5, ChronoUnit.MINUTES);

    @Autowired
    TravelledDistanceService travelledDistanceService;

    @Autowired
    Fixtures fixtures;

    @MockBean
    Clock clock;

    @Test
    void distanceIsZeroWhenZeroPositions() {
        //given
        Driver driver = fixtures.aDriver();

        //when
        Distance distance = travelledDistanceService.calculateDistance(driver.getId(), NOON, NOON_FIVE);

        //then
        assertEquals("0km", distance.printIn("km"));
    }

    @Test
    void travelledDistanceWithoutMultiplePositionsIzZero() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        itIsNoon();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);

        //when
        Distance distance = travelledDistanceService.calculateDistance(driver.getId(), NOON, NOON_FIVE);

        //then
        assertEquals("0km", distance.printIn("km"));
    }

    @Test
    void canCalculateTravelledDistanceFromShortTransit() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        itIsNoon();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);
        travelledDistanceService.addPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON);
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);

        //when
        Distance distance = travelledDistanceService.calculateDistance(driver.getId(), NOON, NOON_FIVE);

        //then
        assertEquals("4.009km", distance.printIn("km"));
    }

    @Test
    void canCalculateTravelledDistanceWithBreakWithin() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        itIsNoon();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);
        travelledDistanceService.addPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON);
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);
        //and
        itIsNoonFive();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON_FIVE);
        travelledDistanceService.addPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON_FIVE);
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON_FIVE);

        //when
        Distance distance = travelledDistanceService.calculateDistance(driver.getId(), NOON, NOON_FIVE);

        //then
        assertEquals("8.017km", distance.printIn("km"));
    }

    @Test
    void canCalculateTravelledDistanceWithMultipleBreaks() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        itIsNoon();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);
        travelledDistanceService.addPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON);
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);
        //and
        itIsNoonFive();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON_FIVE);
        travelledDistanceService.addPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON_FIVE);
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON_FIVE);
        //and
        itIsNoonTen();
        //and
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON_TEN);
        travelledDistanceService.addPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON_TEN);
        travelledDistanceService.addPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON_TEN);

        //when
        Distance distance = travelledDistanceService.calculateDistance(driver.getId(), NOON, NOON_TEN);

        //then
        assertEquals("12.026km", distance.printIn("km"));
    }

    void itIsNoon() {
        when(clock.instant()).thenReturn(NOON);
    }

    void itIsNoonFive() {
        when(clock.instant()).thenReturn(NOON_FIVE);
    }

    void itIsNoonTen() {
        when(clock.instant()).thenReturn(NOON_TEN);
    }

}
