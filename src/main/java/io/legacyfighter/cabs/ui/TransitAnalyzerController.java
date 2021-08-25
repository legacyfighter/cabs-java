package io.legacyfighter.cabs.ui;

import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.AnalyzedAddressesDTO;
import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.service.TransitAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TransitAnalyzerController {
    @Autowired
    TransitAnalyzer transitAnalyzer;

    @GetMapping("/transitAnalyze/{clientId}/{addressId}")
    AnalyzedAddressesDTO analyze(@PathVariable Long clientId, @PathVariable Long addressId) {
        List<Address> addresses = transitAnalyzer.analyze(clientId, addressId);
        List<AddressDTO> addressDTOs = addresses
                .stream()
                .map(a -> new AddressDTO(a))
                .collect(Collectors.toList());

        return new AnalyzedAddressesDTO(addressDTOs);
    }
}
