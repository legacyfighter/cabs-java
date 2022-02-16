package io.legacyfighter.cabs.repair.legacy.job;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;
import io.legacyfighter.cabs.repair.legacy.user.EmployeeDriverWithOwnCar;
import io.legacyfighter.cabs.repair.legacy.user.SignedContract;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RepairTest {

    @Test
    public void employeeDriverWithOwnCarCoveredByWarrantyShouldRepairForFree(){
        //given
        EmployeeDriverWithOwnCar employee = new EmployeeDriverWithOwnCar();
        employee.setContract(fullCoverageWarranty());
        //when
        JobResult result = employee.doJob(fullRepair());
        //then
        assertEquals(JobResult.Decision.ACCEPTED, result.getDecision());
        assertEquals(Money.ZERO, result.getParam("totalCost"));
        assertEquals(allParts(), result.getParam("acceptedParts"));
    }

    private RepairJob fullRepair() {
        RepairJob job = new RepairJob();
        job.setEstimatedValue(new Money(50000));
        job.setPartsToRepair(allParts());
        return job;
    }

    private SignedContract fullCoverageWarranty() {
        SignedContract contract = new SignedContract();
        contract.setCoverageRatio(100.0);
        contract.setCoveredParts(allParts());
        return contract;
    }

    private Set<Parts> allParts(){
        return Set.of(Parts.values());
    }
}
