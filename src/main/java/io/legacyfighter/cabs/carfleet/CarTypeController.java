package io.legacyfighter.cabs.carfleet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CarTypeController {

    @Autowired
    private CarTypeService carTypeService;


    @PostMapping("/cartypes")
    ResponseEntity<CarTypeDTO> create(@RequestBody CarTypeDTO carTypeDTO) {
        CarTypeDTO created = carTypeService.create(carTypeDTO);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/cartypes/{carClass}/registerCar")
    ResponseEntity<CarTypeDTO> registerCar(@PathVariable CarClass carClass) {
        carTypeService.registerCar(carClass);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cartypes/{carClass}/unregisterCar")
    ResponseEntity<CarTypeDTO> unregisterCar(@PathVariable CarClass carClass) {
        carTypeService.unregisterCar(carClass);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cartypes/{id}/activate")
    ResponseEntity<CarTypeDTO> activate(@PathVariable Long id) {
        carTypeService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cartypes/{id}/deactivate")
    ResponseEntity<CarTypeDTO> deactivate(@PathVariable Long id) {
        carTypeService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cartypes/{id}")
    ResponseEntity<CarTypeDTO> find(@PathVariable Long id) {
        CarTypeDTO carType = carTypeService.loadDto(id);
        return ResponseEntity.ok(carType);
    }
}
