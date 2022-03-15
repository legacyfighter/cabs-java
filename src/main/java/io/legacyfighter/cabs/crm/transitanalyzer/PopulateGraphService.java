package io.legacyfighter.cabs.crm.transitanalyzer;


import io.legacyfighter.cabs.ride.details.TransitDetailsDTO;
import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import org.springframework.transaction.annotation.Transactional;

public class PopulateGraphService {

    private final GraphTransitAnalyzer graphTransitAnalyzer;
    private final TransitDetailsFacade transitDetailsFacade;

    public PopulateGraphService(GraphTransitAnalyzer graphTransitAnalyzer, TransitDetailsFacade transitDetailsFacade) {
        this.graphTransitAnalyzer = graphTransitAnalyzer;
        this.transitDetailsFacade = transitDetailsFacade;
    }

    @Transactional
    public void populate() {
        transitDetailsFacade
                .findCompleted()
                .forEach(this::addToGraph);
    }

    private void addToGraph(TransitDetailsDTO transitDetails) {
        Long clientId = transitDetails.client.getId();
        graphTransitAnalyzer.addTransitBetweenAddresses(
                clientId,
                transitDetails.transitId,
                transitDetails.from.getHash(),
                transitDetails.to.getHash(),
                transitDetails.started,
                transitDetails.completedAt);
    }
}
