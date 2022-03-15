package io.legacyfighter.cabs.agreements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByPartnerName(String partnerName);

    @Query("SELECT c FROM Contract c JOIN ContractAttachment ca ON ca.contract.id = c.id WHERE ca.id = ?1")
    Contract findByAttachmentId(Long attachmentId);

    @Query("SELECT c.contractAttachmentNo FROM ContractAttachment c WHERE c.id = ?1")
    UUID findContractAttachmentNoById(Long attachmentId);
}
