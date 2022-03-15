package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.CabsApplication;
import io.legacyfighter.cabs.crm.transitanalyzer.GraphTransitAnalyzer;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PreDestroy;
import java.io.File;


@SpringBootTest(properties = "neo4j.db.file=${random.int}", classes = {CabsApplication.class, TestWithGraphDB.TestNeo4jConfig.class})
public class TestWithGraphDB {

    static class TestNeo4jConfig {

        @Value("${neo4j.db.file}")
        private String dbPath;

        GraphDatabaseService testGraphDatabaseService() {
            GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
            FileSystemUtils.deleteRecursively(new File("db"));
            File storeDir = new File("db/" + dbPath);
            return graphDbFactory.newEmbeddedDatabase(storeDir);
        }

        @Bean(destroyMethod = "onClose")
        @Primary
        GraphTransitAnalyzer testGraphTransitAnalyzer()  {
            return new GraphTransitAnalyzer(testGraphDatabaseService());
        }

        @PreDestroy
        void cleanDbDir() {
            FileSystemUtils.deleteRecursively(new File("db"));
        }

    }

}

