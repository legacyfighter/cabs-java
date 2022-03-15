package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.driverfleet.DriverFee;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.driverfleet.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CalculateDriverPeriodicPaymentsIntegrationTest {

    @Autowired
    DriverService driverService;

    @Autowired
    Fixtures fixtures;

    @Test
    void calculateMonthlyPayment() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        fixtures.transitDetails(driver, 60, LocalDateTime.of(2000, 10, 1, 6, 30));
        fixtures.transitDetails(driver, 70, LocalDateTime.of(2000, 10, 10, 2, 30));
        fixtures.transitDetails(driver, 80, LocalDateTime.of(2000, 10, 30, 6, 30));
        fixtures.transitDetails(driver, 60, LocalDateTime.of(2000, 11, 10, 1, 30));
        fixtures.transitDetails(driver, 30, LocalDateTime.of(2000, 11, 10, 1, 30));
        fixtures.transitDetails(driver, 15, LocalDateTime.of(2000, 12, 10, 2, 30));
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

        //when
        Money feeOctober = driverService.calculateDriverMonthlyPayment(driver.getId(), 2000, 10);
        //then
        assertEquals(new Money(180), feeOctober);

        //when
        Money feeNovember = driverService.calculateDriverMonthlyPayment(driver.getId(), 2000, 11);
        //then
        assertEquals(new Money(70), feeNovember);

        //when
        Money feeDecember = driverService.calculateDriverMonthlyPayment(driver.getId(), 2000, 12);
        //then
        assertEquals(new Money(5), feeDecember);
    }

    @Test
    void calculateYearlyPayment() {
        //given
        Driver driver = fixtures.aDriver();
        //and
        fixtures.transitDetails(driver, 60, LocalDateTime.of(2000, 10, 1, 6, 30));
        fixtures.transitDetails(driver, 70, LocalDateTime.of(2000, 10, 10, 2, 30));
        fixtures.transitDetails(driver, 80, LocalDateTime.of(2000, 10, 30, 6, 30));
        fixtures.transitDetails(driver, 60, LocalDateTime.of(2000, 11, 10, 1, 30));
        fixtures.transitDetails(driver, 30, LocalDateTime.of(2000, 11, 10, 1, 30));
        fixtures.transitDetails(driver, 15, LocalDateTime.of(2000, 12, 10, 2, 30));
        //and
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

        //when
        Map<Month, Money> payments = driverService.calculateDriverYearlyPayment(driver.getId(), 2000);

        //then
        assertEquals(new Money(0), payments.get(Month.JANUARY));
        assertEquals(new Money(0), payments.get(Month.FEBRUARY));
        assertEquals(new Money(0), payments.get(Month.MARCH));
        assertEquals(new Money(0), payments.get(Month.APRIL));
        assertEquals(new Money(0), payments.get(Month.MAY));
        assertEquals(new Money(0), payments.get(Month.JUNE));
        assertEquals(new Money(0), payments.get(Month.JULY));
        assertEquals(new Money(0), payments.get(Month.AUGUST));
        assertEquals(new Money(0), payments.get(Month.SEPTEMBER));
        assertEquals(new Money(180), payments.get(Month.OCTOBER));
        assertEquals(new Money(70), payments.get(Month.NOVEMBER));
        assertEquals(new Money(5), payments.get(Month.DECEMBER));
    }

}