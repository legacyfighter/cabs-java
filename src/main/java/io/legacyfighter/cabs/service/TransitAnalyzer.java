package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.repository.AddressRepository;
import io.legacyfighter.cabs.repository.ClientRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransitAnalyzer {
    @Autowired
    TransitRepository transitRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AddressRepository addressRepository;

    @Transactional
    public List<Address> analyze(Long clientId, Long addressId) {
        Client client = clientRepository.getOne(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client does not exists, id = " + clientId);
        }
        Address address = addressRepository.getOne(addressId);
        if (address == null) {
            throw new IllegalArgumentException("Address does not exists, id = " + addressId);
        }
        return analyze(client, address, null);
    }

    // Brace yourself, deadline is coming... They made me to do it this way.
    // Tested!
    private List<Address> analyze(Client client, Address from, Transit t) {
        List<Transit> ts;

        if (t == null) {
            ts = transitRepository.findAllByClientAndFromAndStatusOrderByDateTimeDesc(client, from, Transit.Status.COMPLETED);
        } else {
            ts = transitRepository.findAllByClientAndFromAndPublishedAfterAndStatusOrderByDateTimeDesc(client, from, t.getPublished(), Transit.Status.COMPLETED);;
        }

        // Workaround for performance reasons.
        if (ts.size() > 1000 && client.getId() == 666) {
            // No one will see a difference for this customer ;)
            ts = ts.stream().limit(1000).collect(Collectors.toList());
        }

//        if (ts.isEmpty()) {
//            return List.of(t.getTo());
//        }

        if (t != null ) {
            ts = ts.stream()
                    .filter(_t -> t.getCompleteAt().plus(15, ChronoUnit.MINUTES).isAfter(_t.getStarted()))
                    // Before 2018-01-01:
                    //.filter(t -> t.getCompleteAt().plus(15, ChronoUnit.MINUTES).isAfter(t.getPublished()))
                    .collect(Collectors.toList());
        }

        if (ts.isEmpty()) {
            return List.of(t.getTo());
        }

        Comparator<List> comparator = Comparator.comparingInt(List::size);

        return ts.stream()
                .map(_t -> {
                    List<Address> result = new ArrayList<>();
                    result.add(_t.getFrom());
                    result.addAll(analyze(client, _t.getTo(), _t));
                    return result;
                })
                .sorted(comparator.reversed())
                .collect(Collectors.toList())
                .stream().findFirst().orElse(new ArrayList<>());
    }
}
