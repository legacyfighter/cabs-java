package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.distance.Distance;
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

    @Test
    void calculatePriceOnSunday() {
        //given
        Transit transit = transit(COMPLETED, 20);
        //and
        transitWasDoneOnSunday(transit);

        //when
        Money price = transit.calculateFinalCosts();

        //then
        assertEquals(new Money(3800), price); //38.00
    }

    @Test
    void calculatePriceOnNewYearsEve() {
        //given
        Transit transit = transit(COMPLETED, 20);
        //and
        transitWasDoneOnNewYearsEve(transit);

        //when
        Money price = transit.calculateFinalCosts();

        //then
        assertEquals(new Money(8100), price); //81.00
    }

    @Test
    void calculatePriceOnSaturday() {
        //given
        Transit transit = transit(COMPLETED, 20);
        //and
        transitWasDoneOnSaturday(transit);

        //when
        Money price = transit.calculateFinalCosts();

        //then
        assertEquals(new Money(3800), price); //38.00
    }

    @Test
    void calculatePriceOnSaturdayNight() {
        //given
        Transit transit = transit(COMPLETED, 20);
        //and
        transitWasDoneOnSaturdayNight(transit);

        //when
        Money price = transit.calculateFinalCosts();

        //then
        assertEquals(new Money(6000), price); //60.00
    }

    Transit transit(Transit.Status status, int km) {
        Transit transit = new Transit();
        transit.setDateTime(Instant.now());
        transit.setStatus(DRAFT);
        transit.setKm(Distance.ofKm(km));
        transit.setStatus(status);
        return transit;
    }

    void transitWasOnDoneOnFriday(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2021, 4, 16, 8, 30).toInstant(ZoneOffset.UTC));
    }

    void transitWasDoneOnNewYearsEve(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2021, 12, 31, 8, 30).toInstant(ZoneOffset.UTC));
    }

    void transitWasDoneOnSaturday(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2021, 4, 17, 8, 30).toInstant(ZoneOffset.UTC));
    }

    void transitWasDoneOnSunday(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2021, 4, 18, 8, 30).toInstant(ZoneOffset.UTC));
    }

    void transitWasDoneOnSaturdayNight(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2021, 4, 17, 19, 30).toInstant(ZoneOffset.UTC));
    }

    void transitWasDoneIn2018(Transit transit) {
        transit.setDateTime(LocalDateTime.of(2018, 1,1, 8, 30).toInstant(ZoneOffset.UTC));
    }


}