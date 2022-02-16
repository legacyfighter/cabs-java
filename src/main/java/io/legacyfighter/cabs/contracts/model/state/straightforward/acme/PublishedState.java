package io.legacyfighter.cabs.contracts.model.state.straightforward.acme;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.straightforward.BaseState;

public class PublishedState extends BaseState {

    @Override
    protected boolean canChangeContent() {
        return false;
    }

    @Override
    protected BaseState stateAfterContentChange() {
        return this;
    }

    @Override
    protected boolean canChangeFrom(BaseState previousState) {
        return previousState instanceof VerifiedState
                && previousState.getDocumentHeader().notEmpty();
    }

    @Override
    protected void acquire(DocumentHeader documentHeader) {

    }
}
