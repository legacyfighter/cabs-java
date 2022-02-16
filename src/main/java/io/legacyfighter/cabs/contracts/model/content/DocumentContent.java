package io.legacyfighter.cabs.contracts.model.content;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class DocumentContent {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID previousId;

    private String physicalContent; //some kind of reference to file, version control. In sour sample i will be a blob stored in DB:)

    @Embedded
    private ContentVersion contentVersion;//just a human readable descriptor

    protected DocumentContent() {

    }

    public DocumentContent(UUID previousId, ContentVersion contentVersion, String physicalContent) {
        this.previousId = previousId;
        this.contentVersion = contentVersion;
        this.physicalContent = physicalContent;
    }

    public DocumentContent(ContentVersion version, String physicalContent) {
        this(null, version, physicalContent);
    }

    public UUID getId() {
        return id;
    }

    public String getPhysicalContent() {
        return physicalContent;
    }

    public ContentVersion getDocumentVersion() {
        return contentVersion;
    }
}