package io.legacyfighter.cabs.contracts.model.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentContentRepository extends JpaRepository<DocumentContent, UUID> {

}
