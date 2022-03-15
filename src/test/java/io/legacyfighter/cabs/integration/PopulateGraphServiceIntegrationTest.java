package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.common.TestWithGraphDB;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.driverfleet.Driver;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.crm.transitanalyzer.GraphTransitAnalyzer;
import io.legacyfighter.cabs.crm.transitanalyzer.PopulateGraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static java.time.Instant.now;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PopulateGraphServiceIntegrationTest extends TestWithGraphDB {

    @Autowired
    Fixtures fixtures;

    @Autowired
    PopulateGraphService populateGraphService;

    @Autowired
    GraphTransitAnalyzer analyzer;

    @MockBean
    GeocodingService geocodingService;

    @Test
    void canPopulateGraphWithDataFromRelationalDB() {
        //given
        Client client = fixtures.aClient();
        //and
        Address address1 = new Address("100_1", "1", "1", "1", 1);
        Address address2 = new Address("100_2", "2", "2", "2", 2);
        Address address3 = new Address("100_3", "3", "3", "3", 3);
        Address address4 = new Address("100_4", "4", "4", "4", 3);
        //and
        aTransitFromTo(address1, address2, client);
        aTransitFromTo(address2, address3, client);
        aTransitFromTo(address3, address4, client);

        //when
        populateGraphService.populate();

        //then
        List<Long> result = analyzer.analyze(client.getId(), address1.getHash());
        assertThat(result).containsExactly(
                address1.getHash().longValue(),
                address2.getHash().longValue(),
                address3.getHash().longValue(),
                address4.getHash().longValue());
    }

    void aTransitFromTo(Address pickup, Address destination, Client client) {
        when(geocodingService.geocodeAddress(destination)).thenReturn(new double[]{1, 1});
        Driver driver = fixtures.aNearbyDriver(geocodingService, pickup);
        fixtures.aRide(50, client, driver, pickup, destination);
    }
}