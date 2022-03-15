package io.legacyfighter.cabs.driverfleet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverRepository driverRepository;

    @PostMapping("/drivers")
    DriverDTO createDriver(@RequestParam String license, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String photo) {
        Driver driver = driverService.createDriver(license, lastName, firstName, Driver.Type.CANDIDATE, Driver.Status.INACTIVE, photo);

        return driverService.loadDriver(driver.getId());
    }

    @GetMapping("/drivers/{id}")
    DriverDTO getDriver(@PathVariable Long id) {
        return driverService.loadDriver(id);
    }

    @PostMapping("/drivers/{id}")
    DriverDTO updateDriver(@PathVariable Long id) {

        return driverService.loadDriver(id);
    }

    @PostMapping("/drivers/{id}/deactivate")
    DriverDTO deactivateDriver(@PathVariable Long id) {
        driverService.changeDriverStatus(id, Driver.Status.INACTIVE);

        return driverService.loadDriver(id);
    }

    @PostMapping("/drivers/{id}/activate")
    DriverDTO activateDriver(@PathVariable Long id) {
        driverService.changeDriverStatus(id, Driver.Status.ACTIVE);

        return driverService.loadDriver(id);
    }

}
