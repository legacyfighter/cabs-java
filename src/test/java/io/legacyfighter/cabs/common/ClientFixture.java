package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.crm.Client;

import io.legacyfighter.cabs.crm.ClientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ClientFixture {

    @Autowired
    ClientRepository clientRepository;

    Client aClient() {
        return clientRepository.save(new Client());
    }

    Client aClient(Client.Type type) {
        Client client = new Client();
        client.setType(type);
        return clientRepository.save(client);
    }
}
