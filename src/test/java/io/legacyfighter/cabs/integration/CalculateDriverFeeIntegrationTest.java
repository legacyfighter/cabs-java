package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.DriverFee;
import io.legacyfighter.cabs.entity.Transit;

import io.legacyfighter.cabs.repository.DriverFeeRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import io.legacyfighter.cabs.service.DriverFeeService;
import io.legacyfighter.cabs.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CalculateDriverFeeIntegrationTest {

    @Autowired
    DriverFeeService driverFeeService;

    @Autowired
    DriverFeeRepository feeRepository;

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    DriverService driverService;

    @Test
    void shouldCalculateDriversFlatFee() {
        //given
        Driver driver = aDriver();
        //and
        Transit transit = aTransit(driver, 60);
        //and
        driverHasFee(driver, DriverFee.FeeType.FLAT, 10);

        //when
        Integer fee = driverFeeService.calculateDriverFee(transit.getId());

        //then
        assertEquals(50, fee);
    }

    @Test
    void shouldCalculateDriversPercentageFee() {
        //given
        Driver driver = aDriver();
        //and
        Transit transit = aTransit(driver, 80);
        //and
        driverHasFee(driver, DriverFee.FeeType.PERCENTAGE, 50);

        //when
        Integer fee = driverFeeService.calculateDriverFee(transit.getId());

        //then
        assertEquals(40, fee);
    }

    @Test
    void shouldUseMinimumFee() {
        //given
        Driver driver = aDriver();
        //and
        Transit transit = aTransit(driver, 10);
        //and
        driverHasFee(driver, DriverFee.FeeType.PERCENTAGE, 7, 5);

        //when
        Integer fee = driverFeeService.calculateDriverFee(transit.getId());

        //then
        assertEquals(5, fee);
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

    Transit aTransit(Driver driver, Integer price) {
        Transit transit = new Transit();
        transit.setPrice(price);
        transit.setDriver(driver);
        transit.setDateTime(LocalDate.of(2020,10,20).atStartOfDay().toInstant(ZoneOffset.UTC));
        return transitRepository.save(transit);
    }

}