package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.entity.*;
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
        Integer feeOctober = driverService.calculateDriverMonthlyPayment(driver.getId(), 2000, 10);
        //then
        assertEquals(180, feeOctober);

        //when
        Integer feeNovember = driverService.calculateDriverMonthlyPayment(driver.getId(), 2000, 11);
        //then
        assertEquals(70, feeNovember);

        //when
        Integer feeDecember = driverService.calculateDriverMonthlyPayment(driver.getId(), 2000, 12);
        //then
        assertEquals(5, feeDecember);
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
        Map<Month, Integer> payments = driverService.calculateDriverYearlyPayment(driver.getId(), 2000);

        //then
        assertEquals(0, payments.get(Month.JANUARY));
        assertEquals(0, payments.get(Month.FEBRUARY));
        assertEquals(0, payments.get(Month.MARCH));
        assertEquals(0, payments.get(Month.APRIL));
        assertEquals(0, payments.get(Month.MAY));
        assertEquals(0, payments.get(Month.JUNE));
        assertEquals(0, payments.get(Month.JULY));
        assertEquals(0, payments.get(Month.AUGUST));
        assertEquals(0, payments.get(Month.SEPTEMBER));
        assertEquals(180, payments.get(Month.OCTOBER));
        assertEquals(70, payments.get(Month.NOVEMBER));
        assertEquals(5, payments.get(Month.DECEMBER));
    }

    public Transit aTransit(Driver driver, Integer price, LocalDateTime when) {
        Transit transit = new Transit();
        transit.setPrice(price);
        transit.setDriver(driver);
        transit.setDateTime(when.toInstant(ZoneOffset.UTC));
        return transitRepository.save(transit);
    }

    DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType, int amount, Integer min) {
        DriverFee driverFee = new DriverFee();
        driverFee.setDriver(driver);
        driverFee.setAmount(amount);
        driverFee.setFeeType(feeType);
        driverFee.setMin(min);
        return feeRepository.save(driverFee);
    }

    DriverFee driverHasFee(Driver driver, DriverFee.FeeType feeType , int amount) {
        return driverHasFee(driver, feeType, amount, 0);
    }

    Driver aDriver() {
        return driverService.createDriver("FARME100165AB5EW", "Kowalsi", "Janusz", Driver.Type.REGULAR, Driver.Status.ACTIVE, "");
    }

}