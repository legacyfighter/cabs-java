package io.legacyfighter.cabs.assignment;


import java.util.HashSet;
import java.util.Set;

import static io.legacyfighter.cabs.assignment.AssignmentStatus.DRIVER_ASSIGNMENT_FAILED;

public class InvolvedDriversSummary {

    public Set<Long> proposedDrivers = new HashSet<>();
    public Set<Long> driverRejections = new HashSet<>();
    public Long assignedDriver;
    public AssignmentStatus status;

    public InvolvedDriversSummary() {
    }

    public InvolvedDriversSummary(Set<Long> proposedDrivers, Set<Long> driverRejections, Long assignedDriverId, AssignmentStatus status) {
        this.proposedDrivers = proposedDrivers;
        this.driverRejections = driverRejections;
        this.status = status;

    }

    public static InvolvedDriversSummary noneFound() {
        return new InvolvedDriversSummary(new HashSet<>(), new HashSet<>(), null, DRIVER_ASSIGNMENT_FAILED);
    }
}
