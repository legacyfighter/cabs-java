package io.legacyfighter.cabs.geolocation.address;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class Address extends BaseEntity {

    public Address() {

    }

    private String country;

    private String district;

    private String city;

    private String street;

    private Integer buildingNumber;

    private Integer additionalNumber;

    private String postalCode;

    private String name;

    @Column(unique=true)
    private Integer hash;

    public Address(String country, String city, String street, int buildingNumber) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    public Address(String country, String district, String city, String street, int buildingNumber) {
        this.country = country;
        this.district = district;
        this.city = city;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(Integer buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public Integer getAdditionalNumber() {
        return additionalNumber;
    }

    public void setAdditionalNumber(Integer additionalNumber) {
        this.additionalNumber = additionalNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void hash() {
        hash = Objects.hash(country, district, city, street, buildingNumber, additionalNumber, postalCode, name);
    }

    public Integer getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id='" + getId() + '\'' +
                ", country='" + country + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", buildingNumber=" + buildingNumber +
                ", additionalNumber=" + additionalNumber +
                ", postalCode='" + postalCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Address))
            return false;

        Address other = (Address) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
