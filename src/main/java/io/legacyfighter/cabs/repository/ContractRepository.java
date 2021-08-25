package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByPartnerName(String partnerName);
}
