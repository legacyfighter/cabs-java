package io.legacyfighter.cabs.contracts.application.dynamic;

import io.legacyfighter.cabs.contracts.application.acme.dynamic.DocumentOperationResult;
import io.legacyfighter.cabs.contracts.application.acme.dynamic.DocumentResourceManager;
import io.legacyfighter.cabs.contracts.application.editor.CommitResult;
import io.legacyfighter.cabs.contracts.application.editor.DocumentDTO;
import io.legacyfighter.cabs.contracts.application.editor.DocumentEditor;
import io.legacyfighter.cabs.contracts.legacy.User;
import io.legacyfighter.cabs.contracts.legacy.UserRepository;
import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.content.ContentVersion;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import io.legacyfighter.cabs.contracts.model.state.dynamic.acme.AcmeContractStateAssembler;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.predicates.statechange.AuthorIsNotAVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AcmeContractManagerBasedOnDynamicStateModelTest {

    @Autowired
    DocumentEditor editor;
    @Autowired
    DocumentResourceManager documentResourceManager;
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
        DocumentOperationResult result = documentResourceManager.changeContent(headerId, contentId);
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.DRAFT).editable().possibleNextStates(AcmeContractStateAssembler.VERIFIED,AcmeContractStateAssembler.ARCHIVED);
        //when
        result = documentResourceManager.changeState(headerId, AcmeContractStateAssembler.VERIFIED, verifierParam());
        //then
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.VERIFIED).editable().possibleNextStates(AcmeContractStateAssembler.PUBLISHED, AcmeContractStateAssembler.ARCHIVED);
    }



    @Test
    public void authorCanNotVerify(){
        //given
        crateAcmeContract(author);
        ContentId contentId = commitContent(CONTENT_1);
        DocumentOperationResult result = documentResourceManager.changeContent(headerId, contentId);
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.DRAFT);
        //when
        result = documentResourceManager.changeState(headerId, AcmeContractStateAssembler.VERIFIED, authorParam());
        //then
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.DRAFT);
    }

    @Test
    public void changingContentOfVerifiedMovesBackToDraft(){
        //given
        crateAcmeContract(author);
        ContentId contentId = commitContent(CONTENT_1);
        DocumentOperationResult result = documentResourceManager.changeContent(headerId, contentId);
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.DRAFT).editable();

        result = documentResourceManager.changeState(headerId, AcmeContractStateAssembler.VERIFIED, verifierParam());
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.VERIFIED).editable();
        //when
        contentId = commitContent(CONTENT_2);
        result = documentResourceManager.changeContent(headerId, contentId);
        //then
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.DRAFT).editable();
    }

    @Test
    public void publishedCanNotBeChanged(){
        //given
        crateAcmeContract(author);
        ContentId firstContentId = commitContent(CONTENT_1);
        DocumentOperationResult result = documentResourceManager.changeContent(headerId, firstContentId);
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.DRAFT).editable();

        result = documentResourceManager.changeState(headerId, AcmeContractStateAssembler.VERIFIED, verifierParam());
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.VERIFIED).editable();

        result = documentResourceManager.changeState(headerId, AcmeContractStateAssembler.PUBLISHED, emptyParam());
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.PUBLISHED).uneditable();
        //when
        ContentId newContentId = commitContent(CONTENT_2);
        result = documentResourceManager.changeContent(headerId, newContentId);
        //then
        new DocumentOperationResultAssert(result).state(AcmeContractStateAssembler.PUBLISHED).uneditable().content(firstContentId);
    }



    private ContentId commitContent(String content) {
        DocumentDTO doc = new DocumentDTO(null, content, ANY_VERSION);
        CommitResult result = editor.commit(doc);
        assertEquals(CommitResult.Result.SUCCESS, result.getResult());
        return new ContentId(result.getContentId());
    }

    private void crateAcmeContract(User user){
        DocumentOperationResult result = documentResourceManager.createDocument(user.getId());
        documentNumber = result.getDocumentNumber();
        headerId = result.getDocumentHeaderId();
    }

    private Map<String, Object> verifierParam() {
        return Map.of(AuthorIsNotAVerifier.PARAM_VERIFIER, verifier.getId());
    }

    private Map<String, Object> authorParam() {
        return Map.of(AuthorIsNotAVerifier.PARAM_VERIFIER, author.getId());
    }

    private Map<String, Object> emptyParam() {
        return Map.of();
    }
}
