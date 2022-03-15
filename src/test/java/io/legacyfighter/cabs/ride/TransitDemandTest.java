package io.legacyfighter.cabs.ride;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.legacyfighter.cabs.ride.TransitDemand.Status.WAITING_FOR_DRIVER_ASSIGNMENT;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransitDemandTest {


    @Test
    void canChangePickupPlace() {
        //given
        TransitDemand transitDemand = transitDemand();

        //expect
        Assertions.assertThatNoException().isThrownBy(() -> transitDemand.changePickup(0.2));
    }

    @Test
    void cannotChangePickupPlaceAfterTransitIsAccepted() {
        //given
        TransitDemand transitDemand = transitDemand();
        //and
        transitDemand.accept();

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitDemand.changePickup(0.1));
        //and
        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> transitDemand.changePickup(0.11));
    }

    @Test
    void cannotChangePickupPlaceMoreThanThreeTimes() {
        //given
        TransitDemand transitDemand = transitDemand();
        //and
        transitDemand.changePickup(0.1d);
        //and
        transitDemand.changePickup(0.2d);
        //and
        transitDemand.changePickup(0.22d);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        transitDemand.changePickup(0.23d));
    }

    @Test
    void cannotChangePickupPlaceWhenItIsFarWayFromOriginal() {
        //given
        TransitDemand transitDemand = transitDemand();

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() ->
                        transitDemand.changePickup(50));
    }

    @Test
    void canCancelDemand() {
        //given
        TransitDemand transitDemand = transitDemand();

        //when
        transitDemand.cancel();

        //then
        assertEquals(TransitDemand.Status.CANCELLED, transitDemand.getStatus());
    }

    @Test
    void canPublishDemand() {
        //given
        TransitDemand transitDemand = transitDemand();

        //then
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, transitDemand.getStatus());
    }


    TransitDemand transitDemand() {
        return new TransitDemand(UUID.randomUUID());
    }


}