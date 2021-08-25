package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.DriverFee;
import io.legacyfighter.cabs.entity.Transit;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.service.DriverFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CalculateDriverFeeIntegrationTest {

    @Autowired
    Fixtures fixtures;

    @Autowired
    DriverFeeService driverFeeService;

    @Test
    void shouldCalculateDriversFlatFee() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        Transit transit = fixtures.aTransit(driver, 60);
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

        //when
        Money fee = driverFeeService.calculateDriverFee(transit.getId());

        //then
        assertEquals(new Money(50), fee);
    }

    @Test
    void shouldCalculateDriversPercentageFee() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        Transit transit = fixtures.aTransit(driver, 80);
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.PERCENTAGE, 50);

        //when
        Money fee = driverFeeService.calculateDriverFee(transit.getId());

        //then
        assertEquals(new Money(40), fee);
    }

    @Test
    void shouldUseMinimumFee() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        Transit transit = fixtures.aTransit(driver, 10);
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.PERCENTAGE, 7, 5);

        //when
        Money fee = driverFeeService.calculateDriverFee(transit.getId());

        //then
        assertEquals(new Money(5), fee);
    }

}