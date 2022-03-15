package io.legacyfighter.cabs.pricing;

import io.legacyfighter.cabs.ride.RequestForTransit;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.money.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateTransitPriceTest {

    @Test
    void calculatePriceOnRegularDay() {
        //given
        //friday
        RequestForTransit requestForTransit = transitWasOnDoneOnFriday(Distance.ofKm(20));
        //when
        Money price = requestForTransit.getEstimatedPrice();

        //then
        assertEquals(new Money(2900), price); //29.00
    }

    @Test
    void calculatePriceOnSunday() {
        //given
        RequestForTransit requestForTransit = transitWasDoneOnSunday(Distance.ofKm(20));

        //when
        Money price = requestForTransit.getEstimatedPrice();

        //then
        assertEquals(new Money(3800), price); //38.00
    }

    @Test
    void calculatePriceOnNewYearsEve() {
        //given
        RequestForTransit requestForTransit = transitWasDoneOnNewYearsEve(Distance.ofKm(20));

        //when
        Money price = requestForTransit.getEstimatedPrice();

        //then
        assertEquals(new Money(8100), price); //81.00
    }

    @Test
    void calculatePriceOnSaturday() {
        //given
        RequestForTransit requestForTransit = transitWasDoneOnSaturday(Distance.ofKm(20));

        //when
        Money price = requestForTransit.getEstimatedPrice();

        //then
        assertEquals(new Money(3800), price); //38.00
    }

    @Test
    void calculatePriceOnSaturdayNight() {
        //given
        RequestForTransit requestForTransit = transitWasDoneOnSaturdayNight(Distance.ofKm(20));

        //when
        Money price = requestForTransit.getEstimatedPrice();

        //then
        assertEquals(new Money(6000), price); //60.00
    }

    RequestForTransit transitWasOnDoneOnFriday(Distance distance) {
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 16, 8, 30));
        RequestForTransit requestForTransit = new RequestForTransit(tariff, distance);
        return requestForTransit;
    }

    RequestForTransit transitWasDoneOnNewYearsEve(Distance distance) {
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 12, 31, 8, 30));
        RequestForTransit requestForTransit = new RequestForTransit(tariff, distance);
        return requestForTransit;
    }

    RequestForTransit transitWasDoneOnSaturday(Distance distance) {
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 17, 8, 30));
        RequestForTransit requestForTransit = new RequestForTransit(tariff, distance);
        return requestForTransit;
    }

    RequestForTransit transitWasDoneOnSunday(Distance distance) {
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 18, 8, 30));
        RequestForTransit requestForTransit = new RequestForTransit(tariff, distance);
        return requestForTransit;
    }

    RequestForTransit transitWasDoneOnSaturdayNight(Distance distance) {
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 17, 19, 30));
        RequestForTransit requestForTransit = new RequestForTransit(tariff, distance);
        return requestForTransit;
    }


}
