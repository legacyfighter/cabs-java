package io.legacyfighter.cabs.contracts.model;

import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ContentId {
    private UUID contentId;

    protected ContentId(){}

    public ContentId(UUID contentId){
        this.contentId = contentId;
    }

    @Override
    public String toString() {
        return "ContentId{" +
                "contentId=" + contentId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentId contentId1 = (ContentId) o;
        return contentId.equals(contentId1.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId);
    }
}
