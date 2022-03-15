package io.legacyfighter.cabs.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
public class DriverTrackingController {
    @Autowired
    private DriverTrackingService trackingService;

    @PostMapping("/driverPositions/")
    ResponseEntity<DriverPositionDTO> create(DriverPositionDTO driverPositionDTO) {
        DriverPosition driverPosition = trackingService.registerPosition(driverPositionDTO.getDriverId(), driverPositionDTO.getLatitude(), driverPositionDTO.getLongitude(), driverPositionDTO.getSeenAt());
        return ResponseEntity.ok(toDto(driverPosition));
    }

    @GetMapping("/driverPositions/{id}/total")
    double calculateTravelledDistance(@PathVariable Long id, @RequestParam Instant from, @RequestParam Instant to) {
        return trackingService.calculateTravelledDistance(id, from, to).toKmInDouble();
    }

    private DriverPositionDTO toDto(DriverPosition driverPosition) {
        DriverPositionDTO dto = new DriverPositionDTO();
        dto.setDriverId(driverPosition.getDriverId());
        dto.setLatitude(driverPosition.getLatitude());
        dto.setLongitude(driverPosition.getLongitude());
        dto.setSeenAt(driverPosition.getSeenAt());
        return dto;
    }
}
