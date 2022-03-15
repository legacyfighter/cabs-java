package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.pricing.Tariff;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.pricing.Tariffs;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@Service
public class StubbedTransitPrice {

    @SpyBean
    Tariffs tariffs;

    @Transactional
    public void stub(Money faked) {
        Tariff fakeTariff = new Tariff(0, "fake", faked);
        when(tariffs.choose(isA(Instant.class))).thenReturn(fakeTariff);
    }
}
