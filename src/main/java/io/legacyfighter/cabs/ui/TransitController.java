package io.legacyfighter.cabs.ui;

import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.service.TransitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransitController {

    @Autowired
    private TransitService transitService;

    @GetMapping("/transits/{id}")
    public TransitDTO getTransit(@PathVariable Long id) {
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/")
    TransitDTO createTransit(@RequestBody TransitDTO transitDTO) {
        Transit transit = transitService.createTransit(transitDTO);
        return transitService.loadTransit(transit.getId());
    }

    @PostMapping("/transits/{id}/changeAddressTo")
    TransitDTO changeAddressTo(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        transitService.changeTransitAddressTo(id, addressDTO);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/changeAddressFrom")
    TransitDTO changeAddressFrom(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        transitService.changeTransitAddressFrom(id, addressDTO);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/cancel")
    TransitDTO cancel(@PathVariable Long id) {
        transitService.cancelTransit(id);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/publish")
    TransitDTO publishTransit(@PathVariable Long id) {
        transitService.publishTransit(id);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/findDrivers")
    TransitDTO findDriversForTransit(@PathVariable Long id) {
        transitService.findDriversForTransit(id);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/accept/{driverId}")
    TransitDTO acceptTransit(@PathVariable Long id, @PathVariable Long driverId) {
        transitService.acceptTransit(driverId, id);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/start/{driverId}")
    TransitDTO start(@PathVariable Long id, @PathVariable Long driverId) {
        transitService.startTransit(driverId, id);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/reject/{driverId}")
    TransitDTO reject(@PathVariable Long id, @PathVariable Long driverId) {
        transitService.rejectTransit(driverId, id);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/complete/{driverId}")
    TransitDTO complete(@PathVariable Long id, @PathVariable Long driverId, @RequestBody AddressDTO destination) {
        transitService.completeTransit(driverId, id, destination);
        return transitService.loadTransit(id);
    }

}
