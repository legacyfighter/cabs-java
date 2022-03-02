package io.legacyfighter.cabs.transitanalyzer;


import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.transaction.annotation.Transactional;


import static io.legacyfighter.cabs.entity.Transit.Status.COMPLETED;

public class PopulateGraphService {

    private final TransitRepository transitRepository;
    private final GraphTransitAnalyzer graphTransitAnalyzer;

    public PopulateGraphService(TransitRepository transitRepository, GraphTransitAnalyzer graphTransitAnalyzer) {
        this.transitRepository = transitRepository;
        this.graphTransitAnalyzer = graphTransitAnalyzer;
    }

    @Transactional
    public void populate() {
        transitRepository
                .findAllByStatus(COMPLETED)
                .forEach(this::addToGraph);
    }

    private void addToGraph(Transit transit) {
        Long clientId = transit.getClient().getId();
        graphTransitAnalyzer.addTransitBetweenAddresses(
                clientId,
                transit.getId(),
                transit.getFrom().getHash(),
                transit.getTo().getHash(),
                transit.getStarted(),
                transit.getCompleteAt());
    }
}
