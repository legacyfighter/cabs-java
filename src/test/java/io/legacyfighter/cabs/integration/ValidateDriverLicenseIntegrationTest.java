package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.driverfleet.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.legacyfighter.cabs.driverfleet.Driver.Status.ACTIVE;
import static io.legacyfighter.cabs.driverfleet.Driver.Status.INACTIVE;
import static io.legacyfighter.cabs.driverfleet.Driver.Type.REGULAR;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidateDriverLicenseIntegrationTest {

    @Autowired
    DriverService driverService;

    @Test
    void cannotCreateActiveDriverWithInvalidLicense() {
        //expect
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> createActiveDriverWithLicense("invalidLicense"));
    }

    @Test
    void canCreateActiveDriverWithValidLicense() {
        //when
        Driver driver = createActiveDriverWithLicense("FARME100165AB5EW");

        //then
        DriverDTO loaded = load(driver);
        assertEquals("FARME100165AB5EW", loaded.getDriverLicense());
        assertEquals(ACTIVE, loaded.getStatus());
    }

    @Test
    void canCreateInactiveDriverWithInvalidLicense() {
        //when
        Driver driver = createInactiveDriverWithLicense("invalidlicense");

        //then
        DriverDTO loaded = load(driver);
        assertEquals("invalidlicense", loaded.getDriverLicense());
        assertEquals(INACTIVE, loaded.getStatus());
    }

    @Test
    void canChangeLicenseForValidOne() {
        //given
        Driver driver = createActiveDriverWithLicense("FARME100165AB5EW");

        //when
        changeLicenseTo("99999740614992TL", driver);

        //then
        DriverDTO loaded = load(driver);
        assertEquals("99999740614992TL", loaded.getDriverLicense());
    }

    @Test
    void cannotChangeLicenseForInvalidOne() {
        //given
        Driver driver = createActiveDriverWithLicense("FARME100165AB5EW");

        //expect
        assertThatExceptionOfType(IllegalArgumentException.class)
    .isThrownBy(() -> changeLicenseTo("invalid", driver));
    }

    @Test
    void canActivateDriverWithValidLicense() {
        //given
        Driver driver = createInactiveDriverWithLicense("FARME100165AB5EW");

        //when
        activate(driver);

        //then
        DriverDTO loaded = load(driver);
        assertEquals(ACTIVE, loaded.getStatus());
    }

    @Test
    void cannotActivateDriverWithInvalidLicense() {
        //given
        Driver driver = createInactiveDriverWithLicense("invalid");

        //exoect
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> activate(driver));
    }

    Driver createActiveDriverWithLicense(String license) {
        return driverService.createDriver(license,"Kowalski", "Jan", REGULAR, ACTIVE, "photo");
    }

    Driver createInactiveDriverWithLicense(String license) {
        return driverService.createDriver(license,"Kowalski", "Jan", REGULAR, INACTIVE, "photo");
    }

    DriverDTO load(Driver driver) {
        DriverDTO loaded = driverService.loadDriver(driver.getId());
        return loaded;
    }

    void changeLicenseTo(String newLicense, Driver driver) {
        driverService.changeLicenseNumber(newLicense, driver.getId());
    }

    void activate(Driver driver) {
        driverService.changeDriverStatus(driver.getId(), ACTIVE);
    }
}