package io.legacyfighter.cabs.crm;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Client extends BaseEntity {

    public enum Type {
        NORMAL, VIP
    }

    public enum ClientType {
        INDIVIDUAL, COMPANY
    }

    public enum PaymentType {
        PRE_PAID, POST_PAID, MONTHLY_INVOICE
    }

    private Type type;

    private String name;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private PaymentType defaultPaymentType;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    public Client() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public PaymentType getDefaultPaymentType() {
        return defaultPaymentType;
    }

    public void setDefaultPaymentType(PaymentType defaultPaymentType) {
        this.defaultPaymentType = defaultPaymentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Client))
            return false;

        Client other = (Client) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}