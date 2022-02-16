package io.legacyfighter.cabs.contracts.infra;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.DocumentHeaderRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

//LockModeType surprised you? MUST see: https://youtu.be/uj25PbkHb94?t=499

@Repository
public class JpaDocumentHeaderRepository implements DocumentHeaderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DocumentHeader getOne(Long id){
        return entityManager.find(DocumentHeader.class, id, LockModeType.OPTIMISTIC);
    }

    @Override
    public void save(DocumentHeader header) {
        if (entityManager.contains(header))
            entityManager.lock(header, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        else
            entityManager.persist(header);
    }
}
