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
import org.springframework.util.comparator.Comparators;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static io.legacyfighter.cabs.entity.miles.ConstantUntil.constantUntil;
import static io.legacyfighter.cabs.entity.miles.ConstantUntil.constantUntilForever;

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

        AwardsAccount account = new AwardsAccount();

        account.setClient(client);
        account.setActive(false);
        account.setDate(Instant.now(clock));

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void activateAccount(Long clientId) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        }

        account.setActive(true);

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deactivateAccount(Long clientId) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));

        if (account == null) {
            throw new IllegalArgumentException("Account does not exists, id = " + clientId);
        }

        account.setActive(false);

        accountRepository.save(account);
    }

    @Override
    public AwardedMiles registerMiles(Long clientId, Long transitId) {
        AwardsAccount account = accountRepository.findByClient(clientRepository.getOne(clientId));
        Transit transit = transitRepository.getOne(transitId);
        if (transit == null) {
            throw new IllegalArgumentException("transit does not exists, id = " + transitId);
        }

        Instant now = Instant.now(clock);
        if (account == null || !account.isActive()) {
            return null;
        } else {
            AwardedMiles miles = new AwardedMiles();
            miles.setTransit(transit);
            miles.setDate(Instant.now(clock));
            miles.setClient(account.getClient());
            miles.setMiles(constantUntil(appProperties.getDefaultMilesBonus(), now.plus(appProperties.getMilesExpirationInDays(), ChronoUnit.DAYS)));
            account.increaseTransactions();

            milesRepository.save(miles);
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
            AwardedMiles _miles = new AwardedMiles();
            _miles.setTransit(null);
            _miles.setClient(account.getClient());
            _miles.setMiles(constantUntilForever(miles));
            _miles.setDate(Instant.now(clock));
            account.increaseTransactions();
            milesRepository.save(_miles);
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
            if (calculateBalance(clientId) >= miles && account.isActive()) {
                List<AwardedMiles> milesList = milesRepository.findAllByClient(client);
                int transitsCounter = transitRepository.findByClient(client).size();
                if (client.getClaims().size() >= 3) {
                    milesList.sort(Comparator.comparing(AwardedMiles::getExpirationDate, Comparators.nullsHigh()).reversed().thenComparing(Comparators.nullsHigh()));
                } else if (client.getType().equals(Client.Type.VIP)) {
                    milesList.sort(Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getExpirationDate, Comparators.nullsLow()));
                } else if (transitsCounter >= 15 && isSunday()) {
                    milesList.sort(Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getExpirationDate, Comparators.nullsLow()));
                } else if (transitsCounter >= 15) {
                    milesList.sort(Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getDate));
                } else {
                    milesList.sort(Comparator.comparing(AwardedMiles::getDate));
                }
                Instant now = Instant.now(clock);
                for (AwardedMiles iter : milesList) {
                    if (miles <= 0) {
                        break;
                    }
                    if (iter.cantExpire() || iter.getExpirationDate().isAfter(Instant.now(clock))) {
                        Integer milesAmount = iter.getMilesAmount(Instant.now(clock));
                        if (milesAmount <= miles) {
                            miles -= milesAmount;
                            iter.removeAll(now);
                        } else {
                            iter.subtract(miles, now);

                            miles = 0;
                        }
                        milesRepository.save(iter);
                    }
                }
            } else {
                throw new IllegalArgumentException("Insufficient miles, id = " + clientId + ", miles requested = " + miles);
            }
        }

    }

    @Override
    public Integer calculateBalance(Long clientId) {
        Client client = clientRepository.getOne(clientId);
        List<AwardedMiles> milesList = milesRepository.findAllByClient(client);
        Instant now = Instant.now(clock);
        Integer sum = milesList.stream()
                .filter(t -> t.getExpirationDate() != null && t.getExpirationDate().isAfter(Instant.now(clock)) || t.cantExpire())
                .map(t -> t.getMilesAmount(now))
                .reduce(0, Integer::sum);

        return sum;
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

        if (calculateBalance(fromClientId) >= miles && accountFrom.isActive()) {
            List<AwardedMiles> milesList = milesRepository.findAllByClient(fromClient);
            Instant now = Instant.now(clock);

            for (AwardedMiles iter : milesList) {
                if (iter.cantExpire() || iter.getExpirationDate().isAfter(Instant.now(clock))) {
                    Integer milesAmount = iter.getMilesAmount(now);
                    if (milesAmount <= miles) {
                        iter.setClient(accountTo.getClient());
                        miles -= milesAmount;
                    } else {
                        iter.subtract(miles, now);
                        AwardedMiles _miles = new AwardedMiles();

                        _miles.setClient(accountTo.getClient());
                        _miles.setMiles(iter.getMiles());

                        miles -= milesAmount;

                        milesRepository.save(_miles);

                    }
                    milesRepository.save(iter);
                }
            }

            accountFrom.increaseTransactions();
            accountTo.increaseTransactions();

            accountRepository.save(accountFrom);
            accountRepository.save(accountTo);
        }
    }
}
