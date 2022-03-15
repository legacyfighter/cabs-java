package io.legacyfighter.cabs.crm;

public class ClientDTO  {

    private Long id;

    private Client.Type type;

    private String name;

    private String lastName;

    private Client.PaymentType defaultPaymentType;

    private Client.ClientType clientType;

    private Integer numberOfClaims;

    public ClientDTO() {

    }

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.type = client.getType();
        this.name = client.getName();
        this.lastName = client.getLastName();
        this.defaultPaymentType = client.getDefaultPaymentType();
        this.clientType = client.getClientType();
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

    public Client.ClientType getClientType() {
        return clientType;
    }

    public void setClientType(Client.ClientType clientType) {
        this.clientType = clientType;
    }

    public Client.Type getType() {
        return type;
    }

    public void setType(Client.Type type) {
        this.type = type;
    }

    public Client.PaymentType getDefaultPaymentType() {
        return defaultPaymentType;
    }

    public void setDefaultPaymentType(Client.PaymentType defaultPaymentType) {
        this.defaultPaymentType = defaultPaymentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}