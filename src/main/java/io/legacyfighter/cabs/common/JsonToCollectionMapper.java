package io.legacyfighter.cabs.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

public class JsonToCollectionMapper {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static Set<Long> deserialize(String json) {
        if (json == null) {
            return new HashSet<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(Set.class, Long.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serialize(Set<Long> collectionOfIds) {
        try {
            return objectMapper.writeValueAsString(collectionOfIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
