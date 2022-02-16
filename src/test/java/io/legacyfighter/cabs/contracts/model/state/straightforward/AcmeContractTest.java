package io.legacyfighter.cabs.contracts.model.state.straightforward;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import io.legacyfighter.cabs.contracts.model.state.straightforward.acme.DraftState;
import io.legacyfighter.cabs.contracts.model.state.straightforward.acme.PublishedState;
import io.legacyfighter.cabs.contracts.model.state.straightforward.acme.VerifiedState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AcmeContractTest {

    private static final DocumentNumber ANY_NUMBER = new DocumentNumber("nr: 1");
    private static final Long ANY_USER = 1L;
    private static final Long OTHER_USER = 2L;
    private static final ContentId ANY_VERSION = new ContentId(UUID.randomUUID());
    private static final ContentId OTHER_VERSION = new ContentId(UUID.randomUUID());

    private BaseState state;

    @Test
    public void onlyDraftCanBeVerifiedByUserOtherThanCreator(){
        //given
        state = draft().changeContent(ANY_VERSION);
        //when
        state = state.changeState(new VerifiedState(OTHER_USER));
        //then
        assertEquals(VerifiedState.class, state.getClass());
        assertEquals(OTHER_USER, state.getDocumentHeader().getVerifier());
    }

    @Test
    public void canNotChangePublished(){
        //given
        state = draft().changeContent(ANY_VERSION).changeState(new VerifiedState(OTHER_USER)).changeState(new PublishedState());
        //when
        state = state.changeContent(OTHER_VERSION);
        //then
        assertEquals(PublishedState.class, state.getClass());
        assertEquals(ANY_VERSION, state.getDocumentHeader().getContentId());
    }

    @Test
    public void changingVerifiedMovesToDraft(){
        //given
        state = draft().changeContent(ANY_VERSION);
        //when
        state = state.changeState(new VerifiedState(OTHER_USER)).changeContent(OTHER_VERSION);
        //then
        assertEquals(DraftState.class, state.getClass());
        assertEquals(OTHER_VERSION, state.getDocumentHeader().getContentId());
    }

    private BaseState draft(){
        DocumentHeader header = new DocumentHeader(ANY_USER, ANY_NUMBER);

        BaseState state = new DraftState();
        state.init(header);

        return state;
    }
}
