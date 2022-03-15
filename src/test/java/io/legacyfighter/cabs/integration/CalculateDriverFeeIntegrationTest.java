package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.driverfleet.DriverFee;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.driverfleet.DriverFeeService;
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
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

        //when
        Money fee = driverFeeService.calculateDriverFee(new Money(60), driver.getId());

        //then
        assertEquals(new Money(50), fee);
    }

    @Test
    void shouldCalculateDriversPercentageFee() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.PERCENTAGE, 50);

        //when
        Money fee = driverFeeService.calculateDriverFee(new Money(80), driver.getId());

        //then
        assertEquals(new Money(40), fee);
    }

    @Test
    void shouldUseMinimumFee() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.PERCENTAGE, 7, 5);

        //when
        Money fee = driverFeeService.calculateDriverFee(new Money(10), driver.getId());

        //then
        assertEquals(new Money(5), fee);
    }

}