package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.entity.AwardedMiles;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.AwardedMilesRepository;
import io.legacyfighter.cabs.service.AwardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static io.legacyfighter.cabs.entity.Client.Type.NORMAL;
import static io.legacyfighter.cabs.entity.Client.Type.VIP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class RemovingAwardMilesIntegrationTest {

    static Instant DAY_BEFORE_YESTERDAY = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);
    static Instant YESTERDAY = DAY_BEFORE_YESTERDAY.plus(1, ChronoUnit.DAYS);
    static Instant TODAY = YESTERDAY.plus(1, ChronoUnit.DAYS);
    static Instant SUNDAY = LocalDateTime.of(1989, 12, 17, 12, 12).toInstant(ZoneOffset.UTC);

    @Autowired
    AwardsService awardsService;

    @Autowired
    AwardedMilesRepository awardedMilesRepository;

    @Autowired
    Fixtures fixtures;

    @MockBean
    Clock clock;

    @MockBean
    AppProperties appProperties;

    @Test
    void byDefaultRemoveOldestFirstEvenWhenTheyAreSpecial() {
        //given
        Client client = clientWithAnActiveMilesProgram(NORMAL);
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        AwardedMiles middle = grantedMilesThatWillExpireInDays(10, 365, YESTERDAY, client, transit);
        AwardedMiles youngest = grantedMilesThatWillExpireInDays(10, 365, TODAY, client, transit);
        AwardedMiles oldestSpecialMiles = grantedSpecialMiles(5, DAY_BEFORE_YESTERDAY, client);

        //when
        awardsService.removeMiles(client.getId(), 16);

        //then
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertThatMilesWereReducedTo(oldestSpecialMiles, 0, awardedMiles);
        assertThatMilesWereReducedTo(middle, 0, awardedMiles);
        assertThatMilesWereReducedTo(youngest, 9, awardedMiles);
    }

    @Test
    void shouldRemoveOldestMilesFirstWhenManyTransits() {
        //given
        Client client = clientWithAnActiveMilesProgram(NORMAL);
        //and
        fixtures.clientHasDoneTransits(client, 15);
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        AwardedMiles oldest = grantedMilesThatWillExpireInDays(10, 60, DAY_BEFORE_YESTERDAY, client, transit);
        AwardedMiles middle = grantedMilesThatWillExpireInDays(10, 365, YESTERDAY, client, transit);
        AwardedMiles youngest = grantedMilesThatWillExpireInDays(10, 30, TODAY, client, transit);

        //when
        awardsService.removeMiles(client.getId(), 15);

        //then
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertThatMilesWereReducedTo(oldest, 0, awardedMiles);
        assertThatMilesWereReducedTo(middle, 5, awardedMiles);
        assertThatMilesWereReducedTo(youngest, 10, awardedMiles);
    }

    @Test
    void shouldRemoveSpecialMilesLastWhenManyTransits() {
        //given
        Client client = clientWithAnActiveMilesProgram(NORMAL);
        //and
        fixtures.clientHasDoneTransits(client, 15);
        //and
        Transit transit = fixtures.aTransit(new Money(80));

        AwardedMiles regularMiles = grantedMilesThatWillExpireInDays(10, 365, TODAY, client, transit);
        AwardedMiles oldestSpecialMiles = grantedSpecialMiles(5, DAY_BEFORE_YESTERDAY, client);

        //when
        awardsService.removeMiles(client.getId(), 13);

        //then
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertThatMilesWereReducedTo(regularMiles, 0, awardedMiles);
        assertThatMilesWereReducedTo(oldestSpecialMiles, 2, awardedMiles);
    }


    @Test
    void shouldRemoveSoonToExpireMilesFirstWhenClientIsVIP() {
        //given
        Client client = clientWithAnActiveMilesProgram(VIP);
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        AwardedMiles secondToExpire = grantedMilesThatWillExpireInDays(10, 60, YESTERDAY, client, transit);
        AwardedMiles thirdToExpire = grantedMilesThatWillExpireInDays(5, 365, DAY_BEFORE_YESTERDAY, client, transit);
        AwardedMiles firstToExpire = grantedMilesThatWillExpireInDays(15, 30, TODAY, client, transit);
        AwardedMiles specialMiles = grantedSpecialMiles(1, DAY_BEFORE_YESTERDAY, client);


        //when
        awardsService.removeMiles(client.getId(), 21);

        //then
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertThatMilesWereReducedTo(specialMiles, 1, awardedMiles);
        assertThatMilesWereReducedTo(firstToExpire, 0, awardedMiles);
        assertThatMilesWereReducedTo(secondToExpire, 4, awardedMiles);
        assertThatMilesWereReducedTo(thirdToExpire, 5, awardedMiles);
    }

    @Test
    void shouldRemoveSoonToExpireMilesFirstWhenRemovingOnSundayAndClientHasDoneManyTransits() {
        //given
        Client client = clientWithAnActiveMilesProgram(NORMAL);
        //and
        fixtures.clientHasDoneTransits(client, 15);
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        AwardedMiles secondToExpire = grantedMilesThatWillExpireInDays(10, 60, YESTERDAY, client, transit);
        AwardedMiles thirdToExpire = grantedMilesThatWillExpireInDays(5, 365, DAY_BEFORE_YESTERDAY, client, transit);
        AwardedMiles firstToExpire = grantedMilesThatWillExpireInDays(15, 10, TODAY, client, transit);
        AwardedMiles specialMiles = grantedSpecialMiles(100, YESTERDAY, client);


        //when
        itIsSunday();
        awardsService.removeMiles(client.getId(), 21);

        //then
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertThatMilesWereReducedTo(specialMiles, 100, awardedMiles);
        assertThatMilesWereReducedTo(firstToExpire, 0, awardedMiles);
        assertThatMilesWereReducedTo(secondToExpire, 4, awardedMiles);
        assertThatMilesWereReducedTo(thirdToExpire, 5, awardedMiles);
    }

    @Test
    void shouldRemoveExpiringMilesFirstWhenClientHasManyClaims() {
        //given
        Client client = clientWithAnActiveMilesProgram(NORMAL);
        //and
        fixtures.clientHasDoneClaims(client, 3);
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        AwardedMiles secondToExpire = grantedMilesThatWillExpireInDays(4, 60, YESTERDAY, client, transit);
        AwardedMiles thirdToExpire = grantedMilesThatWillExpireInDays(10, 365, DAY_BEFORE_YESTERDAY, client, transit);
        AwardedMiles firstToExpire = grantedMilesThatWillExpireInDays(5, 10, YESTERDAY, client, transit);
        AwardedMiles specialMiles = grantedSpecialMiles(10, YESTERDAY, client);

        //when
        awardsService.removeMiles(client.getId(), 21);

        //then
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertThatMilesWereReducedTo(specialMiles, 0, awardedMiles);
        assertThatMilesWereReducedTo(thirdToExpire, 0, awardedMiles);
        assertThatMilesWereReducedTo(secondToExpire, 3, awardedMiles);
        assertThatMilesWereReducedTo(firstToExpire, 5, awardedMiles);
    }

    AwardedMiles grantedMilesThatWillExpireInDays(int miles, int expirationInDays, Instant when, Client client, Transit transit) {
        milesWillExpireInDays(expirationInDays);
        defaultMilesBonusIs(miles);
        return milesRegisteredAt(when, client, transit);
    }

    AwardedMiles grantedSpecialMiles(int miles, Instant when, Client client) {
        defaultMilesBonusIs(miles);
        when(clock.instant()).thenReturn(when);
        return awardsService.registerSpecialMiles(client.getId(), miles);
    }

    void assertThatMilesWereReducedTo(AwardedMiles firstToExpire, int milesAfterReduction, List<AwardedMiles> allMiles) {
        Stream<Integer> actual = allMiles
                .stream()
                .filter(am -> firstToExpire.getId().equals(am.getId())).map(AwardedMiles::getMiles);
        assertThat(actual.findFirst()).contains(milesAfterReduction);
    }

    AwardedMiles milesRegisteredAt(Instant when, Client client, Transit transit) {
        when(clock.instant()).thenReturn(when);
        return awardsService.registerMiles(client.getId(), transit.getId());
    }

    Client clientWithAnActiveMilesProgram(Client.Type type) {
        when(clock.instant()).thenReturn(DAY_BEFORE_YESTERDAY);
        Client client = fixtures.aClient(type);
        fixtures.activeAwardsAccount(client);
        return client;
    }

    void milesWillExpireInDays(int days) {
        when(appProperties.getMilesExpirationInDays()).thenReturn(days);
    }

    void defaultMilesBonusIs(int miles) {
        when(appProperties.getDefaultMilesBonus()).thenReturn(miles);
    }

    void itIsSunday() {
        when(clock.instant()).thenReturn(SUNDAY);
    }


}