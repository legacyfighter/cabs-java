package io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange;

import io.legacyfighter.cabs.contracts.model.state.dynamic.ChangeCommand;
import io.legacyfighter.cabs.contracts.model.state.dynamic.State;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.actions.ChangeVerifier;

import java.util.function.BiFunction;

public class AuthorIsNotAVerifier implements BiFunction<State, ChangeCommand, Boolean> {

    public static final String PARAM_VERIFIER = ChangeVerifier.PARAM_VERIFIER;

    @Override
    public Boolean apply(State state, ChangeCommand command) {
        return ! command.getParam(PARAM_VERIFIER, Long.class).equals(state.getDocumentHeader().getAuthorId());
    }
}
