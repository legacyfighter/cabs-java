package io.legacyfighter.cabs.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface DriverAssignmentRepository extends JpaRepository<DriverAssignment, Long> {

    DriverAssignment findByRequestId(UUID requestId);

    DriverAssignment findByRequestIdAndStatus(UUID transitId, AssignmentStatus status);

}
