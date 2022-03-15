package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.DistanceCalculator;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.geolocation.address.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangePickupService {

    @Autowired
    private DistanceCalculator distanceCalculator;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TransitDemandRepository transitDemandRepository;

    @Transactional
    public Distance changeTransitAddressFrom(UUID requestUUID, Address newAddress, Address oldAddress) {
        newAddress = addressRepository.save(newAddress);
        TransitDemand transitDemand = transitDemandRepository.findByTransitRequestUUID(requestUUID);
        if (transitDemand == null) {
            throw new IllegalArgumentException("Transit does not exist, id = " + requestUUID);
        }

        // FIXME later: add some exceptions handling
        double[] geoFromNew = geocodingService.geocodeAddress(newAddress);
        double[] geoFromOld = geocodingService.geocodeAddress(oldAddress);

        // https://www.geeksforgeeks.org/program-distance-two-points-earth/
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double lon1 = Math.toRadians(geoFromNew[1]);
        double lon2 = Math.toRadians(geoFromOld[1]);
        double lat1 = Math.toRadians(geoFromNew[0]);
        double lat2 = Math.toRadians(geoFromOld[0]);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956 for miles
        double r = 6371;

        // calculate the result
        double distanceInKMeters = c * r;

        Distance newDistance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFromNew[0], geoFromNew[1], geoFromOld[0], geoFromOld[1]));
        transitDemand.changePickup(distanceInKMeters);
        return newDistance;
    }

    @Transactional
    public void changeTransitAddressFrom(UUID requestUUID, AddressDTO newAddress) {
        changeTransitAddressFrom(requestUUID, newAddress);
    }

}
