package io.legacyfighter.cabs.contracts.model;


public interface DocumentHeaderRepository {
    DocumentHeader getOne(Long id);

    void save(DocumentHeader header);
}
