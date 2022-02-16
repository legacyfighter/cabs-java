package io.legacyfighter.cabs.contracts.application.editor;

import io.legacyfighter.cabs.contracts.model.content.ContentVersion;

import java.util.UUID;

public class DocumentDTO {
    private final UUID contentId;
    private final String physicalContent;
    private final ContentVersion contentVersion;

    public DocumentDTO(UUID contentId, String physicalContent, ContentVersion contentVersion) {
        this.contentId = contentId;
        this.physicalContent = physicalContent;
        this.contentVersion = contentVersion;
    }

    public UUID getContentId() {
        return contentId;
    }

    public ContentVersion getDocumentVersion() {
        return contentVersion;
    }

    public String getPhysicalContent() {
        return physicalContent;
    }
}
