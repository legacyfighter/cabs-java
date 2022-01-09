package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.ClaimsResolver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimsResolverRepository extends JpaRepository<ClaimsResolver, Long> {

    ClaimsResolver findByClientId(Long clientId);


}
