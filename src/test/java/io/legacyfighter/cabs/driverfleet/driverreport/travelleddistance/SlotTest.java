package io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance.TimeSlot.slotThatContains;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class SlotTest {

    static Instant NOON = LocalDateTime.of(1989, 12, 12, 12, 10).toInstant(ZoneOffset.UTC);
    static Instant NOON_FIVE = NOON.plus(5, MINUTES);
    static final Instant NOON_TEN = NOON_FIVE.plus(5, MINUTES);

    @Test
    void beginningMustBeBeforeEnd() {
        //expect
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> TimeSlot.of(NOON_FIVE, NOON));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> TimeSlot.of(NOON_TEN, NOON));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> TimeSlot.of(NOON_TEN, NOON_FIVE));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> TimeSlot.of(NOON_TEN, NOON_TEN));
    }

    @Test
    void canCreateValidSlot() {
        //given
        TimeSlot noonToFive = TimeSlot.of(NOON, NOON_FIVE);
        TimeSlot fiveToTen = TimeSlot.of(NOON_FIVE, NOON_TEN);

        //expect
        assertEquals(NOON, noonToFive.beginning());
        assertEquals(NOON_FIVE, noonToFive.end());
        assertEquals(NOON_FIVE, fiveToTen.beginning());
        assertEquals(NOON_TEN, fiveToTen.end());
    }

    @Test
    void canCreatePreviousSLot() {
        //given
        TimeSlot noonToFive = TimeSlot.of(NOON, NOON_FIVE);
        TimeSlot fiveToTen = TimeSlot.of(NOON_FIVE, NOON_TEN);
        TimeSlot tenToFifteen = TimeSlot.of(NOON_TEN, NOON_TEN.plus(5, MINUTES));

        //expect
        assertEquals(noonToFive, fiveToTen.prev());
        assertEquals(fiveToTen, tenToFifteen.prev());
        assertEquals(noonToFive, tenToFifteen.prev().prev());
    }

    @Test
    void canCalculateIfTimestampIsWithin() {
        //given
        TimeSlot noonToFive = TimeSlot.of(NOON, NOON_FIVE);
        TimeSlot fiveToTen = TimeSlot.of(NOON_FIVE, NOON_TEN);

        //expect
        assertTrue(noonToFive.contains(NOON));
        assertTrue(noonToFive.contains(NOON.plus(1, MINUTES)));
        assertFalse(noonToFive.contains(NOON_FIVE));
        assertFalse(noonToFive.contains(NOON_FIVE.plus(1, MINUTES)));

        assertFalse(noonToFive.isBefore(NOON));
        assertFalse(noonToFive.isBefore(NOON_FIVE));
        assertTrue(noonToFive.isBefore(NOON_TEN));

        assertTrue(noonToFive.endsAt(NOON_FIVE));

        assertFalse(fiveToTen.contains(NOON));
        assertTrue(fiveToTen.contains(NOON_FIVE));
        assertTrue(fiveToTen.contains(NOON_FIVE.plus(1, MINUTES)));
        assertFalse(fiveToTen.contains(NOON_TEN));
        assertFalse(fiveToTen.contains(NOON_TEN.plus(1, MINUTES)));

        assertFalse(fiveToTen.isBefore(NOON));
        assertFalse(fiveToTen.isBefore(NOON_FIVE));
        assertFalse(fiveToTen.isBefore(NOON_TEN));
        assertTrue(fiveToTen.isBefore(NOON_TEN.plus(1, MINUTES)));

        assertTrue(fiveToTen.endsAt(NOON_TEN));
    }

    @Test
    void canCreateSlotFromSeedWithinThatSlot() {
        //expect
        assertEquals(TimeSlot.of(NOON, NOON_FIVE), slotThatContains(NOON.plus(1, MINUTES)));
        assertEquals(TimeSlot.of(NOON, NOON_FIVE), slotThatContains(NOON.plus(2, MINUTES)));
        assertEquals(TimeSlot.of(NOON, NOON_FIVE), slotThatContains(NOON.plus(3, MINUTES)));
        assertEquals(TimeSlot.of(NOON, NOON_FIVE), slotThatContains(NOON.plus(4, MINUTES)));

        assertEquals(TimeSlot.of(NOON_FIVE, NOON_TEN), slotThatContains(NOON_FIVE.plus(1, MINUTES)));
        assertEquals(TimeSlot.of(NOON_FIVE, NOON_TEN), slotThatContains(NOON_FIVE.plus(2, MINUTES)));
        assertEquals(TimeSlot.of(NOON_FIVE, NOON_TEN), slotThatContains(NOON_FIVE.plus(3, MINUTES)));
    }
}