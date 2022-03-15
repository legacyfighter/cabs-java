package io.legacyfighter.cabs.driverfleet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DriverLicenseTest {

    @Test
    void cannotCreateInvalidLicense() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> DriverLicense.withLicense("invalid"));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> DriverLicense.withLicense(""));
    }

    @Test
    void canCreateValidLicense() {
        //when
        DriverLicense license = DriverLicense.withLicense("FARME100165AB5EW");

        //then
        assertEquals("FARME100165AB5EW", license.asString());
    }

    @Test
    void canCreateInvalidLicenseExplicitly() {
        //when
        DriverLicense license = DriverLicense.withoutValidation("invalid");

        //then
        assertEquals("invalid", license.asString());
    }

}