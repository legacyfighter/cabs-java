package io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.contentchange;

import io.legacyfighter.cabs.contracts.model.state.dynamic.State;

import java.util.function.Predicate;

public class NegativePredicate implements Predicate<State> {
    @Override
    public boolean test(State state) {
        return false;
    }
}
