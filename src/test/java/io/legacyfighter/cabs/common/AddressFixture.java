package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.geolocation.address.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@Component
class AddressFixture {

    @Autowired
    AddressRepository addressRepository;

    Address anAddress() {
        return addressRepository.save(new Address("Polska", "Warszawa", "Młynarska", 20));
    }

    public AddressDTO anAddress(GeocodingService geocodingService, String country, String city, String street, int buildingNumber) {
        AddressDTO addressDTO = new AddressDTO(country, city, street, buildingNumber);
        Random random = new Random();
        when(geocodingService.geocodeAddress(argThat(new AddressMatcher(addressDTO)))).thenReturn(new double[]{random.nextInt(), random.nextInt()});
        return addressDTO;
    }
}
