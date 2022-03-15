package io.legacyfighter.cabs.carfleet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class CarTypeRepository {

    private final CarTypeEntityRepository carTypeEntityRepository;
    private final CarTypeActiveCounterRepository carTypeActiveCounterRepository;

    CarTypeRepository(CarTypeEntityRepository carTypeEntityRepository, CarTypeActiveCounterRepository carTypeActiveCounterRepository) {
        this.carTypeEntityRepository = carTypeEntityRepository;
        this.carTypeActiveCounterRepository = carTypeActiveCounterRepository;
    }

    CarType findByCarClass(CarClass carClass) {
        return carTypeEntityRepository.findByCarClass(carClass);
    }

    CarTypeActiveCounter findActiveCounter(CarClass carClass) {
        return carTypeActiveCounterRepository.findByCarClass(carClass);
    }

    List<CarType> findByStatus(CarType.Status status) {
        return carTypeEntityRepository.findByStatus(status);
    }

    CarType save(CarType carType) {
        carTypeActiveCounterRepository.save(new CarTypeActiveCounter(carType.getCarClass()));
        return carTypeEntityRepository.save(carType);
    }

    CarType getOne(Long id) {
        return carTypeEntityRepository.getOne(id);
    }

    void delete(CarType carType) {
        carTypeEntityRepository.delete(carType);
        carTypeActiveCounterRepository.delete(carTypeActiveCounterRepository.findByCarClass(carType.getCarClass()));
    }

    void incrementCounter(CarClass carClass) {
        carTypeActiveCounterRepository.incrementCounter(carClass);
    }

    void decrementCounter(CarClass carClass) {
        carTypeActiveCounterRepository.decrementCounter(carClass);
    }
}


interface CarTypeEntityRepository extends JpaRepository<CarType, Long> {

    CarType findByCarClass(CarClass carClass);

    List<CarType> findByStatus(CarType.Status status);
}

interface CarTypeActiveCounterRepository extends CrudRepository<CarTypeActiveCounter, Long> {

    CarTypeActiveCounter findByCarClass(CarClass carClass);

    @Modifying
    @Query(
            value = "UPDATE car_type_active_counter counter SET active_cars_counter = active_cars_counter + 1 where counter.car_class = :#{#carClass.name()}",
            nativeQuery = true)
    int incrementCounter(@Param("carClass") CarClass carClass);

    @Modifying
    @Query(
            value = "UPDATE car_type_active_counter counter SET active_cars_counter = active_cars_counter - 1 where counter.car_class = :#{#carClass.name()}",
            nativeQuery = true)
    int decrementCounter(CarClass carClass);
}

