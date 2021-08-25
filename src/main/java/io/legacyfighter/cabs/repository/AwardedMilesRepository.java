package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.AwardedMiles;
import io.legacyfighter.cabs.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardedMilesRepository extends JpaRepository<AwardedMiles, Long> {
    List<AwardedMiles> findAllByClient(Client client);
}