package io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange;

import io.legacyfighter.cabs.contracts.model.state.dynamic.ChangeCommand;
import io.legacyfighter.cabs.contracts.model.state.dynamic.State;

import java.util.function.BiFunction;

public class PreviousStateVerifier implements BiFunction<State, ChangeCommand, Boolean> {
    private final String stateDescriptor;

    public PreviousStateVerifier(String stateDescriptor) {
        this.stateDescriptor = stateDescriptor;
    }

    @Override
    public Boolean apply(State state, ChangeCommand command) {
        return state.getStateDescriptor().equals(stateDescriptor);
    }
}
