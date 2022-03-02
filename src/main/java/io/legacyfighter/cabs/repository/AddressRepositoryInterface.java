package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepositoryInterface extends JpaRepository<Address, Long> {
    Address findByHash(Integer hash);

    default Integer findHashById(Long id) {
        return getOne(id).getHash();
    }
}
