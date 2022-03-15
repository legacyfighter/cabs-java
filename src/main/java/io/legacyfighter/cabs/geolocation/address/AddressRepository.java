package io.legacyfighter.cabs.geolocation.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AddressRepository {
    @Autowired
    AddressRepositoryInterface addressRepositoryInterface;

    // FIX ME: To replace with getOrCreate method instead of that?
    // Actual workaround for address uniqueness problem: assign result from repo.save to variable for later usage
    public Address save(Address address) {
        address.hash();

        if (address.getId() == null) {
            Address existingAddress = addressRepositoryInterface.findByHash(address.getHash());

            if (existingAddress != null) {
                return existingAddress;
            }
        }

        return addressRepositoryInterface.save(address);
    }

    public Address getOne(Long id) {
        return addressRepositoryInterface.getOne(id);
    }

    @Transactional
    public Integer findHashById(Long addressId) {
        return addressRepositoryInterface.findHashById(addressId);
    }

    public Address getByHash(int hash) {
        return addressRepositoryInterface.findByHash(hash);
    }
}
