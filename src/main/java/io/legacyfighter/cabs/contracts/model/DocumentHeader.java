package io.legacyfighter.cabs.contracts.model;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;

import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
public class DocumentHeader extends BaseEntity {
    @Embedded
    private DocumentNumber number;

    private Long authorId;

    private Long verifierId;

    private String stateDescriptor;

    @Embedded
    private ContentId contentId;

    protected DocumentHeader(){

    }

    public DocumentHeader(Long authorId, DocumentNumber number){
        this.authorId = authorId;
        this.number = number;
    }

    public void changeCurrentContent(ContentId contentId){
        this.contentId = contentId;
    }

    public boolean notEmpty() {
        return contentId != null;
    }


    public Long getVerifier() {
        return verifierId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setVerifierId(Long verifierId) {
        this.verifierId = verifierId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getStateDescriptor() {
        return stateDescriptor;
    }

    public void setStateDescriptor(String stateDescriptor) {
        this.stateDescriptor = stateDescriptor;
    }

    public DocumentNumber getDocumentNumber() {
        return number;
    }

    public ContentId getContentId() {
        return contentId;
    }


}
