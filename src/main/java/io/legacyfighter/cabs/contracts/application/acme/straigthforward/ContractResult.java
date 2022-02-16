package io.legacyfighter.cabs.contracts.application.acme.straigthforward;

import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;

public class ContractResult {

    public enum Result{
        FAILURE, SUCCESS
    }

    private Result result;
    private Long documentHeaderId;
    private DocumentNumber documentNumber;
    private String stateDescriptor;

    public ContractResult(Result result, Long documentHeaderId, DocumentNumber documentNumber, String stateDescriptor) {
        this.result = result;
        this.documentHeaderId = documentHeaderId;
        this.documentNumber = documentNumber;
        this.stateDescriptor = stateDescriptor;
    }

    public Result getResult() {
        return result;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public Long getDocumentHeaderId() {
        return documentHeaderId;
    }

    public String getStateDescriptor() {
        return stateDescriptor;
    }
}
