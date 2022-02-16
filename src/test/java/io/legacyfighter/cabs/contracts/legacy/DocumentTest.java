package io.legacyfighter.cabs.contracts.legacy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DocumentTest {

    private static final String ANY_NUMBER = "number";
    private static final User ANY_USER = new User();
    private static final User OTHER_USER = new User();
    private static final String TITLE = "title";

    @Test
    public void onlyDraftCanBeVerifiedByUserOtherThanCreator(){
        Document doc = new Document(ANY_NUMBER, ANY_USER);

        doc.verifyBy(OTHER_USER);

        assertEquals(DocumentStatus.VERIFIED, doc.getStatus());
    }

    @Test
    public void canNotChangePublished(){
        Document doc = new Document(ANY_NUMBER, ANY_USER);
        doc.changeTitle(TITLE);
        doc.verifyBy(OTHER_USER);
        doc.publish();

        try {
            doc.changeTitle("");
        }
        catch (IllegalStateException ex){
            assertTrue(true);
        }
        assertEquals(TITLE, doc.getTitle());
    }

    @Test
    public void changingVerifiedMovesToDraft(){
        Document doc = new Document(ANY_NUMBER, ANY_USER);
        doc.changeTitle(TITLE);
        doc.verifyBy(OTHER_USER);

        doc.changeTitle("");

        assertEquals(DocumentStatus.DRAFT, doc.getStatus());
    }
}
