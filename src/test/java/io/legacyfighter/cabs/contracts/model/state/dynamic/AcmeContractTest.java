package io.legacyfighter.cabs.contracts.model.state.dynamic;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import io.legacyfighter.cabs.contracts.model.state.dynamic.acme.AcmeContractStateAssembler;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.events.DocumentPublished;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AcmeContractTest {

    private static final DocumentNumber ANY_NUMBER = new DocumentNumber("nr: 1");
    private static final Long ANY_USER = 1L;
    private static final Long OTHER_USER = 2L;
    private static final ContentId ANY_VERSION = new ContentId(UUID.randomUUID());
    private static final ContentId OTHER_VERSION = new ContentId(UUID.randomUUID());

    private FakeDocumentPublisher publisher;

    State draft(){
        DocumentHeader header = new DocumentHeader(ANY_USER, ANY_NUMBER);
        header.setStateDescriptor(AcmeContractStateAssembler.DRAFT);
        publisher = new FakeDocumentPublisher();

        AcmeContractStateAssembler assembler = new AcmeContractStateAssembler(publisher);
        StateConfig config = assembler.assemble();
        State state = config.recreate(header);

        return state;
    }

    @Test
    public void draftCanBeVerifiedByUserOtherThanCreator(){
        //given
        State state = draft().changeContent(ANY_VERSION);
        //when
        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.VERIFIED).withParam(AcmeContractStateAssembler.PARAM_VERIFIER, OTHER_USER));
        //then
        assertEquals(AcmeContractStateAssembler.VERIFIED, state.getStateDescriptor());
        assertEquals(OTHER_USER, state.getDocumentHeader().getVerifier());
    }

    @Test
    public void canNotChangePublished(){
        //given
        State state = state = draft().changeContent(ANY_VERSION)
                .changeState(new ChangeCommand(AcmeContractStateAssembler.VERIFIED).withParam(AcmeContractStateAssembler.PARAM_VERIFIER, OTHER_USER))
                .changeState(new ChangeCommand(AcmeContractStateAssembler.PUBLISHED));

        publisher.contains(DocumentPublished.class);
        publisher.reset();
        //when
        state = state.changeContent(OTHER_VERSION);
        //then
        publisher.noEvents();
        assertEquals(AcmeContractStateAssembler.PUBLISHED, state.getStateDescriptor());
        assertEquals(ANY_VERSION, state.getDocumentHeader().getContentId());
    }

    @Test
    public void changingVerifiedMovesToDraft(){
        //given
        State state = draft().changeContent(ANY_VERSION)
                .changeState(new ChangeCommand(AcmeContractStateAssembler.VERIFIED).withParam(AcmeContractStateAssembler.PARAM_VERIFIER, OTHER_USER));
        //when
        state = state.changeContent(OTHER_VERSION);
        //then
        assertEquals(AcmeContractStateAssembler.DRAFT, state.getStateDescriptor());
        assertEquals(OTHER_VERSION, state.getDocumentHeader().getContentId());
    }


    @Test
    public void canChangeStateToTheSame(){
        State state = draft().changeContent(ANY_VERSION);
        assertEquals(AcmeContractStateAssembler.DRAFT, state.getStateDescriptor());
        state.changeState(new ChangeCommand(AcmeContractStateAssembler.DRAFT));
        assertEquals(AcmeContractStateAssembler.DRAFT, state.getStateDescriptor());

        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.VERIFIED).withParam(AcmeContractStateAssembler.PARAM_VERIFIER, OTHER_USER));
        assertEquals(AcmeContractStateAssembler.VERIFIED, state.getStateDescriptor());
        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.VERIFIED).withParam(AcmeContractStateAssembler.PARAM_VERIFIER, OTHER_USER));
        assertEquals(AcmeContractStateAssembler.VERIFIED, state.getStateDescriptor());

        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.PUBLISHED));
        assertEquals(AcmeContractStateAssembler.PUBLISHED, state.getStateDescriptor());
        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.PUBLISHED));
        assertEquals(AcmeContractStateAssembler.PUBLISHED, state.getStateDescriptor());

        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.ARCHIVED));
        assertEquals(AcmeContractStateAssembler.ARCHIVED, state.getStateDescriptor());
        state = state.changeState(new ChangeCommand(AcmeContractStateAssembler.ARCHIVED));
        assertEquals(AcmeContractStateAssembler.ARCHIVED, state.getStateDescriptor());
    }
}
