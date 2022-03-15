package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.crm.ClientDTO;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.pricing.Tariff;
import io.legacyfighter.cabs.ride.details.TransitDetailsDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Tariff tariff = Tariff.ofTime(LocalDateTime.now());
        TransitDetailsDTO transitDetails = new TransitDetailsDTO(1L, now(), now(), new ClientDTO(), null, new AddressDTO(), new AddressDTO(), now(), now(), Distance.ofKm(km), tariff);
        return new TransitDTO(transitDetails, new HashSet<>(), new HashSet<>(), null);
    }

}

