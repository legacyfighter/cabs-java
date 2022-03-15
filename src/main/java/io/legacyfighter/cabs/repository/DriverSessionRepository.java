package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.DriverSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface DriverSessionRepository extends JpaRepository<DriverSession, Long> {

    List<DriverSession> findAllByLoggedOutAtNullAndDriverInAndCarClassIn(Collection<Driver> drivers, Collection<CarClass> carClasses);

    DriverSession findTopByDriverAndLoggedOutAtIsNullOrderByLoggedAtDesc(Driver driver);

    List<DriverSession> findAllByDriverAndLoggedAtAfter(Driver driver, Instant since);

    List<DriverSession> findByDriver(Driver driver);
}
