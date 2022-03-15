package io.legacyfighter.cabs.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DriverSessionController {

    @Autowired
    private DriverSessionService driverSessionService;

    @Autowired
    private Clock clock;

    @PostMapping("/drivers/{driverId}/driverSessions/login")
    public ResponseEntity logIn(@PathVariable Long driverId, @RequestBody DriverSessionDTO dto) {
        driverSessionService.logIn(driverId, dto.getPlatesNumber(), dto.getCarClass(), dto.getCarBrand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/drivers/{driverId}/driverSessions/{sessionId}")
    public ResponseEntity logOut(@PathVariable Long driverId, @PathVariable Long sessionId) {
        driverSessionService.logOut(sessionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/drivers/{driverId}/driverSessions/")
    public ResponseEntity logOutCurrent(@PathVariable Long driverId) {
        driverSessionService.logOutCurrentSession(driverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/drivers/{driverId}/driverSessions/")
    public ResponseEntity<List<DriverSessionDTO>> list(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverSessionService.findByDriver(driverId)
                .stream()
                .map(DriverSessionDTO::new)
                .collect(Collectors.toList()));
    }
}

