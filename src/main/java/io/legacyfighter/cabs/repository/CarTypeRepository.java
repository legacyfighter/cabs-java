package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.entity.CarTypeActiveCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarTypeRepository {

    private final CarTypeEntityRepository carTypeEntityRepository;
    private final CarTypeActiveCounterRepository carTypeActiveCounterRepository;

    public CarTypeRepository(CarTypeEntityRepository carTypeEntityRepository, CarTypeActiveCounterRepository carTypeActiveCounterRepository) {
        this.carTypeEntityRepository = carTypeEntityRepository;
        this.carTypeActiveCounterRepository = carTypeActiveCounterRepository;
    }

    public CarType findByCarClass(CarType.CarClass carClass) {
        return carTypeEntityRepository.findByCarClass(carClass);
    }

    public CarTypeActiveCounter findActiveCounter(CarType.CarClass carClass) {
        return carTypeActiveCounterRepository.findByCarClass(carClass);
    }

    public List<CarType> findByStatus(CarType.Status status) {
        return carTypeEntityRepository.findByStatus(status);
    }

    public CarType save(CarType carType) {
        carTypeActiveCounterRepository.save(new CarTypeActiveCounter(carType.getCarClass()));
        return carTypeEntityRepository.save(carType);
    }

    public CarType getOne(Long id) {
        return carTypeEntityRepository.getOne(id);
    }

    public void delete(CarType carType) {
        carTypeEntityRepository.delete(carType);
        carTypeActiveCounterRepository.delete(carTypeActiveCounterRepository.findByCarClass(carType.getCarClass()));
    }

    public void incrementCounter(CarType.CarClass carClass) {
        carTypeActiveCounterRepository.incrementCounter(carClass);
    }

    public void decrementCounter(CarType.CarClass carClass) {
        carTypeActiveCounterRepository.decrementCounter(carClass);
    }
}


interface CarTypeEntityRepository extends JpaRepository<CarType, Long> {

    CarType findByCarClass(CarType.CarClass carClass);

    List<CarType> findByStatus(CarType.Status status);
}

interface CarTypeActiveCounterRepository extends CrudRepository<CarTypeActiveCounter, Long> {

    CarTypeActiveCounter findByCarClass(CarType.CarClass carClass);

    @Modifying
    @Query(
            value = "UPDATE car_type_active_counter counter SET active_cars_counter = active_cars_counter + 1 where counter.car_class = :#{#carClass.name()}",
            nativeQuery = true)
    int incrementCounter(@Param("carClass")CarType.CarClass carClass);

    @Modifying
    @Query(
            value = "UPDATE car_type_active_counter counter SET active_cars_counter = active_cars_counter - 1 where counter.car_class = :#{#carClass.name()}",
            nativeQuery = true)
    int decrementCounter(CarType.CarClass carClass);
}

