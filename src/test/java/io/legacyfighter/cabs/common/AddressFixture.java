package io.legacyfighter.cabs.common;



import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class AddressFixture {

    @Autowired
    AddressRepository addressRepository;

    Address anAddress() {
        return addressRepository.save(new Address("Polska", "Warszawa", "Młynarska", 20));
    }
}
