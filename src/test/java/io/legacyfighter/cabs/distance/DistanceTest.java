package io.legacyfighter.cabs.distance;

import io.legacyfighter.cabs.geolocation.Distance;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

    @Test
    void cannotUnderstandInvalidUnit() {
        //expect
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Distance.ofKm(2000).printIn("invalid"));
    }

    @Test
    void canConvertToFloat() {
        //expect
        assertEquals(2000f, Distance.ofKm(2000).toKmInFloat());
        assertEquals(0f, Distance.ofKm(0).toKmInFloat());
        assertEquals(312.22f, Distance.ofKm(312.22f).toKmInFloat());
        assertEquals(2f, Distance.ofKm(2).toKmInFloat());
    }

    @Test
    void canConvertToDouble() {
        //expect
        assertEquals(2000d, Distance.ofKm(2000).toKmInDouble());
        assertEquals(0d, Distance.ofKm(0).toKmInDouble());
        assertEquals(312.22d, Distance.ofKm(312.22d).toKmInDouble());
        assertEquals(2d, Distance.ofKm(2).toKmInDouble());
    }

    @Test
    void canRepresentDistanceAsMeters() {
        //expect
        assertEquals("2000000m", Distance.ofKm(2000).printIn("m"));
        assertEquals("0m", Distance.ofKm(0).printIn("m"));
        assertEquals("312220m", Distance.ofKm(312.22f).printIn("m"));
        assertEquals("2000m", Distance.ofKm(2).printIn("m"));
    }

    @Test
    void canRepresentDistanceAsKm() {
        //expect
        assertEquals("2000km", Distance.ofKm(2000).printIn("km"));
        assertEquals("0km", Distance.ofKm(0).printIn("km"));
        assertEquals("312.220km", Distance.ofKm(312.22f).printIn("km"));
        assertEquals("312.221km", Distance.ofKm(312.221111232313f).printIn("km"));
        assertEquals("2km", Distance.ofKm(2).printIn("km"));
    }

    @Test
    void canRepresentDistanceAsMiles() {
        //expect
        assertEquals("1242.742miles", Distance.ofKm(2000).printIn("miles"));
        assertEquals("0miles", Distance.ofKm(0).printIn("miles"));
        assertEquals("194.005miles", Distance.ofKm(312.22f).printIn("miles"));
        assertEquals("194.005miles", Distance.ofKm(312.221111232313f).printIn("miles"));
        assertEquals("1.243miles", Distance.ofKm(2).printIn("miles"));
    }

    @Test
    void canAddDistances() {
        //expect
        assertEquals(Distance.ofKm(1000f), Distance.ofKm(500f).add(Distance.ofKm(500f)));
        assertEquals(Distance.ofKm(1042f), Distance.ofKm(1020f).add(Distance.ofKm(22f)));
        assertEquals(Distance.ofKm(0f), Distance.ofKm(0f).add(Distance.ofKm(0f)));
        assertEquals(Distance.ofKm(3.7f), Distance.ofKm(1.5f).add(Distance.ofKm(2.2f)));
    }



}