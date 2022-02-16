package io.legacyfighter.cabs.contracts.model.state.straightforward.acme;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.straightforward.BaseState;

public class DraftState extends BaseState {

    //BAD IDEA!
    //public BaseState publish(){
    //if some validation
    //    return new PublishedState();
    //}

    @Override
    protected boolean canChangeContent() {
        return true;
    }

    @Override
    protected BaseState stateAfterContentChange() {
        return this;
    }

    @Override
    protected boolean canChangeFrom(BaseState previousState) {
        return true;
    }

    @Override
    protected void acquire(DocumentHeader documentHeader) {

    }

}
