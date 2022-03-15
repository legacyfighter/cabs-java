package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TransitController {

    @Autowired
    private TransitService transitService;

    @GetMapping("/transits/{requestUUID}")
    public TransitDTO getTransit(@PathVariable UUID requestUUID) {
        return transitService.loadTransit(requestUUID);
    }

    @PostMapping("/transits/")
    public TransitDTO createTransit(@RequestBody TransitDTO transitDTO) {
        TransitDTO transit = transitService.createTransit(transitDTO);
        return transit;
    }

    @PostMapping("/transits/{id}/changeAddressTo")
    TransitDTO changeAddressTo(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        transitService.changeTransitAddressTo(transitService.getRequestUUID(id), addressDTO);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/changeAddressFrom")
    TransitDTO changeAddressFrom(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        transitService.changeTransitAddressFrom(transitService.getRequestUUID(id), addressDTO);
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/cancel")
    TransitDTO cancel(@PathVariable Long id) {
        transitService.cancelTransit(transitService.getRequestUUID(id));
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/publish")
    TransitDTO publishTransit(@PathVariable Long id) {
        transitService.publishTransit(transitService.getRequestUUID(id));
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/findDrivers")
    TransitDTO findDriversForTransit(@PathVariable Long id) {
        transitService.findDriversForTransit(transitService.getRequestUUID(id));
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/accept/{driverId}")
    TransitDTO acceptTransit(@PathVariable Long id, @PathVariable Long driverId) {
        transitService.acceptTransit(driverId, transitService.getRequestUUID(id));
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/start/{driverId}")
    TransitDTO start(@PathVariable Long id, @PathVariable Long driverId) {
        transitService.startTransit(driverId, transitService.getRequestUUID(id));
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/reject/{driverId}")
    TransitDTO reject(@PathVariable Long id, @PathVariable Long driverId) {
        transitService.rejectTransit(driverId, transitService.getRequestUUID(id));
        return transitService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/complete/{driverId}")
    TransitDTO complete(@PathVariable Long id, @PathVariable Long driverId, @RequestBody AddressDTO destination) {
        transitService.completeTransit(driverId, transitService.getRequestUUID(id), destination);
        return transitService.loadTransit(id);
    }

}
