package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.entity.DriverSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DriverSessionRepository extends JpaRepository<DriverSession, Long> {

    List<DriverSession> findAllByLoggedOutAtNullAndDriverIdInAndCarClassIn(Collection<Long> driverIds, Collection<CarClass> carClasses);

    DriverSession findTopByDriverIdAndLoggedOutAtIsNullOrderByLoggedAtDesc(Long driverId);

    List<DriverSession> findByDriverId(Long driverId);
}
