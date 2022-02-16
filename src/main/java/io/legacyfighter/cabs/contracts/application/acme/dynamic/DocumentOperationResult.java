package io.legacyfighter.cabs.contracts.application.acme.dynamic;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;

import java.util.List;
import java.util.Map;

public class DocumentOperationResult {

    public enum Result{
        SUCCESS, ERROR
    }

    private Result result;
    private String stateName;
    private ContentId contentId;

    private Long documentHeaderId;
    private DocumentNumber documentNumber;

    private Map<String, List<String>> possibleTransitionsAndRules;
    private boolean contentChangePossible;
    private String contentChangePredicate;


    public DocumentOperationResult(Result result, Long documentHeaderId, DocumentNumber documentNumber, String stateName, ContentId contentId, Map<String, List<String>> possibleTransitionsAndRules, boolean contentChangePossible, String contentChangePredicate) {
        this.result = result;
        this.documentHeaderId = documentHeaderId;
        this.documentNumber = documentNumber;
        this.stateName = stateName;
        this.contentId = contentId;
        this.possibleTransitionsAndRules = possibleTransitionsAndRules;
        this.contentChangePossible = contentChangePossible;
        this.contentChangePredicate = contentChangePredicate;
    }

    public Map<String, List<String>> getPossibleTransitionsAndRules() {
        return possibleTransitionsAndRules;
    }

    public String getContentChangePredicate() {
        return contentChangePredicate;
    }

    public boolean isContentChangePossible() {
        return contentChangePossible;
    }

    public Result getResult() {
        return result;
    }

    public String getStateName() {
        return stateName;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public Long getDocumentHeaderId() {
        return documentHeaderId;
    }

    public ContentId getContentId() {
        return contentId;
    }
}
