package io.legacyfighter.cabs.common;

import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class AddressMatcher implements ArgumentMatcher<Address> {

    private String country;
    private String city;
    private String street;
    private Integer buildingNumber;

    public AddressMatcher(Address address) {
        this(address.getCountry(), address.getCity(), address.getStreet(), address.getBuildingNumber());
    }

    public AddressMatcher(AddressDTO dto) {
        this(dto.toAddressEntity());
    }

    public AddressMatcher(String country, String city, String street, Integer buildingNumber) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    @Override
    public boolean matches(Address right) {
        if (right == null) {
            return false;
        }
        return Objects.equals(country, right.getCountry()) &&
                Objects.equals(city, right.getCity()) &&
                Objects.equals(street, right.getStreet()) &&
                Objects.equals(buildingNumber, right.getBuildingNumber());
    }
}
