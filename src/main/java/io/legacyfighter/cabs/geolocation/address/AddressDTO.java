package io.legacyfighter.cabs.geolocation.address;

public class AddressDTO  {

    public AddressDTO() {

    }

    public AddressDTO(Address a) {
        country = a.getCountry();
        district = a.getDistrict();
        city = a.getCity();
        street = a.getStreet();
        buildingNumber = a.getBuildingNumber();
        additionalNumber = a.getAdditionalNumber();
        postalCode = a.getPostalCode();
        name = a.getName();
        hash = a.getHash();
    }

    private String country;

    private String district;

    private String city;

    private String street;

    private Integer buildingNumber;

    private Integer additionalNumber;

    private String postalCode;

    private String name;

    private Integer hash;

    public AddressDTO(String country, String city, String street, Integer buildingNumber) {
        this.country = country;
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

    public Integer getHash() {
        return hash;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    public Address toAddressEntity() {
        Address address = new Address();
        address.setAdditionalNumber(this.getAdditionalNumber());
        address.setBuildingNumber(this.getBuildingNumber());
        address.setCity(this.getCity());
        address.setName(this.getName());
        address.setStreet(this.getStreet());
        address.setCountry(this.getCountry());
        address.setPostalCode(this.getPostalCode());
        address.setDistrict(this.getDistrict());
        return address;
    }
}
