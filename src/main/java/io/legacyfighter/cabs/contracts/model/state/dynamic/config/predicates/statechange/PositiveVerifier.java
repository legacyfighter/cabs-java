package io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange;

import io.legacyfighter.cabs.contracts.model.state.dynamic.ChangeCommand;
import io.legacyfighter.cabs.contracts.model.state.dynamic.State;

import java.util.function.BiFunction;

public class PositiveVerifier implements BiFunction<State, ChangeCommand, Boolean> {

    @Override
    public Boolean apply(State state, ChangeCommand command) {
        return true;
    }
}
