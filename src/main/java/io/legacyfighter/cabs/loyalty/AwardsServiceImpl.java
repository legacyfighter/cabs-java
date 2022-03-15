package io.legacyfighter.cabs.loyalty;

import io.legacyfighter.cabs.config.AppProperties;
import io.legacyfighter.cabs.crm.claims.ClaimService;
import io.legacyfighter.cabs.crm.ClientDTO;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.ride.Transit;
import io.legacyfighter.cabs.ride.TransitRepository;
import io.legacyfighter.cabs.crm.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.Comparators;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import static io.legacyfighter.cabs.loyalty.AwardsAccount.notActiveAccount;


@Service
public class AwardsServiceImpl implements AwardsService {

    @Autowired
    private AwardsAccountRepository accountRepository;
    @Autowired
    private TransitRepository transitRepository;
    @Autowired
    private Clock clock;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClaimService claimService;

    @Override
    public AwardsAccountDTO findBy(Long clientId) {
        return new AwardsAccountDTO(accountRepository.findByClientId(clientId), clientService.load(clientId));
    }

    @Override
    public void registerToProgram(Long clientId) {
        ClientDTO client = clientService.load(clientId);

        if (client == null) {
            throw new IllegalArgumentException("Client does not exists, id = " + clientId);
        }

        AwardsAccount account = notActiveAccount(clientId, Instant.now(clock));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void activateAccount(Long clientId) {
        AwardsAccount account = accountRepository.findByClientId(clientId);

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        }

        account.activate();

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deactivateAccount(Long clientId) {
        AwardsAccount account = accountRepository.findByClientId(clientId);

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        }

        account.deactivate();

        accountRepository.save(account);
    }

    @Override
    public AwardedMiles registerMiles(Long clientId, Long transitId) {
        AwardsAccount account = accountRepository.findByClientId(clientId);
        Transit transit = transitRepository.getOne(transitId);
        if (transit == null) {
            throw new IllegalArgumentException("transit does not exists, id = " + transitId);
        }

        if (account == null || !account.isActive()) {
            return null;
        } else {
            Instant expireAt = Instant.now(clock).plus(appProperties.getMilesExpirationInDays(), ChronoUnit.DAYS);
            AwardedMiles miles = account.addExpiringMiles(appProperties.getDefaultMilesBonus(), expireAt, transitId, Instant.now(clock));
            accountRepository.save(account);
            return miles;
        }
    }

    boolean isSunday() {
        return Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    @Override
    public AwardedMiles registerNonExpiringMiles(Long clientId, Integer miles) {
        AwardsAccount account = accountRepository.findByClientId(clientId);

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
        AwardsAccount account = accountRepository.findByClientId(clientId);
        ClientDTO client = clientService.load(clientId);

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        } else {
            Integer numberOfClaims = claimService.getNumberOfClaims(clientId);
            account.remove(miles, Instant.now(clock), chooseStrategy(transitRepository.findByClientId(clientId).size(), numberOfClaims, client.getType(), isSunday()));
        }

    }

    private Comparator<AwardedMiles> chooseStrategy(int transitsCounter, int claimsCounter, Client.Type type, boolean isSunday) {
        if (claimsCounter >= 3) {
            return Comparator.comparing(AwardedMiles::getExpirationDate, Comparators.nullsHigh()).reversed().thenComparing(Comparators.nullsHigh());
        } else if (type.equals(Client.Type.VIP)) {
            return Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getExpirationDate, Comparators.nullsLow());
        } else if (transitsCounter >= 15 && isSunday) {
            return Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getExpirationDate, Comparators.nullsLow());
        } else if (transitsCounter >= 15) {
            return Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getDate);
        } else {
            return Comparator.comparing(AwardedMiles::getDate);
        }
    }

    @Override
    public Integer calculateBalance(Long clientId) {
        AwardsAccount account = accountRepository.findByClientId(clientId);
        return account.calculateBalance(Instant.now(clock));
    }

    @Override
    public void transferMiles(Long fromClientId, Long toClientId, Integer miles) {
        AwardsAccount accountFrom = accountRepository.findByClientId(fromClientId);
        AwardsAccount accountTo = accountRepository.findByClientId(toClientId);
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
