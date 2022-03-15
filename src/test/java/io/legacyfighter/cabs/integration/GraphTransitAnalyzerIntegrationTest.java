package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.TestWithGraphDB;
import io.legacyfighter.cabs.crm.transitanalyzer.GraphTransitAnalyzer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphTransitAnalyzerIntegrationTest extends TestWithGraphDB {

    @Autowired
    GraphTransitAnalyzer analyzer;

    @Test
    void canRecognizeNewAddress() {
        //given
        analyzer.addTransitBetweenAddresses(1L, 1L, 111, 222, Instant.now(), Instant.now());
        analyzer.addTransitBetweenAddresses(1L, 1L, 222, 333, Instant.now(), Instant.now());
        analyzer.addTransitBetweenAddresses(1L, 1L, 333, 444, Instant.now(), Instant.now());

        //when
        List<Long> result = analyzer.analyze(1L, 111);

        //then
        assertThat(result).containsExactly(111L, 222L, 333L, 444L);

    }

}