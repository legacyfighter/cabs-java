package io.legacyfighter.cabs.crm.transitanalyzer;

import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class Neo4jConfig {

    @Value("${neo4j.db.file:testPath}")
    private String dbPath;

    @Bean(destroyMethod = "onClose")
    GraphTransitAnalyzer graphTransitAnalyzer()  {
        return new GraphTransitAnalyzer(notConnectedOnProdYet(dbPath));
    }

    @Bean
    GraphDatabaseService notConnectedOnProdYet(String dbPath) {
        return null;
    }

    @Bean
    PopulateGraphService populateGraphService(GraphTransitAnalyzer graphTransitAnalyzer, TransitDetailsFacade transitDetailsFacade) {
        return new PopulateGraphService(graphTransitAnalyzer, transitDetailsFacade);
    }
}

