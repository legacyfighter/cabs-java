package io.legacyfighter.cabs.entity;
import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

public class Tariff {

    private static final Integer BASE_FEE = 8;

    private Float kmRate;

    private String name;

    private Integer baseFee;

    private Tariff(float kmRate, String name, Integer baseFee) {
        this.kmRate = kmRate;
        this.name = name;
        this.baseFee = baseFee;
    }

    public static Tariff ofTime(LocalDateTime time) {
        if ((time.getMonth() == Month.DECEMBER && time.getDayOfMonth() == 31) ||
                (time.getMonth() == Month.JANUARY && time.getDayOfMonth() == 1 && time.getHour() <= 6)) {
            return new Tariff(3.50f, "Sylwester", BASE_FEE + 3);
        } else {
            // piątek i sobota po 17 do 6 następnego dnia
            if ((time.getDayOfWeek() == DayOfWeek.FRIDAY && time.getHour() >= 17) ||
                    (time.getDayOfWeek() == DayOfWeek.SATURDAY && time.getHour() <= 6) ||
                    (time.getDayOfWeek() == DayOfWeek.SATURDAY && time.getHour() >= 17) ||
                    (time.getDayOfWeek() == DayOfWeek.SUNDAY && time.getHour() <= 6)) {
                return new Tariff(2.5f, "Weekend+", BASE_FEE + 2);
            } else {
                // pozostałe godziny weekendu
                if ((time.getDayOfWeek() == DayOfWeek.SATURDAY && time.getHour() > 6 && time.getHour() < 17) ||
                        (time.getDayOfWeek() == DayOfWeek.SUNDAY && time.getHour() > 6)) {
                    return new Tariff(1.5f, "Weekend", BASE_FEE);
                } else {
                    // tydzień roboczy
                    return new Tariff(1.0f, "Standard", BASE_FEE + 1);
                }
            }
        }
    }

    public Money calculateCost(Distance distance) {
        BigDecimal priceBigDecimal = new BigDecimal(distance.toKmInFloat() * kmRate  + baseFee).setScale(2, RoundingMode.HALF_UP);
        int finalPrice = Integer.parseInt(String.valueOf(priceBigDecimal).replaceAll("\\.", ""));
        return new Money(finalPrice);
    }

    public String getName() {
        return name;
    }

    public float getKmRate() {
        return kmRate;
    }

    public Integer getBaseFee() {
        return baseFee;
    }
}

