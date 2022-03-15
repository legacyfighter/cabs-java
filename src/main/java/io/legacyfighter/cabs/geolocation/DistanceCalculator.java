package io.legacyfighter.cabs.geolocation;

import org.springframework.stereotype.Service;

@Service
public class DistanceCalculator {
    public double calculateByMap(double latitudeFrom, double longitudeFrom, double latitudeTo, double longitudeTo) {
        // ...

        return 42;
    }

    public double calculateByGeo(double latitudeFrom, double longitudeFrom, double latitudeTo, double longitudeTo) {
        // https://www.geeksforgeeks.org/program-distance-two-points-earth/
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double lon1 = Math.toRadians(longitudeFrom);
        double lon2 = Math.toRadians(longitudeTo);
        double lat1 = Math.toRadians(latitudeFrom);
        double lat2 = Math.toRadians(latitudeTo);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956 for miles
        double r = 6371;

        // calculate the result
        double distanceInKMeters = c * r;

        return distanceInKMeters;
    }
}
