package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.pricing.Tariff;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.legacyfighter.cabs.geolocation.Distance.ofKm;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransitTest {

    @Test
    void canChangeTransitDestination() {
        //given
        Transit transit = transit();

        //expect
        transit.changeDestination(ofKm(20));

        //then
        assertEquals(ofKm(20), transit.getDistance());
    }

    @Test
    void cannotChangeDestinationWhenTransitIsCompleted() {
        //given
        Transit transit = transit();
        //and
        transit.completeAt(ofKm(20));

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transit.changeDestination(ofKm(20)));
    }

    @Test
    void canCompleteTransit() {
        Transit transit = transit();
        //and
        transit.completeAt(Distance.ofKm(20));

        //then
        assertEquals(Transit.Status.COMPLETED, transit.getStatus());
    }


    Transit transit() {
        return new Transit(Tariff.ofTime(LocalDateTime.now()), UUID.randomUUID());
    }
}