package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.pricing.Tariff;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestForTransitTest {

    @Test
    void canCreateRequestForTransit() {
        //when
        RequestForTransit requestForTransit = requestTransit();

        //expect
        assertNotNull(requestForTransit.getTariff());
        assertNotEquals(0, requestForTransit.getTariff().getKmRate());
    }


    RequestForTransit requestTransit() {
        Tariff tariff = Tariff.ofTime(LocalDateTime.now());
        return new RequestForTransit(tariff, Distance.ZERO);
    }

}