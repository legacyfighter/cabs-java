package io.legacyfighter.cabs.tracking;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.carfleet.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class DriverSessionService {

    @Autowired
    private DriverSessionRepository driverSessionRepository;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private Clock clock;

    public DriverSession logIn(Long driverId, String plateNumber, CarClass carClass, String carBrand) {
        DriverSession session = new DriverSession();
        session.setDriverId(driverId);
        session.setLoggedAt(Instant.now(clock));
        session.setCarClass(carClass);
        session.setPlatesNumber(plateNumber);
        session.setCarBrand(carBrand);
        carTypeService.registerActiveCar(session.getCarClass());
        return driverSessionRepository.save(session);
    }

    @Transactional
    public void logOut(Long sessionId) {
        DriverSession session = driverSessionRepository.getOne(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session does not exist");
        }
        carTypeService.unregisterCar(session.getCarClass());
        session.setLoggedOutAt(Instant.now(clock));
    }

    @Transactional
    public void logOutCurrentSession(Long driverId) {
        DriverSession session = driverSessionRepository.findTopByDriverIdAndLoggedOutAtIsNullOrderByLoggedAtDesc(driverId);
        if (session != null) {
            session.setLoggedOutAt(Instant.now(clock));
            carTypeService.unregisterCar(session.getCarClass());
        }

    }

    public List<DriverSession> findByDriver(Long driverId) {
        return driverSessionRepository.findByDriverId(driverId);
    }

    public List<Long> findCurrentlyLoggedDriverIds(List<Long> driversIds, Collection<CarClass> carClasses) {
        return driverSessionRepository.findAllByLoggedOutAtNullAndDriverIdInAndCarClassIn(driversIds, carClasses)
                .stream()
                .map(DriverSession::getDriverId).collect(toList());

    }
}
