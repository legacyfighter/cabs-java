package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.Transit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TransitRepository extends JpaRepository<Transit, Long> {

    List<Transit> findAllByDriverAndDateTimeBetween(Driver driver, Instant from, Instant to);

    List<Transit> findAllByClientAndFromAndStatusOrderByDateTimeDesc(Client client, Address from, Transit.Status status);

    List<Transit> findAllByClientAndFromAndPublishedAfterAndStatusOrderByDateTimeDesc(Client client, Address from, Instant when, Transit.Status status);

    List<Transit> findByClient(Client client);
}
