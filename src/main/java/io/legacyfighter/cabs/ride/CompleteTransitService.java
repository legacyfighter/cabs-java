package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.DistanceCalculator;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class CompleteTransitService {

    @Autowired
    private TransitRepository transitRepository;

    @Autowired
    private DistanceCalculator distanceCalculator;

    @Autowired
    private GeocodingService geocodingService;

    @Transactional
    public Money completeTransit(Long driverId, UUID requestUUID, Address from, Address destinationAddress) {
        Transit transit = transitRepository.findByTransitRequestUUID(requestUUID);

        if (transit == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }

        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(from);
        double[] geoTo = geocodingService.geocodeAddress(destinationAddress);
        Distance distance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        Money finalPrice = transit.completeAt(distance);
        transitRepository.save(transit);
        return finalPrice;
    }


}
