package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.tracking.DriverTrackingService;
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
class DriverTrackingServiceIntegrationTest {

    static Instant NOON = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);
    static Instant NOON_FIVE = NOON.plus(5, ChronoUnit.MINUTES);

    @Autowired
    DriverTrackingService driverTrackingService;

    @Autowired
    Fixtures fixtures;

    @MockBean
    Clock clock;

    @Test
    void canCalculateTravelledDistanceFromShortTransit() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        itIsNoon();
        //and
        driverTrackingService.registerPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);
        driverTrackingService.registerPosition(driver.getId(), 53.31861111111111, -1.6997222222222223, NOON);
        driverTrackingService.registerPosition(driver.getId(), 53.32055555555556, -1.7297222222222221, NOON);

        //when
        Distance distance = driverTrackingService.calculateTravelledDistance(driver.getId(), NOON, NOON_FIVE);

        //then
        assertEquals("4.009km", distance.printIn("km"));
    }

    void itIsNoon() {
        when(clock.instant()).thenReturn(NOON);
    }

}
