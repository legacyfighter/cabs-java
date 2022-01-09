package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.entity.Claim;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.service.AwardsService;
import io.legacyfighter.cabs.service.ClaimService;
import io.legacyfighter.cabs.service.ClientNotificationService;
import io.legacyfighter.cabs.service.DriverNotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static io.legacyfighter.cabs.entity.Claim.CompletionMode.AUTOMATIC;
import static io.legacyfighter.cabs.entity.Claim.CompletionMode.MANUAL;
import static io.legacyfighter.cabs.entity.Claim.Status.ESCALATED;
import static io.legacyfighter.cabs.entity.Claim.Status.REFUNDED;
import static io.legacyfighter.cabs.entity.Client.Type.NORMAL;
import static io.legacyfighter.cabs.entity.Client.Type.VIP;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClaimAutomaticResolvingIntegrationTest {

    @Autowired
    ClaimService claimService;

    @MockBean
    ClientNotificationService clientNotificationService;

    @MockBean
    DriverNotificationService driverNotificationService;

    @MockBean
    AwardsService awardsService;

    @MockBean
    AppProperties appProperties;

    @Autowired
    Fixtures fixtures;

    @Test
    void secondClaimForTheSameTransitWillBeEscalated() {
        //given
        lowCostThresholdIs(40);
        //and
        Driver driver = fixtures.aDriver();
        //and
        Client client = fixtures.aClient(VIP);
        //and
        Transit transit = aTransit(client, driver, 39);
        //and
        Claim claim = fixtures.createClaim(client, transit);
        //and
        claim = claimService.tryToResolveAutomatically(claim.getId());
        //and
        Claim claim2 = fixtures.createClaim(client, transit);

        //when
        claim2 = claimService.tryToResolveAutomatically(claim2.getId());

        //then
        assertEquals(REFUNDED, claim.getStatus());
        assertEquals(AUTOMATIC, claim.getCompletionMode());
        assertEquals(ESCALATED, claim2.getStatus());
        assertEquals(MANUAL, claim2.getCompletionMode());
    }

    @Test
    void lowCostTransitsAreRefundedIfClientIsVIP() {
        //given
        lowCostThresholdIs(40);
        //and
        Client client = fixtures.aClientWithClaims(VIP, 3);
        //and
        Driver driver = fixtures.aDriver();
        //and
        Transit transit = aTransit(client, driver, 39);
        //and
        Claim claim = fixtures.createClaim(client, transit);

        //when
        Mockito.clearInvocations(awardsService, clientNotificationService);
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(REFUNDED, claim.getStatus());
        assertEquals(AUTOMATIC, claim.getCompletionMode());
        verify(clientNotificationService).notifyClientAboutRefund(claim.getClaimNo(), claim.getOwner().getId());
        verify(awardsService).registerSpecialMiles(claim.getOwner().getId(), 10);
    }

    @Test
    void highCostTransitsAreEscalatedEvenWhenClientIsVIP() {
        //given
        lowCostThresholdIs(40);
        //and
        Client client = fixtures.aClientWithClaims(VIP, 3);
        //and
        Driver driver = fixtures.aDriver();
        //and
        Transit transit = aTransit(client, driver, 50);
        //and
        Claim claim = fixtures.createClaim(client, transit);

        //when
        Mockito.clearInvocations(awardsService, driverNotificationService);
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(ESCALATED, claim.getStatus());
        assertEquals(MANUAL, claim.getCompletionMode());
        verify(driverNotificationService).askDriverForDetailsAboutClaim(claim.getClaimNo(), driver.getId());
        verifyNoInteractions(awardsService);
    }

    @Test
    void firstThreeClaimsAreRefunded() {
        //given
        lowCostThresholdIs(40);
        //and
        noOfTransitsForAutomaticRefundIs(10);
        //and
        Client client = aClient(NORMAL);
        //and
        Driver driver = fixtures.aDriver();

        //when
        Claim claim1 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(client, driver, 50)).getId());
        Claim claim2 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(client, driver, 50)).getId());
        Claim claim3 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(client, driver, 50)).getId());
        Claim claim4 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(client, driver, 50)).getId());

        //then
        assertEquals(REFUNDED, claim1.getStatus());
        assertEquals(REFUNDED, claim2.getStatus());
        assertEquals(REFUNDED, claim3.getStatus());
        assertEquals(ESCALATED, claim4.getStatus());
        assertEquals(AUTOMATIC, claim1.getCompletionMode());
        assertEquals(AUTOMATIC, claim2.getCompletionMode());
        assertEquals(AUTOMATIC, claim3.getCompletionMode());
        assertEquals(MANUAL, claim4.getCompletionMode());

        verify(clientNotificationService).notifyClientAboutRefund(claim1.getClaimNo(), client.getId());
        verify(clientNotificationService).notifyClientAboutRefund(claim2.getClaimNo(), client.getId());
        verify(clientNotificationService).notifyClientAboutRefund(claim3.getClaimNo(), client.getId());
        verifyNoInteractions(awardsService);
    }

    @Test
    void lowCostTransitsAreRefundedWhenManyTransits() {
        //given
        lowCostThresholdIs(40);
        //and
        noOfTransitsForAutomaticRefundIs(10);
        //and
        Client client = fixtures.aClientWithClaims(NORMAL, 3);
        //and
        fixtures.clientHasDoneTransits(client, 12);
        //and
        Transit transit = aTransit(client, fixtures.aDriver(), 39);
        //and
        Claim claim = fixtures.createClaim(client, transit);

        //when
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(REFUNDED, claim.getStatus());
        assertEquals(AUTOMATIC, claim.getCompletionMode());
        verify(clientNotificationService).notifyClientAboutRefund(claim.getClaimNo(), client.getId());
        verifyNoInteractions(awardsService);
    }

    @Test
    void highCostTransitsAreEscalatedEvenWithManyTransits() {
        //given
        lowCostThresholdIs(40);
        //and
        noOfTransitsForAutomaticRefundIs(10);
        //and
        Client client = fixtures.aClientWithClaims(NORMAL, 3);
        //and
        fixtures.clientHasDoneTransits(client, 12);
        //and
        Claim claim = fixtures.createClaim(client, aTransit(client, fixtures.aDriver(), 50));

        //when
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(ESCALATED, claim.getStatus());
        assertEquals(MANUAL, claim.getCompletionMode());
        verify(clientNotificationService).askForMoreInformation(claim.getClaimNo(), client.getId());
        verifyNoInteractions(awardsService);
    }

    @Test
    void highCostTransitsAreEscalatedWhenFewTransits() {
        //given
        lowCostThresholdIs(40);
        //and
        noOfTransitsForAutomaticRefundIs(10);
        //and
        Client client = fixtures.aClientWithClaims(NORMAL, 3);
        //and
        fixtures.clientHasDoneTransits(client, 2);
        //and
        Driver driver = fixtures.aDriver();
        //and
        Claim claim = fixtures.createClaim(client, aTransit(client, driver, 50));

        //when
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(ESCALATED, claim.getStatus());
        assertEquals(MANUAL, claim.getCompletionMode());
        verify(driverNotificationService).askDriverForDetailsAboutClaim(claim.getClaimNo(), driver.getId());
        verifyNoInteractions(awardsService);
    }

    Transit aTransit(Client client, Driver driver, int price) {
        return fixtures.aCompletedTransitAt(price, now(), client, driver);
    }

    void lowCostThresholdIs(int price) {
        when(appProperties.getAutomaticRefundForVipThreshold()).thenReturn(price);
    }

    void noOfTransitsForAutomaticRefundIs(int no) {
        when(appProperties.getNoOfTransitsForClaimAutomaticRefund()).thenReturn(no);
    }

    Client aClient(Client.Type type) {
        return fixtures.aClientWithClaims(type, 0);
    }

}