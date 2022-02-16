package io.legacyfighter.cabs.contracts.model.state.dynamic.config.changes;

import io.legacyfighter.cabs.contracts.model.state.dynamic.State;

import java.util.function.Function;

public class FixedState implements Function<State, State> {
    private String stateName;
    public FixedState(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public State apply(State state) {
        return new State(stateName);
    }
}
