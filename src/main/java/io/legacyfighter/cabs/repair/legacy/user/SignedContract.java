package io.legacyfighter.cabs.repair.legacy.user;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.Set;

@Entity
public class SignedContract extends BaseEntity {
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Parts> coveredParts;

    private Double coverageRatio;

    public Double getCoverageRatio() {
        return coverageRatio;
    }

    public void setCoverageRatio(Double coverageRatio) {
        this.coverageRatio = coverageRatio;
    }

    public Set<Parts> getCoveredParts() {
        return coveredParts;
    }

    public void setCoveredParts(Set<Parts> coveredParts) {
        this.coveredParts = coveredParts;
    }
}
