package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.common.TestWithGraphDB;
import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.repository.TransitRepository;
import io.legacyfighter.cabs.transitanalyzer.GraphTransitAnalyzer;
import io.legacyfighter.cabs.transitanalyzer.PopulateGraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.time.Instant.now;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateGraphServiceIntegrationTest extends TestWithGraphDB {

    @Autowired
    Fixtures fixtures;

    @Autowired
    TransitRepository transitRepository;

    @Autowired
    PopulateGraphService populateGraphService;

    @Autowired
    GraphTransitAnalyzer analyzer;

    @Test
    void canPopulateGraphWithDataFromRelationalDB() {
        //given
        Client client = fixtures.aClient();
        //and
        Driver driver = fixtures.aDriver();
        //and
        Address address1 = new Address("100_1", "1", "1", "1", 1);
        Address address2 = new Address("100_2", "2", "2", "2", 2);
        Address address3 = new Address("100_3", "3", "3", "3", 3);
        Address address4 = new Address("100_4", "4", "4", "4", 3);
        //and
        fixtures.aRequestedAndCompletedTransit(10, now(), now(), client, driver, address1, address2);
        fixtures.aRequestedAndCompletedTransit(10, now(), now(), client, driver, address2, address3);
        fixtures.aRequestedAndCompletedTransit(10, now(), now(), client, driver, address3, address4);

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
}