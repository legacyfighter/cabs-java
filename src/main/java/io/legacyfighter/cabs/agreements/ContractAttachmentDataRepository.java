package io.legacyfighter.cabs.agreements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

interface ContractAttachmentDataRepository extends JpaRepository<ContractAttachmentData, Long> {

    Set<ContractAttachmentData> findByContractAttachmentNoIn(List<UUID> attachmentIds);

    @Modifying
    @Query("delete FROM ContractAttachmentData cad WHERE cad.contractAttachmentNo =" +
            " (SELECT ca.contractAttachmentNo FROM ContractAttachment ca WHERE ca.id = ?1)")
    int deleteByAttachmentId(Long attachmentId);
}
