package io.legacyfighter.cabs.ride.details;

public enum Status {
    DRAFT,
    CANCELLED,
    WAITING_FOR_DRIVER_ASSIGNMENT,
    DRIVER_ASSIGNMENT_FAILED,
    TRANSIT_TO_PASSENGER,
    IN_TRANSIT,
    COMPLETED
}
