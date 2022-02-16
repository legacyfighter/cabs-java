package io.legacyfighter.cabs.contracts.model.state.straightforward.acme;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.straightforward.BaseState;

public class VerifiedState extends BaseState {
    private Long verifierId;

    public VerifiedState(Long verifierId) {
        this.verifierId = verifierId;
    }

    public VerifiedState(){

    }


    @Override
    protected boolean canChangeContent() {
        return true;
    }

    @Override
    protected BaseState stateAfterContentChange() {
        return new DraftState();
    }

    @Override
    protected boolean canChangeFrom(BaseState previousState) {
        return previousState instanceof DraftState
                && !previousState.getDocumentHeader().getAuthorId().equals(verifierId)
                && previousState.getDocumentHeader().notEmpty();
    }

    @Override
    protected void acquire(DocumentHeader documentHeader) {
        documentHeader.setVerifierId(verifierId);
    }

}
