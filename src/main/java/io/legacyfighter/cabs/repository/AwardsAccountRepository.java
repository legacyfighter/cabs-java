package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.AwardsAccount;
import io.legacyfighter.cabs.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardsAccountRepository extends JpaRepository<AwardsAccount, Long> {
    AwardsAccount findByClient(Client client);
}