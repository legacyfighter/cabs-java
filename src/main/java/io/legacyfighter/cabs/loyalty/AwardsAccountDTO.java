package io.legacyfighter.cabs.loyalty;

import io.legacyfighter.cabs.crm.ClientDTO;

import java.time.Instant;

public class AwardsAccountDTO {


    private ClientDTO client;

    private Instant date;

    private Boolean isActive;

    private Integer transactions;

    public AwardsAccountDTO() {
    }

    public AwardsAccountDTO(AwardsAccount account, ClientDTO clientDTO) {
        this.isActive = account.isActive();
        this.client = clientDTO;
        this.transactions = account.getTransactions();
        this.date = account.getDate();
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean isActive() {
        return isActive;
    }

    public Integer getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this. transactions = transactions;
    }

    public Instant getDate() {
        return date;
    }

    public Boolean getActive() {
        return isActive;
    }
}
