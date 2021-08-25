package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.entity.Transit;

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
