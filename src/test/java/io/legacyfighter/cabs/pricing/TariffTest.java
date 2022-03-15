package io.legacyfighter.cabs.pricing;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariff;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static io.legacyfighter.cabs.geolocation.Distance.ofKm;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TariffTest {
    @Test
    void regularTariffShouldBeDisplayedAndCalculated() {
        //given
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 16, 8, 30));

        //expect
        assertEquals(new Money(2900), tariff.calculateCost(ofKm(20))); //29.00
        assertEquals("Standard", tariff.getName());
        assertEquals(1.0f, tariff.getKmRate());
    }

    @Test
    void sundayTariffShouldBeDisplayedAndCalculated() {
        //expect
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 18, 8, 30));

        //expect
        assertEquals(new Money(3800), tariff.calculateCost(ofKm(20))); //38.00
        assertEquals("Weekend", tariff.getName());
        assertEquals(1.5f, tariff.getKmRate());
    }

    @Test
    void newYearsEveTariffShouldBeDisplayedAndCalculated() {
        //given
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 12, 31, 8, 30));

        //expect
        assertEquals(new Money(8100), tariff.calculateCost(ofKm(20))); //81.00
        assertEquals("Sylwester", tariff.getName());
        assertEquals(3.5f, tariff.getKmRate());
    }

    @Test
    void saturdayTariffShouldBeDisplayedAndCalculated() {
        //given
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 17, 8, 30));

        //expect
        assertEquals(new Money(3800), tariff.calculateCost(ofKm(20))); //38.00
        assertEquals("Weekend", tariff.getName());
        assertEquals(1.5f, tariff.getKmRate());
    }

    @Test
    void saturdayNightTariffShouldBeDisplayedAndCalculated() {
        //given
        Tariff tariff = Tariff.ofTime(LocalDateTime.of(2021, 4, 17, 19, 30));

        //expect
        assertEquals(new Money(6000), tariff.calculateCost(ofKm(20))); //60.00
        assertEquals("Weekend+", tariff.getName());
        assertEquals(2.5f, tariff.getKmRate());
    }
}
