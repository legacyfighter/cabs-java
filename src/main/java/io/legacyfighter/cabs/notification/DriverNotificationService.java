package io.legacyfighter.cabs.notification;

import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public void notifyAboutPossibleTransit(Long driverId, UUID requestId) {
        // find transit and delegate to notifyAboutPossibleTransit(long, long)
    }

    public void notifyAboutChangedTransitAddress(Long driverId, UUID requestId) {
        // find transit and delegate to notifyAboutChangedTransitAddress(long, long)
    }

    public void notifyAboutCancelledTransit(Long driverId, UUID requestId) {
        // find transit and delegate to notifyAboutCancelledTransit(long, long)
    }

    public void askDriverForDetailsAboutClaim(String claimNo, Long driverId) {
        // ...
    }
}
