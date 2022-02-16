package io.legacyfighter.cabs.contracts.application.dynamic;

import io.legacyfighter.cabs.contracts.application.acme.dynamic.DocumentOperationResult;
import io.legacyfighter.cabs.contracts.model.ContentId;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class DocumentOperationResultAssert {
    private DocumentOperationResult result;

    public DocumentOperationResultAssert(DocumentOperationResult result){
        this.result = result;
        assertEquals(DocumentOperationResult.Result.SUCCESS, result.getResult());
    }

    public DocumentOperationResultAssert editable(){
        assertTrue(result.isContentChangePossible());
        return this;
    }

    public DocumentOperationResultAssert uneditable(){
        assertFalse(result.isContentChangePossible());
        return this;
    }

    public DocumentOperationResultAssert state(String state){
        assertEquals(state, result.getStateName());
        return this;
    }

    public DocumentOperationResultAssert content(ContentId contentId) {
        assertEquals(contentId, result.getContentId());
        return this;
    }

    public DocumentOperationResultAssert possibleNextStates(String ... states){
        assertEquals(Set.of(states),
            result.getPossibleTransitionsAndRules().keySet());
        return this;
    }
}
