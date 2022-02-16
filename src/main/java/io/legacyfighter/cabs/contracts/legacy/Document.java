package io.legacyfighter.cabs.contracts.legacy;


import javax.persistence.*;
import java.util.Set;

@Entity
public class Document extends BaseAggregateRoot implements Printable {
    private String number;
    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    protected DocumentStatus status = DocumentStatus.DRAFT;

    @ManyToMany
    private Set<User> assignedUsers;

    @ManyToOne
    private User creator;
    @ManyToOne
    private User verifier;

    public Document(String number, User creator){
        this.number = number;
        this.creator = creator;
    }

    protected Document() {}

    public void verifyBy(User verifier){
        if (status != DocumentStatus.DRAFT)
            throw new IllegalStateException("Can not verify in status: " + status);
        if (creator.equals(verifier))
            throw new IllegalArgumentException("Verifier can not verify documents by himself");
        this.verifier = verifier;
        status = DocumentStatus.VERIFIED;
    }

    public void publish() throws UnsupportedTransitionException {//code open for modifications: throws is for super classes
        if (status != DocumentStatus.VERIFIED)
            throw new IllegalStateException("Can not publish in status: " + status);
        status = DocumentStatus.PUBLISHED;
    }

    public void archive(){
        status = DocumentStatus.ARCHIVED;
    }

    //===============================================================

    public void changeTitle(String title){
        if (status == DocumentStatus.ARCHIVED || status == DocumentStatus.PUBLISHED)
            throw new IllegalStateException("Can not change title in status: " + status);
        this.title = title;
        if (status == DocumentStatus.VERIFIED)
            status = DocumentStatus.DRAFT;
    }

    protected boolean overridePublished;

    public void changeContent(String content){
        if (overridePublished){
            this.content = content;
            return;
        }

        if (status == DocumentStatus.ARCHIVED || status == DocumentStatus.PUBLISHED)
            throw new IllegalStateException("Can not change content in status: " + status);
        this.content = content;
        if (status == DocumentStatus.VERIFIED)
            status = DocumentStatus.DRAFT;
    }

    //===============================================================

    protected String getContent() {
        return content;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
