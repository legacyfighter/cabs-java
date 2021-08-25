package io.legacyfighter.cabs.service;

import org.springframework.stereotype.Service;

@Service
public class DriverNotificationService {
    public void notifyAboutPossibleTransit(Long driverId, Long transitId) {
        // ...
    }

    public void notifyAboutChangedTransitAddress(Long driverId, Long transitId) {
        // ...
    }

    public void notifyAboutCancelledTransit(Long driverId, Long transitId) {
        // ...
    }

    public void askDriverForDetailsAboutClaim(String claimNo, Long driverId) {
        // ...
    }
}
