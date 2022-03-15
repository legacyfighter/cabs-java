package io.legacyfighter.cabs.pricing;
import io.legacyfighter.cabs.geolocation.Distance;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

@Embeddable
public class Tariff {

    private static final Integer BASE_FEE = 8;

    private Float kmRate;

    private String name;

    public Tariff() {
    }
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="baseFee")),
    })
    private Money baseFee;
    public Tariff(float kmRate, String name, Money baseFee) {
        this.kmRate = kmRate;
        this.name = name;
        this.baseFee = baseFee;
    }

    public static Tariff ofTime(LocalDateTime time) {
        if ((time.getMonth() == Month.DECEMBER && time.getDayOfMonth() == 31) ||
                (time.getMonth() == Month.JANUARY && time.getDayOfMonth() == 1 && time.getHour() <= 6)) {
            return new Tariff(3.50f, "Sylwester", new Money((BASE_FEE + 3) * 100));
        } else {
            // piątek i sobota po 17 do 6 następnego dnia
            if ((time.getDayOfWeek() == DayOfWeek.FRIDAY && time.getHour() >= 17) ||
                    (time.getDayOfWeek() == DayOfWeek.SATURDAY && time.getHour() <= 6) ||
                    (time.getDayOfWeek() == DayOfWeek.SATURDAY && time.getHour() >= 17) ||
                    (time.getDayOfWeek() == DayOfWeek.SUNDAY && time.getHour() <= 6)) {
                return new Tariff(2.5f, "Weekend+", new Money((BASE_FEE + 2) * 100));
            } else {
                // pozostałe godziny weekendu
                if ((time.getDayOfWeek() == DayOfWeek.SATURDAY && time.getHour() > 6 && time.getHour() < 17) ||
                        (time.getDayOfWeek() == DayOfWeek.SUNDAY && time.getHour() > 6)) {
                    return new Tariff(1.5f, "Weekend", new Money(BASE_FEE * 100));
                } else {
                    // tydzień roboczy
                    return new Tariff(1.0f, "Standard", new Money((BASE_FEE + 1) * 100));
                }
            }
        }
    }

    public Money calculateCost(Distance distance) {
        BigDecimal priceBigDecimal = new BigDecimal(distance.toKmInFloat() * kmRate).setScale(2, RoundingMode.HALF_UP);
        int finalPrice = Integer.parseInt(String.valueOf(priceBigDecimal).replaceAll("\\.", ""));
        return new Money(finalPrice).add(baseFee);
    }

    public String getName() {
        return name;
    }

    public float getKmRate() {
        return kmRate;
    }

    public Integer getBaseFee() {
        return baseFee.toInt();
    }
}

