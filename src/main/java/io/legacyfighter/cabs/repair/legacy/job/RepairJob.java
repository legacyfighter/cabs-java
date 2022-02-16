package io.legacyfighter.cabs.repair.legacy.job;

import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import java.util.Set;

public class RepairJob extends CommonBaseAbstractJob{
    private Set<Parts> partsToRepair;
    private Money estimatedValue;

    public Money getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(Money estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public Set<Parts> getPartsToRepair() {
        return partsToRepair;
    }

    public void setPartsToRepair(Set<Parts> partsToRepair) {
        this.partsToRepair = partsToRepair;
    }
}
