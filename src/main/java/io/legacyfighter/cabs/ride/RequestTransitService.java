package io.legacyfighter.cabs.ride;

import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.geolocation.DistanceCalculator;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.pricing.Tariff;
import io.legacyfighter.cabs.pricing.Tariffs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class RequestTransitService {

    private final DistanceCalculator distanceCalculator;
    private final GeocodingService geocodingService;
    private final Clock clock;
    private final RequestForTransitRepository requestForTransitRepository;
    private final Tariffs tariffs;

    public RequestTransitService(DistanceCalculator distanceCalculator, GeocodingService geocodingService, Clock clock, RequestForTransitRepository requestForTransitRepository, Tariffs tariffs) {
        this.distanceCalculator = distanceCalculator;
        this.geocodingService = geocodingService;
        this.clock = clock;
        this.requestForTransitRepository = requestForTransitRepository;
        this.tariffs = tariffs;
    }

    @Transactional
    public RequestForTransit createRequestForTransit(Address from, Address to) {

        // FIXME later: add some exceptions handling
        double[] geoFrom = geocodingService.geocodeAddress(from);
        double[] geoTo = geocodingService.geocodeAddress(to);
        Distance distance = Distance.ofKm((float) distanceCalculator.calculateByMap(geoFrom[0], geoFrom[1], geoTo[0], geoTo[1]));
        Instant now = Instant.now(clock);
        Tariff tariff = chooseTariff(now);
        return requestForTransitRepository.save(new RequestForTransit(tariff, distance));
    }

    private Tariff chooseTariff(Instant when) {
        return tariffs.choose(when);
    }


    public UUID findCalculationUUID(Long requestId) {
        return requestForTransitRepository.getOne(requestId).getRequestUUID();
    }

    public Tariff findTariff(UUID requestUUID) {
        return requestForTransitRepository.findByRequestUUID(requestUUID).getTariff();
    }
}
