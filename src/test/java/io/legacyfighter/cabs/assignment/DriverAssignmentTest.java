package io.legacyfighter.cabs.assignment;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static io.legacyfighter.cabs.assignment.AssignmentStatus.*;
import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DriverAssignmentTest {

    static final long DRIVER = 1L;
    static final Long SECOND_DRIVER = 2L;

    @Test
    void canAcceptTransit() {
        //given
        DriverAssignment assignment = assigmentForTransit(now());
        //and
        assignment.proposeTo(DRIVER);

        //when
        assignment.acceptBy(DRIVER);
        //then
        assertEquals(ON_THE_WAY, assignment.getStatus());
    }

    @Test
    void onlyOneDriverCanAcceptTransit() {
        //given
        DriverAssignment assignment = assigmentForTransit(now());
        //and
        assignment.proposeTo(DRIVER);
        //and
        assignment.acceptBy(DRIVER);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> assignment.acceptBy(SECOND_DRIVER));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoAlreadyRejected() {
        //given
        DriverAssignment assignment = assigmentForTransit(now());
        //and
        assignment.rejectBy(DRIVER);

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> assignment.acceptBy(DRIVER));
    }

    @Test
    void transitCannotByAcceptedByDriverWhoHasNotSeenProposal() {
        //given
        DriverAssignment assignment = assigmentForTransit(now());

        //expect
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> assignment.acceptBy(DRIVER));
    }


    @Test
    void canRejectTransit() {
        //given
        DriverAssignment assignment = assigmentForTransit(now());

        //when
        assignment.rejectBy(DRIVER);

        //then
        assertEquals(WAITING_FOR_DRIVER_ASSIGNMENT, assignment.getStatus());
    }

    DriverAssignment assigmentForTransit(Instant when) {
        return new DriverAssignment(UUID.randomUUID(), when);
    }
}