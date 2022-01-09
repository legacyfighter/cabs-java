package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.dto.AwardsAccountDTO;
import io.legacyfighter.cabs.entity.miles.AwardedMiles;
import io.legacyfighter.cabs.entity.miles.AwardsAccount;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.repository.AwardedMilesRepository;
import io.legacyfighter.cabs.repository.AwardsAccountRepository;
import io.legacyfighter.cabs.repository.ClientRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static io.legacyfighter.cabs.entity.miles.AwardsAccount.notActiveAccount;


@Service
public class AwardsServiceImpl implements AwardsService {

    @Autowired
    private AwardsAccountRepository accountRepository;
    @Autowired
    private AwardedMilesRepository milesRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransitRepository transitRepository;
    @Autowired
    private Clock clock;
    @Autowired
    private AppProperties appProperties;

    @Override
    public AwardsAccountDTO findBy(Long clientId) {
        return new AwardsAccountDTO(accountRepository.findByClient(clientRepository.getOne(clientId)));
    }

    @Override
    public void registerToProgram(Long clientId) {
        Client client = clientRepository.getOne(clientId);

        if (client == null) {
            throw new IllegalArgumentException("Client does not exists, id = " + clientId);
        }

        AwardsAccount account = notActiveAccount(client, Instant.now(clock));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void activateAccount(Long clientId) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        }

        account.activate();

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deactivateAccount(Long clientId) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        }

        account.deactivate();

        accountRepository.save(account);
    }

    @Override
    public AwardedMiles registerMiles(Long clientId, Long transitId) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));
        Transit transit = transitRepository.getOne(transitId);
        if (transit == null) {
            throw new IllegalArgumentException("transit does not exists, id = " + transitId);
        }

        if (account == null || !account.isActive()) {
            return null;
        } else {
            Instant expireAt = Instant.now(clock).plus(appProperties.getMilesExpirationInDays(), ChronoUnit.DAYS);
            AwardedMiles miles = account.addExpiringMiles(appProperties.getDefaultMilesBonus(), expireAt, transit, Instant.now(clock));
            accountRepository.save(account);
            return miles;
        }
    }

    boolean isSunday() {
        return Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    @Override
    public AwardedMiles registerNonExpiringMiles(Long clientId, Integer miles) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        } else {
            AwardedMiles _miles = account.addNonExpiringMiles(miles, Instant.now(clock));
            accountRepository.save(account);
            return _miles;
        }
    }

    @Override
    @Transactional
    public void removeMiles(Long clientId, Integer miles) {
        Client client = clientRepository.getOne(clientId);
        AwardsAccount account = accountRepository.findByClient(client);
        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        } else {
            account.remove(miles, Instant.now(clock), transitRepository.findByClient(client).size(), client.getClaims().size(), client.getType(), isSunday());
        }

    }

    @Override
    public Integer calculateBalance(Long clientId) {
        Client client = clientRepository.getOne(clientId);
        AwardsAccount account = accountRepository.findByClient(client);
        return account.calculateBalance(Instant.now(clock));
    }

    @Override
    public void transferMiles(Long fromClientId, Long toClientId, Integer miles) {
        Client fromClient = clientRepository.getOne(fromClientId);
        AwardsAccount accountFrom = accountRepository.findByClient(fromClient);
        AwardsAccount accountTo = accountRepository.findByClient(clientRepository.getOne(toClientId));
        if (accountFrom == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + fromClientId);
        }
        if (accountTo == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + toClientId);
        }
        accountFrom.moveMilesTo(accountTo, miles, Instant.now(clock));
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);

    }
}
