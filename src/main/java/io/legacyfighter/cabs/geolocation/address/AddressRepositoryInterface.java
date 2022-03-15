package io.legacyfighter.cabs.geolocation.address;

import org.springframework.data.jpa.repository.JpaRepository;

interface AddressRepositoryInterface extends JpaRepository<Address, Long> {
    Address findByHash(Integer hash);

    default Integer findHashById(Long id) {
        return getOne(id).getHash();
    }
}
