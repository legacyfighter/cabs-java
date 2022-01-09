package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.entity.ClaimsResolver.Result;
import io.legacyfighter.cabs.money.Money;
import org.junit.jupiter.api.Test;

import static io.legacyfighter.cabs.entity.Claim.Status.ESCALATED;
import static io.legacyfighter.cabs.entity.Claim.Status.REFUNDED;
import static io.legacyfighter.cabs.entity.ClaimsResolver.WhoToAsk.*;
import static io.legacyfighter.cabs.entity.Client.Type.NORMAL;
import static io.legacyfighter.cabs.entity.Client.Type.VIP;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClaimAutomaticResolvingTest {

    @Test
    void secondClaimForTheSameTransitWillBeEscalated() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Transit transit = aTransit(1L, 39);
        //and
        Claim claim = createClaim(transit);
        //and
        resolver.resolve(claim, 40, 15, 10);
        //and
        Claim claim2 = createClaim(transit);

        //when
        Result result = resolver.resolve(claim2, 40, 15, 10);

        //then
        assertEquals(ESCALATED, result.decision);
        assertEquals(ASK_NOONE, result.whoToAsk);
    }

    @Test
    void lowCostTransitsAreRefundedIfClientIsVIP() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Transit transit = aTransit(1L, 39);
        //and
        Claim claim = createClaim(transit);

        //when
        Result result = resolver.resolve(claim, 40, 15, 10);

        //then
        assertEquals(REFUNDED, result.decision);
        assertEquals(ASK_NOONE, result.whoToAsk);
    }

    @Test
    void highCostTransitsAreEscalatedEvenWhenClientIsVIP() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Claim claim = createClaim(aTransit(1L, 39));
        resolver.resolve(claim, 40, 15, 10);
        Claim claim2 = createClaim(aTransit(2L, 39));
        resolver.resolve(claim2, 40, 15, 10);
        Claim claim3 = createClaim(aTransit(3L, 39));
        resolver.resolve(claim3, 40, 15, 10);
        //and
        Claim claim4 = createClaim(aTransit(4L, 41), aClient(VIP));

        //when
        Result result = resolver.resolve(claim4, 40, 15, 10);

        //then
        assertEquals(ESCALATED, result.decision);
        assertEquals(ASK_DRIVER, result.whoToAsk);
    }

    @Test
    void firstThreeClaimsAreRefunded() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Claim claim = createClaim(aTransit(1L, 39));
        Result result1 = resolver.resolve(claim, 40, 15, 10);
        Claim claim2 = createClaim(aTransit(2L, 39));
        Result result2 = resolver.resolve(claim2, 40, 15, 10);
        Claim claim3 = createClaim(aTransit(3L, 39));
        Result result3 = resolver.resolve(claim3, 40, 15, 10);

        //when
        Claim claim4 = createClaim(aTransit(4L, 39), aClient(NORMAL));
        Result result4 = resolver.resolve(claim4, 40, 4, 10);

        //then
        assertEquals(REFUNDED, result1.decision);
        assertEquals(REFUNDED, result2.decision);
        assertEquals(REFUNDED, result3.decision);
        assertEquals(ESCALATED, result4.decision);

        assertEquals(ASK_NOONE, result1.whoToAsk);
        assertEquals(ASK_NOONE, result2.whoToAsk);
        assertEquals(ASK_NOONE, result3.whoToAsk);
    }

    @Test
    void lowCostTransitsAreRefundedWhenManyTransits() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Claim claim = createClaim(aTransit(1L, 39));
        resolver.resolve(claim, 40, 15, 10);
        Claim claim2 = createClaim(aTransit(2L, 39));
        resolver.resolve(claim2, 40, 15, 10);
        Claim claim3 = createClaim(aTransit(3L, 39));
        resolver.resolve(claim3, 40, 15, 10);
        //and
        Claim claim4 = createClaim(aTransit(4L, 39), aClient(NORMAL));

        //when
        Result result = resolver.resolve(claim4, 40, 10, 9);

        //then
        assertEquals(REFUNDED, result.decision);
        assertEquals(ASK_NOONE, result.whoToAsk);
    }

    @Test
    void highCostTransitsAreEscalatedEvenWithManyTransits() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Claim claim = createClaim(aTransit(1L, 39));
        resolver.resolve(claim, 40, 15, 10);
        Claim claim2 = createClaim(aTransit(2L, 39));
        resolver.resolve(claim2, 40, 15, 10);
        Claim claim3 = createClaim(aTransit(3L, 39));
        resolver.resolve(claim3, 40, 15, 10);
        //and
        Claim claim4 = createClaim(aTransit(4L, 50), aClient(NORMAL));

        //when
        Result result = resolver.resolve(claim4, 40, 12, 10);

        //then
        assertEquals(ESCALATED, result.decision);
        assertEquals(ASK_CLIENT, result.whoToAsk);
    }

    @Test
    void highCostTransitsAreEscalatedWhenFewTransits() {
        //given
        ClaimsResolver resolver = new ClaimsResolver();
        //and
        Claim claim = createClaim(aTransit(1L, 39));
        resolver.resolve(claim, 40, 15, 10);
        Claim claim2 = createClaim(aTransit(2L, 39));
        resolver.resolve(claim2, 40, 15, 10);
        Claim claim3 = createClaim(aTransit(3L, 39));
        resolver.resolve(claim3, 40, 15, 10);
        //and
        Claim claim4 = createClaim(aTransit(4L, 50), aClient(NORMAL));

        //when
        Result result = resolver.resolve(claim4, 40, 2, 10);

        //then
        assertEquals(ESCALATED, result.decision);
        assertEquals(ASK_DRIVER, result.whoToAsk);
    }

     Transit aTransit(Long id, int price) {
         Transit transit = new Transit(id);
         transit.setPrice(new Money(price));
         return transit;
    }

    Claim createClaim(Transit transit) {
        Claim claim = new Claim();
        claim.setTransit(transit);
        return claim;
    }

    Claim createClaim(Transit transit, Client client) {
        Claim claim = new Claim();
        claim.setTransit(transit);
        claim.setOwner(client);
        return claim;
    }

    Client aClient(Client.Type type) {
        Client client = new Client();
        client.setType(type);
        return client;
    }


}