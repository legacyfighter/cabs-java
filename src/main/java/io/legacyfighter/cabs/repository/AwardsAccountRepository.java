package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.miles.AwardedMiles;
import io.legacyfighter.cabs.entity.miles.AwardsAccount;
import io.legacyfighter.cabs.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardsAccountRepository extends JpaRepository<AwardsAccount, Long> {

    AwardsAccount findByClientId(Long clientId);

    default List<AwardedMiles> findAllMilesBy(Client client) {
        return findByClientId(client.getId()).getMiles();
    }

}