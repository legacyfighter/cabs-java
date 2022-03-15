package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.loyalty.AwardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class AwardsAccountFixture {

    @Autowired
    AwardsService awardsService;

    void awardsAccount(Client client) {
        awardsService.registerToProgram(client.getId());
    }

    void activeAwardsAccount(Client client) {
        awardsAccount(client);
        awardsService.activateAccount(client.getId());
    }
}
