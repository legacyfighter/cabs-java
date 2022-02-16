package io.legacyfighter.cabs.contracts.model.state.dynamic;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.contentchange.PositivePredicate;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange.PreviousStateVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class StateBuilder implements StateConfig{

    //last step of the Builder - because it is special
    public static class FinalStateConfig{
        private final State state;

        FinalStateConfig(State state){
            this.state = state;
        }

        /**
         * Adds an operation to be performed if state have changed
         */
        public FinalStateConfig action(BiFunction<DocumentHeader, ChangeCommand, Void> action) {
            state.addAfterStateChangeAction(action);
            return this;
        }
    }

    /**
     * This {@link StateBuilder} state, that depends on method call
     */
    private enum Mode{
        /**
         * Rules for state transition {@link #check(BiFunction) check}  method called or {@link #from(String) from}  method called
         */
        STATE_CHANGE,
        /**
         * Rules for content change {@link #whenContentChanged() whenContentChanged}  method called
         */
        CONTENT_CHANGE
    }
    private Mode mode;

    //all states configured so far
    private Map<String, State> states = new HashMap<>();

    //below is the current state of the builder, gathered whit assembling methods, current state is reset in to() method
    private State fromState;
    private State initialState;
    private List<BiFunction<State, ChangeCommand, Boolean>> predicates;

    //========= methods for application layer - business process

    @Override
    public State begin(DocumentHeader header) {
        header.setStateDescriptor(initialState.getStateDescriptor());
        return recreate(header);
    }

    @Override
    public State recreate(DocumentHeader header) {
        State state = states.get(header.getStateDescriptor());
        state.init(header);
        return state;
    }

    //======= methods for assembling process

    /**
     * Similar to the {@link #from(String)} from} method, but marks initial state
     */
    public StateBuilder beginWith(String stateName) {
        if (initialState != null)
            throw new IllegalStateException("Initial state already set to: " + initialState.getStateDescriptor());

        StateBuilder config = from(stateName);
        initialState = fromState;
        return config;
    }

    /**
     * Begins a rule sequence with a beginning state
     */
    public StateBuilder from(String stateName) {
        mode = Mode.STATE_CHANGE;
        predicates = new ArrayList<>();
        fromState = getOrPut(stateName);
        return this;
    }

    /**
     * Adds a rule to the current sequence
     */
    public StateBuilder check(BiFunction<State, ChangeCommand, Boolean> checkingFunction) {
        mode = Mode.STATE_CHANGE;
        predicates.add(checkingFunction);
        return this;
    }

    /**
     * Ends a rule sequence with a destination state
     */
    public FinalStateConfig to(String stateName) {
        State toState = getOrPut(stateName);

        switch (mode){
            case STATE_CHANGE:
                predicates.add(new PreviousStateVerifier(fromState.getStateDescriptor()));
                fromState.addStateChangePredicates(toState, predicates);
                break;
            case CONTENT_CHANGE:
                fromState.setAfterContentChangeState(toState);
                toState.setContentChangePredicate(new PositivePredicate());
        }

        predicates = null;
        fromState = null;
        mode = null;

        return new FinalStateConfig(toState);
    }

    /**
     * Adds a rule of state change after a content change
     */
    public StateBuilder whenContentChanged() {
        mode = Mode.CONTENT_CHANGE;
        return this;
    }

    private State getOrPut(String stateName) {
        if (!states.containsKey(stateName))
            states.put(stateName, new State(stateName));
        return states.get(stateName);
    }
}
