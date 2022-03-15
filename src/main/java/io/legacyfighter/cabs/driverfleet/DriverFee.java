package io.legacyfighter.cabs.driverfleet;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.money.Money;

import javax.persistence.*;

@Entity
public class DriverFee extends BaseEntity {

    public enum FeeType {
        FLAT, PERCENTAGE
    }

    public DriverFee() {

    }

    public DriverFee(FeeType feeType, Driver driver, Integer amount, Integer min) {
        this.feeType = feeType;
        this.driver = driver;
        this.amount = amount;
        this.min = new Money(min);
    }

    @Column(nullable = false)
    private FeeType feeType;

    @OneToOne
    private Driver driver;

    @Column(nullable = false)
    private Integer amount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="min")),
    })
    private Money min;

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Money getMin() {
        return min;
    }

    public void setMin(Money min) {
        this.min = min;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DriverFee))
            return false;

        DriverFee other = (DriverFee) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
