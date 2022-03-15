package io.legacyfighter.cabs.crm.claims;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findAllByOwnerId(Long ownerId);
}
