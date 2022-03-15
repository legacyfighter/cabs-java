package io.legacyfighter.cabs.assignment;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.carfleet.CarTypeService;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.notification.DriverNotificationService;
import io.legacyfighter.cabs.tracking.DriverPositionDTOV2;
import io.legacyfighter.cabs.tracking.DriverTrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class DriverAssignmentFacade {

    private final DriverAssignmentRepository driverAssignmentRepository;
    private final Clock clock;
    private final CarTypeService carTypeService;
    private final DriverTrackingService driverTrackingService;
    private final DriverNotificationService driverNotificationService;

    public DriverAssignmentFacade(DriverAssignmentRepository driverAssignmentRepository,
                                  Clock clock,
                                  CarTypeService carTypeService,
                                  DriverTrackingService driverTrackingService,
                                  DriverNotificationService driverNotificationService) {
        this.driverAssignmentRepository = driverAssignmentRepository;
        this.clock = clock;
        this.carTypeService = carTypeService;
        this.driverTrackingService = driverTrackingService;
        this.driverNotificationService = driverNotificationService;
    }

    @Transactional
    public InvolvedDriversSummary startAssigningDrivers(UUID transitRequestUUID, AddressDTO from, CarClass carClass, Instant when) {
        driverAssignmentRepository.save(new DriverAssignment(transitRequestUUID, when));
        return searchForPossibleDrivers(transitRequestUUID, from, carClass);
    }

    @Transactional
    public InvolvedDriversSummary searchForPossibleDrivers(UUID transitRequestUUID, AddressDTO from, CarClass carClass) {

        DriverAssignment driverAssignment = find(transitRequestUUID);

        if (driverAssignment != null) {

            Integer distanceToCheck = 0;

            // Tested on production, works as expected.
            // If you change this code and the system will collapse AGAIN, I'll find you...
            while (true) {
                if (driverAssignment.getAwaitingDriversResponses()
                        > 4) {
                    return InvolvedDriversSummary.noneFound();
                }
                distanceToCheck++;

                // FIXME: to refactor when the final business logic will be determined
                if (driverAssignment.shouldNotWaitForDriverAnyMore(Instant.now(clock)) || distanceToCheck >= 20) {
                    driverAssignment.failDriverAssignment();
                    driverAssignmentRepository.save(driverAssignment);
                    return InvolvedDriversSummary.noneFound();
                }


                List<CarClass> carClasses = choosePossibleCarClasses(carClass);
                if (carClasses.isEmpty()) {
                    return InvolvedDriversSummary.noneFound();
                }

                List<DriverPositionDTOV2> driversAvgPositions = driverTrackingService.findActiveDriversNearby(from, Distance.ofKm(distanceToCheck), carClasses);

                if (driversAvgPositions.isEmpty()) {
                    //next iteration
                    continue;
                }

                // Iterate across average driver positions
                for (DriverPositionDTOV2 driverAvgPosition : driversAvgPositions) {
                    if (driverAssignment.canProposeTo(driverAvgPosition.getDriverId())) {
                        driverAssignment.proposeTo(driverAvgPosition.getDriverId());
                        driverNotificationService.notifyAboutPossibleTransit(driverAvgPosition.getDriverId(), transitRequestUUID);
                    }

                }

                driverAssignmentRepository.save(driverAssignment);
                return loadInvolvedDrivers(driverAssignment);
            }
        } else {
            throw new IllegalArgumentException("Transit does not exist, id = " + transitRequestUUID);
        }

    }

    private List<CarClass> choosePossibleCarClasses(CarClass carClass) {
        List<CarClass> carClasses = new ArrayList<>();
        List<CarClass> activeCarClasses = carTypeService.findActiveCarClasses();
        if (carClass != null) {
            if (activeCarClasses.contains(carClass)) {
                carClasses.add(carClass);
            }
        } else {
            carClasses.addAll(activeCarClasses);
        }
        return carClasses;
    }

    @Transactional
    public InvolvedDriversSummary acceptTransit(UUID transitRequestUUID, Long driverId) {
        DriverAssignment driverAssignment = find(transitRequestUUID);
        driverAssignment.acceptBy(driverId);
        return loadInvolvedDrivers(driverAssignment);
    }

    @Transactional
    public InvolvedDriversSummary rejectTransit(UUID transitRequestUUID, Long driverId) {
        DriverAssignment driverAssignment = find(transitRequestUUID);
        if (driverAssignment == null) {
            throw new IllegalArgumentException("Assignment does not exist, id = " + transitRequestUUID);
        }
        driverAssignment.rejectBy(driverId);
        return loadInvolvedDrivers(driverAssignment);
    }

    public boolean isDriverAssigned(UUID transitRequestUUID) {
        return driverAssignmentRepository.findByRequestUUIDAndStatus(transitRequestUUID, AssignmentStatus.ON_THE_WAY) != null;
    }

    private InvolvedDriversSummary loadInvolvedDrivers(DriverAssignment driverAssignment) {
        return new InvolvedDriversSummary(driverAssignment.getProposedDrivers(), driverAssignment.getDriverRejections(), driverAssignment.getAssignedDriver(), driverAssignment.getStatus());
    }

    public InvolvedDriversSummary loadInvolvedDrivers(UUID transitRequestUUID) {
        DriverAssignment driverAssignment = find(transitRequestUUID);
        if (driverAssignment == null) {
            return InvolvedDriversSummary.noneFound();
        }
        return loadInvolvedDrivers(driverAssignment);
    }

    @Transactional
    public void cancel(UUID transitRequestUUID) {
        DriverAssignment driverAssignment = find(transitRequestUUID);
        if (driverAssignment != null) {
            driverAssignment.cancel();
            notifyAboutCancelledDestination(driverAssignment, transitRequestUUID);
        }
    }

    private DriverAssignment find(UUID transitRequestUUID) {
        return driverAssignmentRepository.findByRequestUUID(transitRequestUUID);
    }

    public void notifyAssignedDriverAboutChangedDestination(UUID transitRequestUUID) {
        DriverAssignment driverAssignment = find(transitRequestUUID);
        if (driverAssignment != null && driverAssignment.getAssignedDriver() != null) {
            Long assignedDriver = driverAssignment.getAssignedDriver();
            driverNotificationService.notifyAboutChangedTransitAddress(assignedDriver, transitRequestUUID);
            for (Long driver : driverAssignment.getProposedDrivers()) {
                driverNotificationService.notifyAboutChangedTransitAddress(driver, transitRequestUUID);
            }
        }

    }

    public void notifyProposedDriversAboutChangedDestination(UUID transitRequestUUID) {
        DriverAssignment driverAssignment = find(transitRequestUUID);
        for (Long driver : driverAssignment.getProposedDrivers()) {
            driverNotificationService.notifyAboutChangedTransitAddress(driver, transitRequestUUID);
        }
    }

    private void notifyAboutCancelledDestination(DriverAssignment driverAssignment, UUID transitRequestUUID) {
        Long assignedDriver = driverAssignment.getAssignedDriver();
        if (assignedDriver != null) {
            driverNotificationService.notifyAboutCancelledTransit(assignedDriver, transitRequestUUID);
        }
    }
}
