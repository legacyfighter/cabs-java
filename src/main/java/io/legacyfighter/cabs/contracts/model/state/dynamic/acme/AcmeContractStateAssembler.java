package io.legacyfighter.cabs.contracts.model.state.dynamic.acme;

import io.legacyfighter.cabs.contracts.model.state.dynamic.StateBuilder;
import io.legacyfighter.cabs.contracts.model.state.dynamic.StateConfig;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.actions.ChangeVerifier;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.actions.PublishEvent;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.events.DocumentPublished;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.events.DocumentUnpublished;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange.AuthorIsNotAVerifier;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange.ContentNotEmptyVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Sample static config.
 */
@Component
public class AcmeContractStateAssembler {

    public static final String VERIFIED  = "verified";
    public static final String DRAFT     = "draft";
    public static final String PUBLISHED = "published";
    public static final String ARCHIVED  = "archived";

    public static final String PARAM_VERIFIER = ChangeVerifier.PARAM_VERIFIER;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public AcmeContractStateAssembler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public StateConfig assemble(){
        StateBuilder builder = new StateBuilder();
        builder.beginWith(DRAFT).check(new ContentNotEmptyVerifier()).check(new AuthorIsNotAVerifier()).to(VERIFIED).action(new ChangeVerifier());
        builder.from(DRAFT).whenContentChanged().to(DRAFT);
        //name of the "published" state and name of the DocumentPublished event are NOT correlated. These are two different domains, name similarity is just a coincidence
        builder.from(VERIFIED).check(new ContentNotEmptyVerifier()).to(PUBLISHED).action(new PublishEvent(DocumentPublished.class, publisher));
        builder.from(VERIFIED).whenContentChanged().to(DRAFT);
        builder.from(DRAFT).to(ARCHIVED);
        builder.from(VERIFIED).to(ARCHIVED);
        builder.from(PUBLISHED).to(ARCHIVED).action(new PublishEvent(DocumentUnpublished.class, publisher));
        return builder;
    }
}
