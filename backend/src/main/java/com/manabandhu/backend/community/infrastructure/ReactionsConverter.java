package com.manabandhu.backend.community.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Converter
public class ReactionsConverter implements AttributeConverter<Map<String, Integer>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Integer> attribute) {
        if (attribute == null) return "{}";
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "{\"like\":0,\"helpful\":0,\"celebrate\":0,\"support\":0}";
        }
    }

    @Override
    public Map<String, Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Map.of("like", 0, "helpful", 0, "celebrate", 0, "support", 0);
        }
        try {
            return MAPPER.readValue(dbData, new TypeReference<Map<String, Integer>>() {});
        } catch (IOException e) {
            return Map.of("like", 0, "helpful", 0, "celebrate", 0, "support", 0);
        }
    }
}
