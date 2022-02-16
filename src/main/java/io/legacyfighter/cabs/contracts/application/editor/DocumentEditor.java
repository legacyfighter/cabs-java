package io.legacyfighter.cabs.contracts.application.editor;

import io.legacyfighter.cabs.contracts.model.content.DocumentContent;
import io.legacyfighter.cabs.contracts.model.content.DocumentContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DocumentEditor {
    @Autowired
    private DocumentContentRepository documentContentRepository;

    public CommitResult commit(DocumentDTO document){
        UUID previousID = document.getContentId();
        DocumentContent content = new DocumentContent(previousID, document.getDocumentVersion(), document.getPhysicalContent());
        documentContentRepository.save(content);
        return new CommitResult(content.getId(), CommitResult.Result.SUCCESS);
    }


    public DocumentDTO get(UUID contentId){
        DocumentContent content = documentContentRepository.getOne(contentId);
        return new DocumentDTO(contentId, content.getPhysicalContent(), content.getDocumentVersion());
    }

}
