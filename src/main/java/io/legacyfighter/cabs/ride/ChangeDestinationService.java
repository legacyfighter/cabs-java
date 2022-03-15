package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.DistanceCalculator;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class ChangeDestinationService {

    @Autowired
    private TransitRepository transitRepository;

    @Autowired
    private DistanceCalculator distanceCalculator;

    @Autowired
    private GeocodingService geocodingService;

    @Transactional
    public Distance changeTransitAddressTo(UUID requestUUID, Address newAddress, Address from) {
        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(from);
        double[] geoTo = geocodingService.geocodeAddress(newAddress);
        Distance newDistance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        Transit transit = transitRepository.findByTransitRequestUUID(requestUUID);
        if (transit != null) {
            transit.changeDestination(newDistance);
        }
        return newDistance;

    }


}
