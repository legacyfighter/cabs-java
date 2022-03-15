package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.Transit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TransitRepository extends JpaRepository<Transit, Long> {

    @Query("select T from Transit T join TransitDetails TD ON T.id = TD.transitId where T.driver = ?1 and TD.dateTime between ?2 and ?3")
    List<Transit> findAllByDriverAndDateTimeBetween(Driver driver, Instant from, Instant to);

    List<Transit> findAllByStatus(Transit.Status status);

    @Query("select T from Transit T join TransitDetails TD ON T.id = TD.transitId where TD.client = ?1")
    List<Transit> findByClient(Client client);
}
