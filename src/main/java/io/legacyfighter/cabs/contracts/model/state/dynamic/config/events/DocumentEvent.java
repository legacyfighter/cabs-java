package io.legacyfighter.cabs.contracts.model.state.dynamic.config.events;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import org.springframework.context.ApplicationEvent;

public abstract class DocumentEvent extends ApplicationEvent {
    private final Long documentId;
    private final String currentSate;
    private final ContentId contentId;
    private final DocumentNumber number;

    public DocumentEvent(Long documentId, String currentSate, ContentId contentId, DocumentNumber number) {
        super(number);
        this.documentId = documentId;
        this.currentSate = currentSate;
        this.contentId = contentId;
        this.number = number;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getCurrentSate() {
        return currentSate;
    }

    public ContentId getContentId() {
        return contentId;
    }

    public DocumentNumber getNumber() {
        return number;
    }
}
