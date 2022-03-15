package io.legacyfighter.cabs.crm.transitanalyzer;


import io.legacyfighter.cabs.ride.events.TransitCompleted;
import org.neo4j.graphdb.*;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


public class GraphTransitAnalyzer {

    private final GraphDatabaseService graphDb;

    public GraphTransitAnalyzer(GraphDatabaseService graphDatabaseService) {
        this.graphDb = graphDatabaseService;
    }

    public List<Long> analyze(Long clientId, Integer addressHash) {
        try (Transaction t = graphDb.beginTx()) {
            Result result = graphDb.execute("MATCH p=(a:Address)-[:Transit*]->(:Address) " +
                    "WHERE a.hash = " + addressHash + " " +
                    "AND (ALL(x IN range(1, length(p)-1) WHERE ((relationships(p)[x]).clientId = " + clientId + ") AND 0 <= duration.inSeconds( (relationships(p)[x-1]).completeAt, (relationships(p)[x]).started).minutes <= 15)) " +
                    "AND length(p) >= 1 " +
                    "RETURN [x in nodes(p) | x.hash] AS hashes ORDER BY length(p) DESC LIMIT 1");

            t.success();

            return ((List<Long>) result.next().get("hashes")).stream().collect(Collectors.toList());
        }
    }

    public void addTransitBetweenAddresses(Long clientId, Long transitId, Integer addressFromHash, Integer addressToHash, Instant started, Instant completeAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());

        try (Transaction t = graphDb.beginTx()) {
            graphDb.execute("MERGE (from:Address {hash: " + addressFromHash + "})");
            graphDb.execute("MERGE (to:Address {hash: " + addressToHash + "})");
            graphDb.execute("MATCH (from:Address {hash: " + addressFromHash + "}), (to:Address {hash: " + addressToHash + "}) " +
                    "CREATE (from)-[:Transit {clientId: " + clientId + ", transitId: " + transitId + ", " +
                    "started: datetime(\"" + formatter.format(started) + "\"), completeAt: datetime(\"" + formatter.format(completeAt) + "\") }]->(to)");

            t.success();
        }
    }

    @TransactionalEventListener
    public void handle(TransitCompleted transitCompleted) {
        addTransitBetweenAddresses(transitCompleted.clientId, transitCompleted.transitId, transitCompleted.addressFromHash, transitCompleted.addressToHash, transitCompleted.started, transitCompleted.completeAt);
    }

    public void onClose() {
        if (graphDb != null) {
            graphDb.shutdown();
        }
    }

}
