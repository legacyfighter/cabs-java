package io.legacyfighter.cabs.money;

import javax.persistence.Embeddable;
import java.util.Locale;
import java.util.Objects;

@Embeddable
public class Money {

    public static final Money ZERO = new Money(0);
    private Integer value;

    public Money() {
    }

    public Money(Integer value) {
        this.value = value;
    }

    public Money add(Money other) {
        return new Money(value + other.value);
    }

    public Money subtract(Money other) {
        return new Money(value - other.value);
    }

    public Money percentage(int percentage) {
        return new Money((int) Math.round(percentage * value/100.0));
    }

    public Money percentage(Double percentage) {
        return new Money((int) Math.round(percentage * value/100));
    }

    public Integer toInt() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return value.equals(money.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        double value = Double.valueOf(this.value) / 100;
        return String.format(Locale.US, "%.2f", value);
    }
}
