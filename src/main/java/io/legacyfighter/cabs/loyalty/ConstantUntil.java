package io.legacyfighter.cabs.loyalty;

import javax.persistence.Embeddable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
class ConstantUntil implements Miles {

    public static ConstantUntil constantUntilForever(int amount) {
        return new ConstantUntil(amount, Instant.MAX);
    }

    public static ConstantUntil constantUntil(int amount, Instant when) {
        return new ConstantUntil(amount, when);
    }

    private Integer amount;

    private Instant whenExpires;

    public ConstantUntil() {
    }

    public ConstantUntil(Integer amount, Instant whenExpires) {
        this.amount = amount;
        this.whenExpires = whenExpires;
    }

    @Override
    public Integer getAmountFor(Instant moment) {
        return !whenExpires.isBefore(moment) ? amount : 0;
    }

    @Override
    public ConstantUntil subtract(Integer amount, Instant moment) {
        if (getAmountFor(moment) < amount) {
            throw new IllegalArgumentException("Insufficient amount of miles");
        }
        return new ConstantUntil(this.amount - amount, whenExpires);
    }

    @Override
    public Instant expiresAt() {
        return whenExpires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ConstantUntil that = (ConstantUntil) o;
        return Objects.equals(amount, that.amount) && Objects.equals(whenExpires, that.whenExpires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, whenExpires);
    }
}
