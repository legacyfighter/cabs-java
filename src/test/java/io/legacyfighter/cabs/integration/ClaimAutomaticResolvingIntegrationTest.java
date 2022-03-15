package io.legacyfighter.cabs.integration;


import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.crm.claims.Claim;
import io.legacyfighter.cabs.crm.claims.ClaimService;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.loyalty.AwardsService;
import io.legacyfighter.cabs.notification.ClientNotificationService;
import io.legacyfighter.cabs.notification.DriverNotificationService;
import io.legacyfighter.cabs.ride.Transit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static io.legacyfighter.cabs.crm.Client.Type.NORMAL;
import static io.legacyfighter.cabs.crm.Client.Type.VIP;
import static io.legacyfighter.cabs.crm.claims.Claim.CompletionMode.AUTOMATIC;
import static io.legacyfighter.cabs.crm.claims.Claim.CompletionMode.MANUAL;
import static io.legacyfighter.cabs.crm.claims.Status.ESCALATED;
import static io.legacyfighter.cabs.crm.claims.Status.REFUNDED;
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

    @MockBean
    GeocodingService geocodingService;

    @Test
    void secondClaimForTheSameTransitWillBeEscalated() {
        //given
        lowCostThresholdIs(40);
        //and
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        //and
        Client client = fixtures.aClient(VIP);
        //and
        Transit transit = aTransit(pickup, client, driver, 39);
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
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        //and
        Transit transit = aTransit(pickup, client, driver, 39);
        //and
        Claim claim = fixtures.createClaim(client, transit);

        //when
        Mockito.clearInvocations(awardsService, clientNotificationService);
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(REFUNDED, claim.getStatus());
        assertEquals(AUTOMATIC, claim.getCompletionMode());
        verify(clientNotificationService).notifyClientAboutRefund(claim.getClaimNo(), claim.getOwnerId());
        verify(awardsService).registerNonExpiringMiles(claim.getOwnerId(), 10);
    }

    @Test
    void highCostTransitsAreEscalatedEvenWhenClientIsVIP() {
        //given
        lowCostThresholdIs(40);
        //and
        Client client = fixtures.aClientWithClaims(VIP, 3);
        //and
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        //and
        Transit transit = aTransit(pickup, client, driver, 50);
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
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);

        //when
        Claim claim1 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(pickup, client, driver, 50)).getId());
        Claim claim2 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(pickup, client, driver, 50)).getId());
        Claim claim3 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, aTransit(pickup, client, driver, 50)).getId());
        //and
        Transit transit = aTransit(pickup, client, driver, 50);
        //and
        Mockito.clearInvocations(awardsService);
        //and
        Claim claim4 = claimService.tryToResolveAutomatically(fixtures.createClaim(client, transit).getId());

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
        fixtures.clientHasDoneTransits(client, 12, geocodingService);
        //and
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        //and
        Transit transit = aTransit(pickup, client, driver, 39);
        //and
        Claim claim = fixtures.createClaim(client, transit);
        //and
        Mockito.clearInvocations(awardsService);

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
        fixtures.clientHasDoneTransits(client, 12, geocodingService);
        //and
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        //and
        Claim claim = fixtures.createClaim(client, aTransit(pickup, client, driver, 50));
        //and
        Mockito.clearInvocations(awardsService);
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
        fixtures.clientHasDoneTransits(client, 2, geocodingService);
        //and
        Address pickup = fixtures.anAddress();
        //and
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        //and
        Transit transit = aTransit(pickup, client, driver, 50);
        //and
        Mockito.clearInvocations(awardsService);
        //and
        Claim claim = fixtures.createClaim(client, transit);

        //when
        claim = claimService.tryToResolveAutomatically(claim.getId());

        //then
        assertEquals(ESCALATED, claim.getStatus());
        assertEquals(MANUAL, claim.getCompletionMode());
        verify(driverNotificationService).askDriverForDetailsAboutClaim(claim.getClaimNo(), driver.getId());
        verifyNoInteractions(awardsService);
    }

    Transit aTransit(Address pickup, Client client, Driver driver, int price) {
        return fixtures.aRide(price, client, driver, pickup, fixtures.anAddress());
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