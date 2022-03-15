package io.legacyfighter.cabs.loyalty;

import io.legacyfighter.cabs.crm.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardsAccountRepository extends JpaRepository<AwardsAccount, Long> {

    AwardsAccount findByClientId(Long clientId);

    default List<AwardedMiles> findAllMilesBy(Client client) {
        return findByClientId(client.getId()).getMiles();
    }

}