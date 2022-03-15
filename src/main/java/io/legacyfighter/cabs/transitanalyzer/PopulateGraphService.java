package io.legacyfighter.cabs.transitanalyzer;


import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.transitdetails.TransitDetailsDTO;
import io.legacyfighter.cabs.transitdetails.TransitDetailsFacade;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.transaction.annotation.Transactional;


import static io.legacyfighter.cabs.entity.Transit.Status.COMPLETED;

public class PopulateGraphService {

    private final TransitRepository transitRepository;
    private final GraphTransitAnalyzer graphTransitAnalyzer;
    private final TransitDetailsFacade transitDetailsFacade;

    public PopulateGraphService(TransitRepository transitRepository, GraphTransitAnalyzer graphTransitAnalyzer, TransitDetailsFacade transitDetailsFacade) {
        this.transitRepository = transitRepository;
        this.graphTransitAnalyzer = graphTransitAnalyzer;
        this.transitDetailsFacade = transitDetailsFacade;
    }

    @Transactional
    public void populate() {
        transitRepository
                .findAllByStatus(COMPLETED)
                .forEach(this::addToGraph);
    }

    private void addToGraph(Transit transit) {
        TransitDetailsDTO transitDetails = transitDetailsFacade.find(transit.getId());
        Long clientId = transitDetails.client.getId();
        graphTransitAnalyzer.addTransitBetweenAddresses(
                clientId,
                transit.getId(),
                transitDetails.from.getHash(),
                transitDetails.to.getHash(),
                transitDetails.started,
                transitDetails.completedAt);
    }
}
