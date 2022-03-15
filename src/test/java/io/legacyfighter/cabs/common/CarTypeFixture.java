package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.dto.CarTypeDTO;
import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
class CarTypeFixture {

    @Autowired
    CarTypeService carTypeService;

    CarType anActiveCarCategory(CarType.CarClass carClass) {
        CarTypeDTO carTypeDTO = new CarTypeDTO();
        carTypeDTO.setCarClass(carClass);
        carTypeDTO.setDescription("opis");
        CarType carType = carTypeService.create(carTypeDTO);
        IntStream.range(1, carType.getMinNoOfCarsToActivateClass() + 1)
                .forEach(i -> carTypeService.registerCar(carType.getCarClass()));
        carTypeService.activate(carType.getId());
        return carType;
    }
}
