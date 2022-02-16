package io.legacyfighter.cabs.contracts.model.state.dynamic.config.events;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;

public class DocumentUnpublished extends DocumentEvent{
    public DocumentUnpublished(Long documentId, String currentSate, ContentId contentId, DocumentNumber number) {
        super(documentId, currentSate, contentId, number);
    }
}
