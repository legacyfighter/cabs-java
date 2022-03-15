package io.legacyfighter.cabs.carfleet;

import io.legacyfighter.cabs.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarTypeService {

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private AppProperties appProperties;

    @Transactional
    public CarType load(Long id) {
        CarType carType = carTypeRepository.getOne(id);
        if (carType == null) {
            throw new IllegalStateException("Cannot find car type");
        }
        return carType;
    }

    @Transactional
    public CarTypeDTO loadDto(Long id) {
        CarType loaded = load(id);
        return new CarTypeDTO(loaded, carTypeRepository.findActiveCounter(loaded.getCarClass()).getActiveCarsCounter());
    }

    @Transactional
    public CarTypeDTO create(CarTypeDTO carTypeDTO) {
        CarType byCarClass = carTypeRepository.findByCarClass(carTypeDTO.getCarClass());
        if (byCarClass == null) {
            CarType type = new CarType(carTypeDTO.getCarClass(), carTypeDTO.getDescription(), getMinNumberOfCars(carTypeDTO.getCarClass()));
            return loadDto(carTypeRepository.save(type).getId());
        } else {
            byCarClass.setDescription(carTypeDTO.getDescription());
            return loadDto(carTypeRepository.findByCarClass(carTypeDTO.getCarClass()).getId());
        }
    }

    @Transactional
    public void activate(Long id) {
        CarType carType = load(id);
        carType.activate();
    }

    @Transactional
    public void deactivate(Long id) {
        CarType carType = load(id);
        carType.deactivate();
    }

    @Transactional
    public void registerCar(CarClass carClass) {
        CarType carType = findByCarClass(carClass);
        carType.registerCar();
    }

    @Transactional
    public void unregisterCar(CarClass carClass) {
        CarType carType = findByCarClass(carClass);
        carType.unregisterCar();
    }

    @Transactional
    public void unregisterActiveCar(CarClass carClass) {
        carTypeRepository.decrementCounter(carClass);
    }

    @Transactional
    public void registerActiveCar(CarClass carClass) {
        carTypeRepository.incrementCounter(carClass);
    }

    @Transactional
    public List<CarClass> findActiveCarClasses() {
        return carTypeRepository.findByStatus(CarType.Status.ACTIVE)
                .stream()
                .map(CarType::getCarClass)
                .collect(Collectors.toList());
    }

    private Integer getMinNumberOfCars(CarClass carClass) {
        if (carClass.equals(CarClass.ECO)) {
            return appProperties.getMinNoOfCarsForEcoClass();
        } else {
            return 10;
        }
    }


    @Transactional
    public void removeCarType(CarClass carClass) {
        CarType carType = carTypeRepository.findByCarClass(carClass);
        if (carType != null) {
            carTypeRepository.delete(carType);
        }
    }

    private CarType findByCarClass(CarClass carClass) {
        CarType byCarClass = carTypeRepository.findByCarClass(carClass);
        if (byCarClass == null) {
            throw new IllegalArgumentException("Car class does not exist: " + carClass);
        }
        return byCarClass;
    }
}
