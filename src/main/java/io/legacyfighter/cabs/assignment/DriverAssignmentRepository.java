package io.legacyfighter.cabs.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface DriverAssignmentRepository extends JpaRepository<DriverAssignment, Long> {

    DriverAssignment findByRequestUUID(UUID transitRequestUUID);

    DriverAssignment findByRequestUUIDAndStatus(UUID transitRequestUUID, AssignmentStatus status);

}
