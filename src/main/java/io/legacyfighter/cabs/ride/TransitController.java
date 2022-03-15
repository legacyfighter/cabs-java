package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TransitController {

    @Autowired
    private RideService rideService;

    @GetMapping("/transits/{requestUUID}")
    public TransitDTO getTransit(@PathVariable UUID requestUUID) {
        return rideService.loadTransit(requestUUID);
    }

    @PostMapping("/transits/")
    public TransitDTO createTransit(@RequestBody TransitDTO transitDTO) {
        TransitDTO transit = rideService.createTransit(transitDTO);
        return transit;
    }

    @PostMapping("/transits/{id}/changeAddressTo")
    TransitDTO changeAddressTo(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        rideService.changeTransitAddressTo(rideService.getRequestUUID(id), addressDTO);
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/changeAddressFrom")
    TransitDTO changeAddressFrom(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        rideService.changeTransitAddressFrom(rideService.getRequestUUID(id), addressDTO);
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/cancel")
    TransitDTO cancel(@PathVariable Long id) {
        rideService.cancelTransit(rideService.getRequestUUID(id));
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/publish")
    TransitDTO publishTransit(@PathVariable Long id) {
        rideService.publishTransit(rideService.getRequestUUID(id));
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/findDrivers")
    TransitDTO findDriversForTransit(@PathVariable Long id) {
        rideService.findDriversForTransit(rideService.getRequestUUID(id));
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/accept/{driverId}")
    TransitDTO acceptTransit(@PathVariable Long id, @PathVariable Long driverId) {
        rideService.acceptTransit(driverId, rideService.getRequestUUID(id));
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/start/{driverId}")
    TransitDTO start(@PathVariable Long id, @PathVariable Long driverId) {
        rideService.startTransit(driverId, rideService.getRequestUUID(id));
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/reject/{driverId}")
    TransitDTO reject(@PathVariable Long id, @PathVariable Long driverId) {
        rideService.rejectTransit(driverId, rideService.getRequestUUID(id));
        return rideService.loadTransit(id);
    }

    @PostMapping("/transits/{id}/complete/{driverId}")
    TransitDTO complete(@PathVariable Long id, @PathVariable Long driverId, @RequestBody AddressDTO destination) {
        rideService.completeTransit(driverId, rideService.getRequestUUID(id), destination);
        return rideService.loadTransit(id);
    }

}
