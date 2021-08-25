package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.Contract;
import io.legacyfighter.cabs.entity.ContractAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractAttachmentRepository extends JpaRepository<ContractAttachment, Long> {


    List<ContractAttachment> findByContract(Contract contract);
}
