package io.legacyfighter.cabs.entity.miles;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Transit;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.comparator.Comparators;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

import static io.legacyfighter.cabs.entity.miles.ConstantUntil.constantUntil;
import static io.legacyfighter.cabs.entity.miles.ConstantUntil.constantUntilForever;

@Entity
public class AwardsAccount extends BaseEntity {

    @OneToOne
    private Client client;

    @Column(nullable = false)
    private Instant date = Instant.now();

    @Column(nullable = false)
    private Boolean isActive = false;

    @Column(nullable = false)
    private Integer transactions = 0;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.JOIN)
    private Set<AwardedMiles> miles = new HashSet<>();

    public AwardsAccount() {
    }

    public AwardsAccount(Client client, boolean isActive, Instant date) {
        this.client = client;
        this.isActive = isActive;
        this.date = date;
    }

    public static AwardsAccount notActiveAccount(Client client, Instant date) {
        return new AwardsAccount(client, false, date);
    }

    public AwardedMiles addExpiringMiles(Integer amount, Instant expireAt, Transit transit, Instant when) {
        AwardedMiles expiringMiles = new AwardedMiles(this, transit, client, when, constantUntil(amount, expireAt));
        this.miles.add(expiringMiles);
        transactions++;
        return expiringMiles;
    }

    public AwardedMiles addNonExpiringMiles(Integer amount, Instant when) {
        AwardedMiles nonExpiringMiles = new AwardedMiles(this, null, client, when, constantUntilForever(amount));
        this.miles.add(nonExpiringMiles);
        transactions++;
        return nonExpiringMiles;
    }

    public Integer calculateBalance(Instant at) {
        return miles
                .stream()
                .filter(t -> t.getExpirationDate() != null && t.getExpirationDate().isAfter(at) || t.cantExpire())
                .map(t -> t.getMilesAmount(at))
                .reduce(0, Integer::sum);
    }

    public void remove(Integer miles, Instant when, int transitsCounter, int claimsCounter, Client.Type type, boolean isSunday) {
        if (calculateBalance(when) >= miles && isActive()) {
            List<AwardedMiles> milesList = new ArrayList<>(this.miles);
            if (claimsCounter >= 3) {
                milesList.sort(Comparator.comparing(AwardedMiles::getExpirationDate, Comparators.nullsHigh()).reversed().thenComparing(Comparators.nullsHigh()));
            } else if (type.equals(Client.Type.VIP)) {
                milesList.sort(Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getExpirationDate, Comparators.nullsLow()));
            } else if (transitsCounter >= 15 && isSunday) {
                milesList.sort(Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getExpirationDate, Comparators.nullsLow()));
            } else if (transitsCounter >= 15) {
                milesList.sort(Comparator.comparing(AwardedMiles::cantExpire).thenComparing(AwardedMiles::getDate));
            } else {
                milesList.sort(Comparator.comparing(AwardedMiles::getDate));
            }

            for (AwardedMiles iter : milesList) {
                if (miles <= 0) {
                    break;
                }
                if (iter.cantExpire() || iter.getExpirationDate().isAfter(when)) {
                    Integer milesAmount = iter.getMilesAmount(when);
                    if (milesAmount <= miles) {
                        miles -= milesAmount;
                        iter.removeAll(when);
                    } else {
                        iter.subtract(miles, when);

                        miles = 0;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Insufficient miles, id = " + client.getId() + ", miles requested = " + miles);
        }
    }

    public void moveMilesTo(AwardsAccount accountTo, Integer amount, Instant when) {
        if (calculateBalance(when) >= amount && isActive()) {
            for (AwardedMiles iter : miles) {
                if (iter.cantExpire() || iter.getExpirationDate().isAfter(when)) {
                    Integer milesAmount = iter.getMilesAmount(when);
                    if (milesAmount <= amount) {
                        iter.transferTo(accountTo);
                        amount -= milesAmount;
                    } else {
                        iter.subtract(amount, when);
                        iter.transferTo(accountTo);
                        amount -= iter.getMilesAmount(when);
                    }
                }
            }
            transactions++;
            accountTo.transactions++;
        }
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AwardsAccount))
            return false;

        AwardsAccount other = (AwardsAccount) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }

    public Client getClient() {
        return client;
    }

    public Boolean isActive() {
        return isActive;
    }

    public Integer getTransactions() {
        return transactions;
    }

    public Instant getDate() {
        return date;
    }

    public List<AwardedMiles> getMiles() {
        return Collections.unmodifiableList(new ArrayList<>(miles));
    }
}
