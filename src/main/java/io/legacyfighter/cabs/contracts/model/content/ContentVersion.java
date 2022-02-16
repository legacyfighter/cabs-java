package io.legacyfighter.cabs.contracts.model.content;

import javax.persistence.Embeddable;

@Embeddable
public class ContentVersion {
    private String contentVersion;

    protected ContentVersion(){}

    public ContentVersion(String contentVersion){
        this.contentVersion = contentVersion;
    }
}
