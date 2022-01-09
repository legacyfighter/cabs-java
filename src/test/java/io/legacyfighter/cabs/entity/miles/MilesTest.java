package io.legacyfighter.cabs.entity.miles;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static io.legacyfighter.cabs.entity.miles.ConstantUntil.constantUntil;
import static io.legacyfighter.cabs.entity.miles.ConstantUntil.constantUntilForever;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class MilesTest {

    static Instant YESTERDAY = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);
    static Instant TODAY = YESTERDAY.plus(1, ChronoUnit.DAYS);
    static Instant TOMORROW = TODAY.plus(1, ChronoUnit.DAYS);

    @Test
    void milesWithoutExpirationDateDontExpire() {
        //given
        Miles neverExpiring = constantUntilForever(10);

        //expect
        assertEquals(10, neverExpiring.getAmountFor(YESTERDAY));
        assertEquals(10, neverExpiring.getAmountFor(TODAY));
        assertEquals(10, neverExpiring.getAmountFor(TOMORROW));
    }

    @Test
    void expiringMilesExpire() {
        //given
        Miles expiringMiles = constantUntil(10, TODAY);

        //expect
        assertEquals(10, expiringMiles.getAmountFor(YESTERDAY));
        assertEquals(10, expiringMiles.getAmountFor(TODAY));
        assertEquals(0, expiringMiles.getAmountFor(TOMORROW));
    }

    @Test
    void canSubtractWhenEnoughMiles() {
        //given
        Miles expiringMiles = constantUntil(10, TODAY);
        Miles neverExpiring = constantUntilForever(10);

        //expect
        assertEquals(constantUntil(0, TODAY), expiringMiles.subtract(10, TODAY));
        assertEquals(constantUntil(0, TODAY), expiringMiles.subtract(10, YESTERDAY));

        assertEquals(constantUntil(2, TODAY), expiringMiles.subtract(8, TODAY));
        assertEquals(constantUntil(2, TODAY), expiringMiles.subtract(8, YESTERDAY));

        assertEquals(constantUntilForever(0), neverExpiring.subtract(10, YESTERDAY));
        assertEquals(constantUntilForever(0), neverExpiring.subtract(10, TODAY));
        assertEquals(constantUntilForever(0), neverExpiring.subtract(10, TOMORROW));

        assertEquals(constantUntilForever(2), neverExpiring.subtract(8, YESTERDAY));
        assertEquals(constantUntilForever(2), neverExpiring.subtract(8, TODAY));
        assertEquals(constantUntilForever(2), neverExpiring.subtract(8, TOMORROW));
    }

    @Test
    void cannotSubtractWhenNotEnoughMiles() {
        //given
        Miles neverExpiring = constantUntilForever(10);
        Miles expiringMiles = constantUntil(10, TODAY);

        //expect
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> neverExpiring.subtract(11, YESTERDAY));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> neverExpiring.subtract(11, TODAY));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> neverExpiring.subtract(11, TOMORROW));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> expiringMiles.subtract(11, YESTERDAY));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> expiringMiles.subtract(11, TODAY));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> expiringMiles.subtract(8, TOMORROW));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> expiringMiles.subtract(8, TOMORROW));
    }

}