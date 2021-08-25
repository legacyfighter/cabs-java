package io.legacyfighter.cabs.ui;

import io.legacyfighter.cabs.dto.CarTypeDTO;
import io.legacyfighter.cabs.dto.ClaimDTO;
import io.legacyfighter.cabs.entity.CarType;
import io.legacyfighter.cabs.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CarTypeController {

    @Autowired
    private CarTypeService carTypeService;


    @PostMapping("/cartypes")
    ResponseEntity<CarTypeDTO> create(@RequestBody CarTypeDTO carTypeDTO) {
        CarType created = carTypeService.create(carTypeDTO);
        return ResponseEntity.ok(new CarTypeDTO(created));
    }

    @PostMapping("/cartypes/{carClass}/registerCar")
    ResponseEntity<ClaimDTO> registerCar(@PathVariable CarType.CarClass carClass) {
        carTypeService.registerCar(carClass);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cartypes/{carClass}/unregisterCar")
    ResponseEntity<ClaimDTO> unregisterCar(@PathVariable CarType.CarClass carClass) {
        carTypeService.unregisterCar(carClass);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cartypes/{id}/activate")
    ResponseEntity<ClaimDTO> activate(@PathVariable Long id) {
        carTypeService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cartypes/{id}/deactivate")
    ResponseEntity<ClaimDTO> deactivate(@PathVariable Long id) {
        carTypeService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cartypes/{id}")
    ResponseEntity<CarTypeDTO> find(@PathVariable Long id) {
        CarTypeDTO carType = carTypeService.loadDto(id);
        return ResponseEntity.ok(carType);
    }
}
