package io.legacyfighter.cabs.dto;


import io.legacyfighter.cabs.entity.DriverAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverReport {

    private DriverDTO driverDTO;

    private List<DriverAttributeDTO> attributes = new ArrayList<>();

    private Map<DriverSessionDTO, List<TransitDTO>> sessions = new HashMap<>();

    public DriverDTO getDriverDTO() {
        return driverDTO;
    }

    public void setDriverDTO(DriverDTO driverDTO) {
        this.driverDTO = driverDTO;
    }

    public List<DriverAttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DriverAttributeDTO> attributes) {
        this.attributes = attributes;
    }

    public Map<DriverSessionDTO, List<TransitDTO>> getSessions() {
        return sessions;
    }

    public void setSessions(Map<DriverSessionDTO, List<TransitDTO>> sessions) {
        this.sessions = sessions;
    }

    public void addAttr(DriverAttribute.DriverAttributeName name, String value) {
        attributes.add(new DriverAttributeDTO(name, value));
    }
}

