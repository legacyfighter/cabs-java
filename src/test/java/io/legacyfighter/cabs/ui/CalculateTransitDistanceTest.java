package io.legacyfighter.cabs.ui;

import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

public class CalculateTransitDistanceTest {

    @Test
    void shouldNotWorkWithInvalidUnit() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> transitForDistance(2f).getDistance("invalid"));
    }

    @Test
    void shouldRepresentAsKm() {
        assertEquals("10km", transitForDistance(10).getDistance("km"));
        assertEquals("10.123km", transitForDistance(10.123f).getDistance("km"));
        assertEquals("10.123km", transitForDistance(10.12345f).getDistance("km"));
        assertEquals("0km", transitForDistance(0).getDistance("km"));
    }

    @Test
    void shouldRepresentAsMeters() {
        assertEquals("10000m", transitForDistance(10).getDistance("m"));
        assertEquals("10123m", transitForDistance(10.123f).getDistance("m"));
        assertEquals("10123m", transitForDistance(10.12345f).getDistance("m"));
        assertEquals("0m", transitForDistance(0).getDistance("m"));
    }

    @Test
    void shouldRepresentAsMiles() {
        assertEquals("6.214miles", transitForDistance(10).getDistance("miles"));
        assertEquals("6.290miles", transitForDistance(10.123f).getDistance("miles"));
        assertEquals("6.290miles", transitForDistance(10.12345f).getDistance("miles"));
        assertEquals("0miles", transitForDistance(0).getDistance("miles"));
    }

    TransitDTO transitForDistance(float km) {
        Transit t = new Transit(new Address(), new Address(), new Client(), null, Instant.now(), Distance.ofKm(km));
        t.setPrice(new Money(10));
        return new TransitDTO(t);
    }

}

