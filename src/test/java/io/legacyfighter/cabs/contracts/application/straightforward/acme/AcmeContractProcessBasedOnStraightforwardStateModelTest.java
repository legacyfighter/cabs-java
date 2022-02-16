package io.legacyfighter.cabs.contracts.application.straightforward.acme;

import io.legacyfighter.cabs.contracts.application.acme.straigthforward.AcmeContractProcessBasedOnStraightforwardDocumentModel;
import io.legacyfighter.cabs.contracts.application.acme.straigthforward.ContractResult;
import io.legacyfighter.cabs.contracts.application.editor.CommitResult;
import io.legacyfighter.cabs.contracts.application.editor.DocumentDTO;
import io.legacyfighter.cabs.contracts.application.editor.DocumentEditor;
import io.legacyfighter.cabs.contracts.legacy.User;
import io.legacyfighter.cabs.contracts.legacy.UserRepository;
import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.content.ContentVersion;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import io.legacyfighter.cabs.contracts.model.state.straightforward.acme.DraftState;
import io.legacyfighter.cabs.contracts.model.state.straightforward.acme.VerifiedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AcmeContractProcessBasedOnStraightforwardStateModelTest {

    @Autowired
    DocumentEditor editor;
    @Autowired
    AcmeContractProcessBasedOnStraightforwardDocumentModel contractProcess;
    @Autowired
    UserRepository userRepository;

    private static final String CONTENT_1 = "content 1";
    private static final String CONTENT_2 = "content 2";
    private static final ContentVersion ANY_VERSION = new ContentVersion("v1");

    private User author;
    private User verifier;

    DocumentNumber documentNumber;
    Long headerId;

    @BeforeEach
    public void prepare(){
        author = userRepository.save(new User());
        verifier = userRepository.save(new User());
    }

    @Test
    public void verifierOtherThanAuthorCanVerify(){
        //given
        crateAcmeContract(author);
        ContentId contentId = commitContent(CONTENT_1);
        contractProcess.changeContent(headerId, contentId);
        //when
        ContractResult result = contractProcess.verify(headerId, verifier.getId());
        //then
        new ContractResultAssert(result).state(new VerifiedState(verifier.getId()));
    }

    @Test
    public void authorCanNotVerify(){
        //given
        crateAcmeContract(author);
        ContentId contentId = commitContent(CONTENT_1);
        contractProcess.changeContent(headerId, contentId);
        //when
        ContractResult result = contractProcess.verify(headerId, author.getId());
        //then
        new ContractResultAssert(result).state(new DraftState());
    }

    @Test
    public void changingContentOfVerifiedMovesBackToDraft(){
        //given
        crateAcmeContract(author);
        ContentId contentId = commitContent(CONTENT_1);
        ContractResult result =  contractProcess.changeContent(headerId, contentId);
        new ContractResultAssert(result).state(new DraftState());

        result = contractProcess.verify(headerId, verifier.getId());
        new ContractResultAssert(result).state(new VerifiedState(verifier.getId()));
        //when
        contentId = commitContent(CONTENT_2);
        //then
        result = contractProcess.changeContent(headerId, contentId);
        new ContractResultAssert(result).state(new DraftState());
    }

    private ContentId commitContent(String content) {
        DocumentDTO doc = new DocumentDTO(null, content, ANY_VERSION);
        CommitResult result = editor.commit(doc);
        assertEquals(CommitResult.Result.SUCCESS, result.getResult());
        return new ContentId(result.getContentId());
    }

    private void crateAcmeContract(User user){
        ContractResult contractResult = contractProcess.createContract(user.getId());
        documentNumber = contractResult.getDocumentNumber();
        headerId = contractResult.getDocumentHeaderId();
    }
}
