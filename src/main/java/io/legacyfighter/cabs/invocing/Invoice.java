package io.legacyfighter.cabs.invocing;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
class Invoice extends BaseEntity {

    private BigDecimal amount;

    private String subjectName;

    Invoice(BigDecimal amount, String subjectName) {
        this.amount = amount;
        this.subjectName = subjectName;
    }

    Invoice() {

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
