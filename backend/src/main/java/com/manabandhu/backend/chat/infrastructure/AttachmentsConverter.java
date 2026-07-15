package com.manabandhu.backend.chat.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter
public class AttachmentsConverter implements AttributeConverter<List<com.manabandhu.backend.chat.domain.MessageAttachment>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<com.manabandhu.backend.chat.domain.MessageAttachment> attribute) {
        if (attribute == null || attribute.isEmpty()) return "[]";
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<com.manabandhu.backend.chat.domain.MessageAttachment> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || dbData.equals("[]")) return List.of();
        try {
            return MAPPER.readValue(dbData, new TypeReference<List<com.manabandhu.backend.chat.domain.MessageAttachment>>() {});
        } catch (IOException e) {
            return List.of();
        }
    }
}
