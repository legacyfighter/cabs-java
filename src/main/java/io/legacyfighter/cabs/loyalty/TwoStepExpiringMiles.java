package io.legacyfighter.cabs.loyalty;

import javax.persistence.Embeddable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
class TwoStepExpiringMiles implements Miles {

    private Integer amount;

    private Instant whenFirstHalfExpires;

    private Instant whenExpires;

    public TwoStepExpiringMiles() {
    }

    public TwoStepExpiringMiles(Integer amount, Instant whenFirstHalfExpires, Instant whenExpires) {
        this.amount = amount;
        this.whenFirstHalfExpires = whenFirstHalfExpires;
        this.whenExpires = whenExpires;
    }

    @Override
    public Integer getAmountFor(Instant moment) {
        if (!whenFirstHalfExpires.isBefore(moment)) {
            return amount;
        }
        if (!whenExpires.isBefore(moment)) {
            return amount - halfOf(amount);
        }
        return 0;
    }

    private Integer halfOf(Integer amount) {
        return amount / 2;
    }

    @Override
    public TwoStepExpiringMiles subtract(Integer amount, Instant moment) {
        Integer currentAmount = getAmountFor(moment);
        if (currentAmount < amount) {
            throw new IllegalArgumentException("Insufficient amount of miles");
        }
        return new TwoStepExpiringMiles(currentAmount - amount, whenFirstHalfExpires, whenExpires);
    }

    @Override
    public Instant expiresAt() {
        return whenExpires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TwoStepExpiringMiles that = (TwoStepExpiringMiles) o;
        return Objects.equals(amount, that.amount) && Objects.equals(whenFirstHalfExpires, that.whenFirstHalfExpires) && Objects.equals(whenExpires, that.whenExpires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, whenFirstHalfExpires, whenExpires);
    }

    @Override
    public String toString() {
        return "TwoStepExpiringMiles{" +
                "amount=" + amount +
                ", whenFirstHalfExpires=" + whenFirstHalfExpires +
                ", whenExpires=" + whenExpires +
                '}';
    }
}
