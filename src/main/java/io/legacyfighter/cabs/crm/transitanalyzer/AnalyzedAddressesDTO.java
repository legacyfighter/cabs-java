package io.legacyfighter.cabs.crm.transitanalyzer;

import io.legacyfighter.cabs.geolocation.address.AddressDTO;

import java.util.List;

public class AnalyzedAddressesDTO {
    private List<AddressDTO> addresses;

    public AnalyzedAddressesDTO() {
    }

    public AnalyzedAddressesDTO(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

}
