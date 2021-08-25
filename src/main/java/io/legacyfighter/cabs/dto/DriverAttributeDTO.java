package io.legacyfighter.cabs.dto;

import io.legacyfighter.cabs.entity.DriverAttribute;

import java.util.Objects;

public class DriverAttributeDTO {

    private DriverAttribute.DriverAttributeName name;

    private String value;

    public DriverAttributeDTO(DriverAttribute driverAttribute) {
        this.name = driverAttribute.getName();
        this.value = driverAttribute.getValue();
    }

    public DriverAttributeDTO(DriverAttribute.DriverAttributeName name, String value) {
        this.name = name;
        this.value = value;
    }

    DriverAttributeDTO() {

    }

    public DriverAttribute.DriverAttributeName getName() {
        return name;
    }

    public void setName(DriverAttribute.DriverAttributeName name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DriverAttributeDTO that = (DriverAttributeDTO) o;
        return name == that.name && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
