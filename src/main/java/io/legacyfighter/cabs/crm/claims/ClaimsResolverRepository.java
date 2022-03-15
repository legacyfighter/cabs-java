package io.legacyfighter.cabs.crm.claims;

import org.springframework.data.jpa.repository.JpaRepository;

interface ClaimsResolverRepository extends JpaRepository<ClaimsResolver, Long> {

    ClaimsResolver findByClientId(Long clientId);

}
