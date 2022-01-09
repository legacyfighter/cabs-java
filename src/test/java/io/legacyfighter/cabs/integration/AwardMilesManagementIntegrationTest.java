package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.dto.AwardsAccountDTO;
import io.legacyfighter.cabs.entity.AwardedMiles;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repository.AwardedMilesRepository;
import io.legacyfighter.cabs.service.AwardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static io.legacyfighter.cabs.entity.Client.Type.NORMAL;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AwardMilesManagementIntegrationTest {

    static Instant NOW = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);

    @Autowired
    AwardsService awardsService;

    @Autowired
    AwardedMilesRepository awardedMilesRepository;

    @Autowired
    Fixtures fixtures;

    @MockBean
    Clock clock;

    @BeforeEach
    void setup() {
        when(clock.instant()).thenReturn(NOW);
    }

    @Test
    void canRegisterAccount() {
        //given
        Client client = fixtures.aClient();

        //when
        awardsService.registerToProgram(client.getId());

        //then
        AwardsAccountDTO account = awardsService.findBy(client.getId());
        assertNotNull(account);
        assertEquals(client.getId(), account.getClient().getId());
        assertFalse(account.isActive());
        assertEquals(0, account.getTransactions());
    }

    @Test
    void canActivateAccount() {
        //given
        Client client = fixtures.aClient();
        //and
        awardsService.registerToProgram(client.getId());

        //when
        awardsService.activateAccount(client.getId());

        //then
        AwardsAccountDTO account = awardsService.findBy(client.getId());
        assertTrue(account.isActive());
    }

    @Test
    void canDeactivateAccount() {
        //given
        Client client = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);

        //when
        awardsService.deactivateAccount(client.getId());

        //then
        AwardsAccountDTO account = awardsService.findBy(client.getId());
        assertFalse(account.isActive());
    }

    @Test
    void canRegisterMiles() {
        //given
        Client client = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        //and
        Transit transit = fixtures.aTransit(new Money(80));

        //when
        awardsService.registerMiles(client.getId(), transit.getId());

        //then
        AwardsAccountDTO account = awardsService.findBy(client.getId());
        assertEquals(1, account.getTransactions());
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertEquals(1, awardedMiles.size());
        assertEquals(10, awardedMiles.get(0).getMiles());
        assertFalse(awardedMiles.get(0).cantExpire());

    }

    @Test
    void canRegisterNonExpiringMiles() {
        //given
        Client client = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);

        //when
        awardsService.registerNonExpiringMiles(client.getId(), 20);

        //then
        AwardsAccountDTO account = awardsService.findBy(client.getId());
        assertEquals(1, account.getTransactions());
        List<AwardedMiles> awardedMiles = awardedMilesRepository.findAllByClient(client);
        assertEquals(1, awardedMiles.size());
        assertEquals(20, awardedMiles.get(0).getMiles());
        assertTrue(awardedMiles.get(0).cantExpire());
    }

    @Test
    void canCalculateMilesBalance() {
        //given
        Client client = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        //and
        Transit transit = fixtures.aTransit(new Money(80));

        //when
        awardsService.registerNonExpiringMiles(client.getId(), 20);
        awardsService.registerMiles(client.getId(), transit.getId());
        awardsService.registerMiles(client.getId(), transit.getId());

        //then
        AwardsAccountDTO account = awardsService.findBy(client.getId());
        assertEquals(3, account.getTransactions());
        Integer miles = awardsService.calculateBalance(client.getId());
        assertEquals(40, miles);
    }

    @Test
    void canTransferMiles() {
        //given
        Client client = fixtures.aClient();
        Client secondClient = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        fixtures.activeAwardsAccount(secondClient);
        //and
        awardsService.registerNonExpiringMiles(client.getId(),  10);

        //when
        awardsService.transferMiles(client.getId(), secondClient.getId(), 10);

        //then
        Integer firstClientBalance = awardsService.calculateBalance(client.getId());
        Integer secondClientBalance = awardsService.calculateBalance(secondClient.getId());
        assertEquals(0, firstClientBalance);
        assertEquals(10, secondClientBalance);
    }

    @Test
    void cannotTransferMilesWhenAccountIsNotActive() {
        //given
        Client client = fixtures.aClient();
        Client secondClient = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        fixtures.activeAwardsAccount(secondClient);
        //and
        awardsService.registerNonExpiringMiles(client.getId(),  10);
        //and
        awardsService.deactivateAccount(client.getId());

        //when
        awardsService.transferMiles(client.getId(), secondClient.getId(), 5);

        //then
        assertEquals(10, awardsService.calculateBalance(client.getId()));
    }

    @Test
    void cannotTransferMilesWhenNotEnough() {
        //given
        Client client = fixtures.aClient();
        Client secondClient = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        fixtures.activeAwardsAccount(secondClient);
        //and
        awardsService.registerNonExpiringMiles(client.getId(),  10);

        //when
        awardsService.transferMiles(client.getId(), secondClient.getId(), 30);

        //then
        assertEquals(10, awardsService.calculateBalance(client.getId()));
    }

    @Test
    void cannotTransferMilesWhenAccountNotActive() {
        //given
        Client client = fixtures.aClient();
        Client secondClient = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        fixtures.activeAwardsAccount(secondClient);
        //and
        awardsService.registerNonExpiringMiles(client.getId(),  10);
        //and
        awardsService.deactivateAccount(client.getId());

        //when
        awardsService.transferMiles(client.getId(), secondClient.getId(), 5);
        //then
        assertEquals(10, awardsService.calculateBalance(client.getId()));
    }

    @Test
    void canRemoveMiles() {
        //given
        Client client = fixtures.aClient(NORMAL);
        //and
        fixtures.activeAwardsAccount(client);
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        awardsService.registerMiles(client.getId(), transit.getId());
        awardsService.registerMiles(client.getId(), transit.getId());
        awardsService.registerMiles(client.getId(), transit.getId());

        //when
        awardsService.removeMiles(client.getId(), 20);

        //then
        Integer miles = awardsService.calculateBalance(client.getId());
        assertEquals(10, miles);
    }

    @Test
    void cannotRemoveMoreThanClientHasMiles() {
        //given
        Client client = fixtures.aClient();
        //and
        fixtures.activeAwardsAccount(client);
        //and
        Transit transit = fixtures.aTransit(new Money(80));

        //when
        awardsService.registerMiles(client.getId(), transit.getId());
        awardsService.registerMiles(client.getId(), transit.getId());
        awardsService.registerMiles(client.getId(), transit.getId());

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> awardsService.removeMiles(client.getId(), 40));
    }

    @Test
    void cannotRemoveMilesIfAccountIsNotActive() {
        //given
        Client client = fixtures.aClient();
        //and
        awardsService.registerToProgram(client.getId());
        //and
        Transit transit = fixtures.aTransit(new Money(80));
        //and
        Integer currentMiles = awardsService.calculateBalance(client.getId());

        //when
        awardsService.registerMiles(client.getId(), transit.getId());

        //then
        assertEquals(currentMiles, awardsService.calculateBalance(client.getId()));
    }

}