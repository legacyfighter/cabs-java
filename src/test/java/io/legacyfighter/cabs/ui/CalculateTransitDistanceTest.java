package io.legacyfighter.cabs.ui;

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
        Transit t = new Transit();
        t.setPrice(new Money(10));
        t.setDateTime(Instant.now());
        t.setTo(new Address());
        t.setFrom(new Address());
        t.setStatus(Transit.Status.DRAFT);
        t.setKm(km);
        t.setClient(new Client());
        return new TransitDTO(t);
    }

}

