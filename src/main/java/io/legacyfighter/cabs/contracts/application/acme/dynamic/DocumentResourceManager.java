package io.legacyfighter.cabs.contracts.application.acme.dynamic;

import io.legacyfighter.cabs.contracts.legacy.User;
import io.legacyfighter.cabs.contracts.legacy.UserRepository;
import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.DocumentHeaderRepository;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import io.legacyfighter.cabs.contracts.model.state.dynamic.ChangeCommand;
import io.legacyfighter.cabs.contracts.model.state.dynamic.State;
import io.legacyfighter.cabs.contracts.model.state.dynamic.StateConfig;
import io.legacyfighter.cabs.contracts.model.state.dynamic.acme.AcmeContractStateAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;

@Service
@Transactional
public class DocumentResourceManager {

    @Autowired
    private DocumentHeaderRepository documentHeaderRepository;

    @Autowired
    private AcmeContractStateAssembler assembler;

    @Autowired
    private UserRepository userRepository;

    public void changeContent(){

    }

    public DocumentOperationResult createDocument(Long authorId){
        User author = userRepository.getOne(authorId);

        DocumentNumber number = generateNumber();
        DocumentHeader documentHeader = new DocumentHeader(author.getId(), number);

        StateConfig stateConfig = assembler.assemble();
        State state = stateConfig.begin(documentHeader);

        documentHeaderRepository.save(documentHeader);

        return generateDocumentOperationResult(DocumentOperationResult.Result.SUCCESS, state);
    }

    public DocumentOperationResult changeState(Long documentId, String desiredState, Map<String, Object> params){
        DocumentHeader documentHeader = documentHeaderRepository.getOne(documentId);
        StateConfig stateConfig = assembler.assemble();
        State state = stateConfig.recreate(documentHeader);

        state = state.changeState(new ChangeCommand(desiredState, params));

        documentHeaderRepository.save(documentHeader);

        return generateDocumentOperationResult(DocumentOperationResult.Result.SUCCESS, state);
    }

    public DocumentOperationResult changeContent(Long headerId, ContentId contentVersion) {
        DocumentHeader documentHeader = documentHeaderRepository.getOne(headerId);
        StateConfig stateConfig = assembler.assemble();
        State state = stateConfig.recreate(documentHeader);
        state = state.changeContent(contentVersion);

        documentHeaderRepository.save(documentHeader);
        return generateDocumentOperationResult(DocumentOperationResult.Result.SUCCESS, state);
    }

    private DocumentOperationResult generateDocumentOperationResult(DocumentOperationResult.Result result, State state) {
        return new DocumentOperationResult(result, state.getDocumentHeader().getId(),
                state.getDocumentHeader().getDocumentNumber(), state.getStateDescriptor(), state.getDocumentHeader().getContentId(),
                extractPossibleTransitionsAndRules(state),
                state.isContentEditable(),
                extractContentChangePredicate(state));
    }

    private String extractContentChangePredicate(State state) {
        if (state.isContentEditable())
            return state.getContentChangePredicate().getClass().getTypeName();
        return null;
    }


    private Map<String, List<String>> extractPossibleTransitionsAndRules(State state) {
        Map<String, List<String>> transitionsAndRules = new HashMap<>();

        Map<State, List<BiFunction<State, ChangeCommand, Boolean>>> stateChangePredicates = state.getStateChangePredicates();
        for (State s : stateChangePredicates.keySet()){
            //transition to self is not important
            if (s.equals(state))
                continue;

            List<BiFunction<State, ChangeCommand, Boolean>> predicates = stateChangePredicates.get(s);
            List<String> ruleNames = new ArrayList<>();
            for (BiFunction<State, ChangeCommand, Boolean> predicate : predicates){
                ruleNames.add(predicate.getClass().getTypeName());
            }
            transitionsAndRules.put(s.getStateDescriptor(), ruleNames);
        }

        return transitionsAndRules;
    }

    private DocumentNumber generateNumber() {
        return new DocumentNumber("nr: " + new Random().nextInt()); //TODO integrate with doc number generator
    }
}
