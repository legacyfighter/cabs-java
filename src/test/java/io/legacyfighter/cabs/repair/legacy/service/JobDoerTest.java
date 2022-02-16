package io.legacyfighter.cabs.repair.legacy.service;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repair.legacy.job.JobResult;
import io.legacyfighter.cabs.repair.legacy.job.RepairJob;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JobDoerTest {

    /**
     * fake database returns {@link io.legacyfighter.cabs.repair.legacy.user.EmployeeDriverWithOwnCar}
     */
    private static final Long ANY_USER = 1L;

    @Autowired
    JobDoer jobDoer;

    @Test
    public void employeeWithOwnCarWithWarrantyShouldHaveCoveredAllPartsForFree(){
        JobResult result = jobDoer.repair(ANY_USER, repairJob());

        assertEquals(result.getDecision(), JobResult.Decision.ACCEPTED);
        assertEquals(result.getParam("acceptedParts"), allParts());
        assertEquals(result.getParam("totalCost"), Money.ZERO);
    }

    private RepairJob repairJob() {
        RepairJob job= new RepairJob();
        job.setPartsToRepair(allParts());
        job.setEstimatedValue(new Money(7000));
        return job;
    }

    Set<Parts> allParts(){
        return Set.of(Parts.values());
    }
}
