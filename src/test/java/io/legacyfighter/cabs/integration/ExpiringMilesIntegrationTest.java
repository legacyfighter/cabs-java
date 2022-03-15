package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.loyalty.AwardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExpiringMilesIntegrationTest {

    public static final long TRANSIT_ID = 1L;
    static Instant _1989_12_12 = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);
    static Instant _1989_12_13 = _1989_12_12.plus(1, ChronoUnit.DAYS);
    static Instant _1989_12_14 = _1989_12_13.plus(1, ChronoUnit.DAYS);

    @MockBean
    Clock clock;

    @Autowired
    AwardsService awardsService;

    @Autowired
    Fixtures fixtures;

    @MockBean
    AppProperties appProperties;

    @Test
    void shouldTakeIntoAccountExpiredMilesWhenCalculatingBalance() {
        //given
        Client client = fixtures.aClient();
        //and
        when(clock.instant()).thenReturn(_1989_12_12);
        //and
        defaultMilesBonusIs(10);
        //and
        defaultMilesExpirationInDaysIs(365);
        //and
        fixtures.activeAwardsAccount(client);

        //when
        registerMilesAt(client, _1989_12_12);
        //then
        assertEquals(10, calculateBalanceAt(client, _1989_12_12));
        //when
        registerMilesAt(client, _1989_12_13);
        //then
        assertEquals(20, calculateBalanceAt(client, _1989_12_12));
        //when
        registerMilesAt(client, _1989_12_14);
        //then
        assertEquals(30, calculateBalanceAt(client, _1989_12_14));
        assertEquals(30, calculateBalanceAt(client, _1989_12_12.plus(300, ChronoUnit.DAYS)));
        assertEquals(20, calculateBalanceAt(client, _1989_12_12.plus(365, ChronoUnit.DAYS)));
        assertEquals(10, calculateBalanceAt(client, _1989_12_13.plus(365, ChronoUnit.DAYS)));
        assertEquals(0, calculateBalanceAt(client, _1989_12_14.plus(365, ChronoUnit.DAYS)));

    }

    void defaultMilesBonusIs(int bonus) {
        when(appProperties.getDefaultMilesBonus()).thenReturn(bonus);
    }

    void defaultMilesExpirationInDaysIs(int days) {
        when(appProperties.getMilesExpirationInDays()).thenReturn(days);
    }

    void registerMilesAt(Client client, Instant when) {
        when(clock.instant()).thenReturn(when);
        awardsService.registerMiles(client.getId(), TRANSIT_ID);
    }

    Integer calculateBalanceAt(Client client, Instant when) {
        when(clock.instant()).thenReturn(when);
        return awardsService.calculateBalance(client.getId());
    }


}