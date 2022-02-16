package io.legacyfighter.cabs.repair.legacy.user;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repair.legacy.job.JobResult;
import io.legacyfighter.cabs.repair.legacy.job.RepairJob;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
public class EmployeeDriverWithOwnCar extends EmployeeDriver{

    @OneToOne
    private SignedContract contract;

    @Override
    protected JobResult handle(RepairJob job) {
        Set<Parts> acceptedParts = new HashSet<>(job.getPartsToRepair());
        acceptedParts.retainAll(contract.getCoveredParts());

        Money coveredCost = job.getEstimatedValue().percentage(contract.getCoverageRatio());
        Money totalCost = job.getEstimatedValue().subtract(coveredCost);

        return new JobResult(JobResult.Decision.ACCEPTED).addParam("totalCost", totalCost).addParam("acceptedParts", acceptedParts);
    }

    public void setContract(SignedContract contract) {
        this.contract = contract;
    }
}
