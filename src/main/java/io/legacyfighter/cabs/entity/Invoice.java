package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Invoice extends BaseEntity {

    public Invoice() {

    }

    private BigDecimal amount;

    private String subjectName;

    public Invoice(BigDecimal amount, String subjectName) {
        this.amount = amount;
        this.subjectName = subjectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Invoice))
            return false;

        Invoice other = (Invoice) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
