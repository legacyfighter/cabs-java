package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.dto.CarTypeDTO;
import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.entity.CarType.CarClass;
import io.legacyfighter.cabs.repository.CarTypeRepository;
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
        return new CarTypeDTO(load(id));
    }

    @Transactional
    public CarType create(CarTypeDTO carTypeDTO) {
        CarType byCarClass = carTypeRepository.findByCarClass(carTypeDTO.getCarClass());
        if (byCarClass == null) {
            CarType type = new CarType(carTypeDTO.getCarClass(), carTypeDTO.getDescription(), getMinNumberOfCars(carTypeDTO.getCarClass()));
            return carTypeRepository.save(type);
        } else {
            byCarClass.setDescription(carTypeDTO.getDescription());
            return byCarClass;
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
        CarType carType = findByCarClass(carClass);
        carType.unregisterActiveCar();
    }

    @Transactional
    public void registerActiveCar(CarClass carClass) {
        CarType carType = findByCarClass(carClass);
        carType.registerActiveCar();
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
