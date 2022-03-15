package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.carfleet.CarTypeDTO;
import io.legacyfighter.cabs.carfleet.CarTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static io.legacyfighter.cabs.carfleet.CarClass.VAN;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CarTypeUpdateIntegrationTest {

    @Autowired
    CarTypeService carTypeService;

    @Test
    void canCreateCarType() {
        //given
        thereIsNoCarClassInTheSystem(VAN);

        //when
        CarTypeDTO created = createCarClass("duże i dobre", VAN);

        //then
        CarTypeDTO loaded = load(created.getId());
        assertEquals(VAN, loaded.getCarClass());
        assertEquals(0, loaded.getCarsCounter());
        assertEquals(0, loaded.getActiveCarsCounter());
        assertEquals("duże i dobre", loaded.getDescription());
    }

    @Test
    void canChangeCarDescription() {
        //given
        thereIsNoCarClassInTheSystem(VAN);
        //and
        createCarClass("duże i dobre", VAN);

        //when
        CarTypeDTO changed = createCarClass("duże i bardzo dobre", VAN);

        //then
        CarTypeDTO loaded = load(changed.getId());
        assertEquals(VAN, loaded.getCarClass());
        assertEquals(0, loaded.getCarsCounter());
        assertEquals("duże i bardzo dobre", loaded.getDescription());
    }

    @Test
    void canRegisterActiveCars() {
        //given
        CarTypeDTO created = createCarClass("duże i dobre", VAN);
        //and
        int currentActiveCarsCount = load(created.getId()).getActiveCarsCounter();

        //when
        registerActiveCar(VAN);

        //then
        CarTypeDTO loaded = load(created.getId());
        assertEquals(currentActiveCarsCount + 1, loaded.getActiveCarsCounter());
    }

    @Test
    void canUnregisterActiveCars() {
        //given
        CarTypeDTO created = createCarClass("duże i dobre", VAN);
        //and
        registerActiveCar(VAN);
        //and
        int currentActiveCarsCount = load(created.getId()).getActiveCarsCounter();

        //when
        unregisterActiveCar(VAN);

        //then
        CarTypeDTO loaded = load(created.getId());
        assertEquals(currentActiveCarsCount - 1, loaded.getActiveCarsCounter());
    }


    void registerActiveCar(CarClass carClass) {
        carTypeService.registerActiveCar(carClass);
    }

    void unregisterActiveCar(CarClass carClass) {
        carTypeService.unregisterActiveCar(carClass);
    }
    
    CarTypeDTO load(Long id) {
        return carTypeService.loadDto(id);
    }

    CarTypeDTO createCarClass(String desc, CarClass carClass) {
        CarTypeDTO carTypeDTO = new CarTypeDTO();
        carTypeDTO.setCarClass(carClass);
        carTypeDTO.setDescription(desc);
        return carTypeService.loadDto(carTypeService.create(carTypeDTO).getId());
    }

    void thereIsNoCarClassInTheSystem(CarClass carClass) {
        carTypeService.removeCarType(carClass);
    }
}