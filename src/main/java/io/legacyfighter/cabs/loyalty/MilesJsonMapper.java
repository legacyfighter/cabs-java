package io.legacyfighter.cabs.loyalty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

class MilesJsonMapper {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(FIELD, ANY);
        objectMapper.registerModule(new JavaTimeModule());
    }


    static Miles deserialize(String json) {
        try {
            return objectMapper.readValue(json, Miles.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static String serialize(Miles miles) {
        try {
            return objectMapper.writeValueAsString(miles);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
