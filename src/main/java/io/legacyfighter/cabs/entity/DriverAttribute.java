package io.legacyfighter.cabs.entity;

import javax.persistence.*;

@Entity
public class DriverAttribute {


    public enum DriverAttributeName {
        PENALTY_POINTS, NATIONALITY, YEARS_OF_EXPERIENCE, MEDICAL_EXAMINATION_EXPIRATION_DATE , MEDICAL_EXAMINATION_REMARKS, EMAIL, BIRTHPLACE, COMPANY_NAME
    }

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

    public DriverAttribute() {

    }

    public DriverAttribute(Driver driver, DriverAttributeName attr, String value) {
        this.driver = driver;
        this.value = value;
        this.name = attr;
    }

    public DriverAttributeName getName() {
        return name;
    }

    public void setName(DriverAttributeName name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
