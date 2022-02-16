package io.legacyfighter.cabs.contracts.model.state.dynamic;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.contentchange.NegativePredicate;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange.PositiveVerifier;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class State {
    //before: getClass().getName()
    /**
     * Unique name of a state
     */
    private final String stateDescriptor;

    //TODO consider to get rid of this stateful object and transform State to reusable logic
    private DocumentHeader documentHeader;


    //TODO consider merging contentChangePredicate and afterContentChangeState int one function

    //before: abstract canChangeContent()
    /**
     * predicates tested if content can be changed
     */
    private Predicate<State> contentChangePredicate = new NegativePredicate();  //default

    //before: abstract stateAfterContentChange()
    /**
     * state after content change - may be the same as before content change
     */
    private State afterContentChangeState;

    //before: abstract canChangeFrom(state)
    /**
     * possible transitions to other states with rules that need to be tested to determine if transition is legal
     */
    private final Map<State, List<BiFunction<State, ChangeCommand, Boolean>>> stateChangePredicates = new HashMap<>();

    //before: abstract acquire()
    /**
     * actions that may be needed to perform while transition to the next state
     */
    private final List<BiFunction<DocumentHeader, ChangeCommand, Void>> afterStateChangeActions = new ArrayList<>();

    public State(String stateDescriptor){
        this.stateDescriptor = stateDescriptor;
        addStateChangePredicates(this, List.of(new PositiveVerifier()));//change to self is always possible
    }

    /**
     * initial bounding with a document header
     */
    public void init(DocumentHeader documentHeader){
        this.documentHeader = documentHeader;
        documentHeader.setStateDescriptor(getStateDescriptor());
    }

    public State changeContent(ContentId currentContent){
        if (!isContentEditable())
            return this;

        State newState = afterContentChangeState;//local variable just to focus attention
        if (newState.contentChangePredicate.test(this)){
            newState.init(documentHeader);
            this.documentHeader.changeCurrentContent(currentContent);
            return newState;
        }

        return this;
    }


    public State changeState(ChangeCommand command){
        State desiredState = find(command.getDesiredState());
        if (desiredState == null)
            return this;

        List<BiFunction<State, ChangeCommand, Boolean>> predicates = stateChangePredicates.getOrDefault(desiredState, Collections.emptyList());

        if (predicates.stream().allMatch(e -> e.apply(this, command))) {
            desiredState.init(documentHeader);
            desiredState.afterStateChangeActions.forEach(e -> e.apply(documentHeader, command));
            return desiredState;
        }

        return this;
    }

    public String getStateDescriptor(){
        return stateDescriptor;
    }

    public DocumentHeader getDocumentHeader(){
        return documentHeader;
    }

    public Map<State, List<BiFunction<State, ChangeCommand, Boolean>>> getStateChangePredicates() {
        return stateChangePredicates;
    }

    public Predicate<State> getContentChangePredicate() {
        return contentChangePredicate;
    }

    public boolean isContentEditable(){
        return afterContentChangeState != null;
    }

    @Override
    public String toString() {
        return "State{" +
                "stateDescriptor='" + stateDescriptor + '\'' +
                '}';
    }

    void addStateChangePredicates(State toState, List<BiFunction<State, ChangeCommand, Boolean>> predicatesToAdd) {
        if (stateChangePredicates.containsKey(toState)) {
            List<BiFunction<State, ChangeCommand, Boolean>> predicates = stateChangePredicates.get(toState);
            predicates.addAll(predicatesToAdd);
        }
        else {
            stateChangePredicates.put(toState, predicatesToAdd);
        }
    }

    void addAfterStateChangeAction(BiFunction<DocumentHeader, ChangeCommand, Void> action) {
        afterStateChangeActions.add(action);
    }

    void setAfterContentChangeState(State toState) {
        afterContentChangeState = toState;
    }

    void setContentChangePredicate(Predicate<State> predicate) {
        contentChangePredicate = predicate;
    }

    private State find(String desiredState) {
        return stateChangePredicates.keySet().stream().filter(e -> e.getStateDescriptor().equals(desiredState)).findFirst().orElse(null);
    }
}
