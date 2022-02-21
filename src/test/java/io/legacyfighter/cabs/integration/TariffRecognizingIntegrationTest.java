package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.ui.TransitController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TariffRecognizingIntegrationTest {

    @Autowired
    Fixtures fixtures;

    @Autowired
    TransitController transitController;

    @Test
    void newYearsEveTariffShouldBeDisplayed() {
        //given
        Transit transit = fixtures.aCompletedTransitAt(60, LocalDateTime.of(2021, 12, 31, 8, 30).toInstant(OffsetDateTime.now().getOffset()));

        //when
        TransitDTO transitDTO = transitController.getTransit(transit.getId());

        //then
        assertEquals("Sylwester", transitDTO.getTariff());
        assertEquals(3.5f, transitDTO.getKmRate());

    }

    @Test
    void weekendTariffShouldBeDisplayed() {
        //given
        Transit transit = fixtures.aCompletedTransitAt(60, LocalDateTime.of(2021, 4, 17, 8, 30).toInstant(OffsetDateTime.now().getOffset()));

        //when
        TransitDTO transitDTO = transitController.getTransit(transit.getId());

        //then
        assertEquals("Weekend", transitDTO.getTariff());
        assertEquals(1.5f, transitDTO.getKmRate());
    }

    @Test
    void weekendPlusTariffShouldBeDisplayed() {
        //given
        Transit transit = fixtures.aCompletedTransitAt(60, LocalDateTime.of(2022, Month.FEBRUARY, 20, 6, 0).toInstant(OffsetDateTime.now().getOffset()));

        //when
        TransitDTO transitDTO = transitController.getTransit(transit.getId());

        //then
        assertEquals("Weekend+", transitDTO.getTariff());
        assertEquals(2.5f, transitDTO.getKmRate());
    }

    @Test
    void standardTariffShouldBeDisplayed() {
        //given
        Transit transit = fixtures.aCompletedTransitAt(60, LocalDateTime.of(2021, 4, 13, 22, 30).toInstant(OffsetDateTime.now().getOffset()));

        //when
        TransitDTO transitDTO = transitController.getTransit(transit.getId());

        //then
        assertEquals("Standard", transitDTO.getTariff());
        assertEquals(1.0f, transitDTO.getKmRate());
    }

}