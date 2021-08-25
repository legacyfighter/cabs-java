package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.entity.Address;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {
    public double[] geocodeAddress(Address address) {
        //TODO ... call do zewnętrznego serwisu

        double geocoded[] = new double[2];

        geocoded[0] = 1f; //latitude
        geocoded[1] = 1f; //longitude

        return geocoded;
    }
}
