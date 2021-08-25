package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.money.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.legacyfighter.cabs.entity.Transit.Status.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateTransitPriceTest {

    @Test
    void cannotCalculatePriceWhenTransitIsCancelled() {
        //given
        Transit transit = transit(CANCELLED, 20);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(transit::calculateFinalCosts);
    }

    @Test
    void cannotEstimatePriceWhenTransitIsCompleted() {
        //given
        Transit transit = transit(COMPLETED, 20);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(transit::estimateCost);
    }

    @Test
    void calculatePriceOnRegularDay() {
        //given
        Transit transit = transit(COMPLETED, 20);

        //friday
        transitWasOnDoneOnFriday(transit);
        //when
        Money price = transit.calculateFinalCosts();

        //then
        assertEquals( new Money(2900), price); //29.00
    }

    @Test
    void estimatePriceOnRegularDay() {
        //given
        Transit transit = transit(DRAFT, 20);

        //friday
        transitWasOnDoneOnFriday(transit);
        //when
        Money price = transit.estimateCost();

        //then
        assertEquals(new Money(2900), price); //29.00
    }


    Transit transit(Transit.Status status, int km) {
        Transit transit = new Transit();
        transit.setDateTime(Instant.now());
        transit.setStatus(DRAFT);
        transit.setKm(km);
        transit.setStatus(status);
        return transit;
    }

    void transitWasOnDoneOnFriday(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2021, 4, 16, 8, 30).toInstant(ZoneOffset.UTC));
    }

}