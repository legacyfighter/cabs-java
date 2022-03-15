package io.legacyfighter.cabs.loyalty;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConstantUntil.class, name = "Expiring"),
        @JsonSubTypes.Type(value = TwoStepExpiringMiles.class, name = "TwoStep")
})
interface Miles {
    Integer getAmountFor(Instant moment);

    Miles subtract(Integer amount, Instant moment);

    Instant expiresAt();
}
