package io.legacyfighter.cabs.pricing;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
public class Tariffs {

    public Tariff choose(Instant when) {
        if (when == null) {
            when = Instant.now();
        }
        return Tariff.ofTime(when.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
}