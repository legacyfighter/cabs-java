package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.ContractAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ContractAttachmentRepository extends JpaRepository<ContractAttachment, Long> {

    Set<ContractAttachment> findByContractId(Long id);
}
