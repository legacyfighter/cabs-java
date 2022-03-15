package io.legacyfighter.cabs.assignment;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.common.JsonToCollectionMapper;

import javax.persistence.Entity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
class DriverAssignment extends BaseEntity {

    private UUID requestUUID;

    private Instant publishedAt;

    private AssignmentStatus status = AssignmentStatus.WAITING_FOR_DRIVER_ASSIGNMENT;

    private Long assignedDriver;

    private String driversRejections;

    private String proposedDrivers;

    private Integer awaitingDriversResponses = 0;

    public DriverAssignment() {
    }

    DriverAssignment(UUID requestUUID, Instant publishedAt) {
        this.requestUUID = requestUUID;
        this.publishedAt = publishedAt;
    }

    void cancel() {
        if (!EnumSet.of(AssignmentStatus.WAITING_FOR_DRIVER_ASSIGNMENT, AssignmentStatus.ON_THE_WAY).contains(this.getStatus())) {
            throw new IllegalStateException("Transit cannot be cancelled, id = " + getId());
        }
        this.status = AssignmentStatus.CANCELLED;
        this.assignedDriver = null;
        this.awaitingDriversResponses = 0;

    }

    boolean canProposeTo(Long driverId) {
        return !this.getDriverRejections()
                .contains(driverId);
    }

    void proposeTo(Long driverId) {
        if (canProposeTo(driverId)) {
            addDriverToProposed(driverId);
            this.awaitingDriversResponses++;
        }
    }

    private void addDriverToProposed(Long driverId) {
        Set<Long> proposedDriversSet = getProposedDrivers();
        proposedDriversSet.add(driverId);
        proposedDrivers = JsonToCollectionMapper.serialize(proposedDriversSet);
    }

    void failDriverAssignment() {
        this.status = AssignmentStatus.DRIVER_ASSIGNMENT_FAILED;
        this.assignedDriver = null;
        this.awaitingDriversResponses = 0;
    }

    boolean shouldNotWaitForDriverAnyMore(Instant date) {
        return (status.equals(AssignmentStatus.CANCELLED) || publishedAt.plus(300, ChronoUnit.SECONDS).isBefore(date));
    }

    void acceptBy(Long driverId) {
        if (this.assignedDriver != null) {
            throw new IllegalStateException("Transit already accepted, id = " + getId());
        } else {
            if (!getProposedDrivers().contains(driverId)) {
                throw new IllegalStateException("Driver out of possible drivers, id = " + getId());
            } else {
                if (getDriverRejections().contains(driverId)) {
                    throw new IllegalStateException("Driver out of possible drivers, id = " + getId());
                }
            }
            this.assignedDriver = driverId;
            this.awaitingDriversResponses = 0;
            this.status = AssignmentStatus.ON_THE_WAY;
        }
    }

    void rejectBy(Long driverId) {
        addToDriverRejections(driverId);
        awaitingDriversResponses--;
    }

    private void addToDriverRejections(Long driverId) {
        Set<Long> driverRejectionSet = getDriverRejections();
        driverRejectionSet.add(driverId);
        driversRejections = JsonToCollectionMapper.serialize(driverRejectionSet);
    }

    Set<Long> getDriverRejections() {
        return JsonToCollectionMapper.deserialize(driversRejections);
    }

    Set<Long> getProposedDrivers() {
        return JsonToCollectionMapper.deserialize(proposedDrivers);
    }

    Long getAssignedDriver() {
        return assignedDriver;
    }

    AssignmentStatus getStatus() {
        return status;
    }

    Integer getAwaitingDriversResponses() {
        return awaitingDriversResponses;
    }

    UUID getRequestUUID() {
        return requestUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DriverAssignment))
            return false;

        DriverAssignment other = (DriverAssignment) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }

}
