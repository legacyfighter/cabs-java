package io.legacyfighter.cabs.driverfleet;

import javax.persistence.*;

@Entity
public
class DriverAttribute {


    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverAttributeName name;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "DRIVER_ID")
    private Driver driver;

    DriverAttribute() {

    }

    public DriverAttribute(Driver driver, DriverAttributeName attr, String value) {
        this.driver = driver;
        this.value = value;
        this.name = attr;
    }

    DriverAttributeName getName() {
        return name;
    }

    void setName(DriverAttributeName name) {
        this.name = name;
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    Driver getDriver() {
        return driver;
    }

    void setDriver(Driver driver) {
        this.driver = driver;
    }
}
