package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.AddressRepository;
import io.legacyfighter.cabs.repository.ClientRepository;
import io.legacyfighter.cabs.repository.DriverFeeRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import io.legacyfighter.cabs.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CalculateDriverPeriodicPaymentsIntegrationTest {

    @Autowired
    DriverService driverService;

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    DriverFeeRepository feeRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ClientRepository clientRepository;

    @Test
    void calculateMonthlyPayment() {
        //given
        Driver driver = aDriver();
        //and
        aTransit(driver, 60, LocalDateTime.of(2000, 10, 1, 6, 30));
        aTransit(driver, 70, LocalDateTime.of(2000, 10, 10, 2, 30));
        aTransit(driver, 80, LocalDateTime.of(2000, 10, 30, 6, 30));
        aTransit(driver, 60, LocalDateTime.of(2000, 11, 10, 1, 30));
        aTransit(driver, 30, LocalDateTime.of(2000, 11, 10, 1, 30));
        aTransit(driver, 15, LocalDateTime.of(2000, 12, 10, 2, 30));
        //and
        driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

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
        Driver driver = aDriver();
        //and
        aTransit(driver, 60, LocalDateTime.of(2000, 10, 1, 6, 30));
        aTransit(driver, 70, LocalDateTime.of(2000, 10, 10, 2, 30));
        aTransit(driver, 80, LocalDateTime.of(2000, 10, 30, 6, 30));
        aTransit(driver, 60, LocalDateTime.of(2000, 11, 10, 1, 30));
        aTransit(driver, 30, LocalDateTime.of(2000, 11, 10, 1, 30));
        aTransit(driver, 15, LocalDateTime.of(2000, 12, 10, 2, 30));
        //and
        driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

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

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when) {
        Transit transit = new Transit();
        transit.setPrice(new Money(price));
        transit.setDriver(driver);
        transit.setDateTime(when.toInstant(ZoneOffset.UTC));
        return transitRepository.save(transit);
    }

    DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType, int amount, Integer min) {
        DriverFee driverFee = new DriverFee();
        driverFee.setDriver(driver);
        driverFee.setAmount(amount);
        driverFee.setFeeType(feeType);
        driverFee.setMin(new Money(min));
        return feeRepository.save(driverFee);
    }

    DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType , int amount) {
        return driverHasFee(driver, feeType, amount, 0);
    }

    Driver aDriver() {
        return driverService.createDriver("FARME100165AB5EW", "Kowalsi", "Janusz", Driver.Type.REGULAR, Driver.Status.ACTIVE, "");
    }

}