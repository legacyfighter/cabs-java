package io.legacyfighter.cabs.contracts.model.state.dynamic.config.events;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;

public class DocumentPublished extends DocumentEvent{
    public DocumentPublished(Long documentId, String currentSate, ContentId contentId, DocumentNumber number) {
        super(documentId, currentSate, contentId, number);
    }
}
