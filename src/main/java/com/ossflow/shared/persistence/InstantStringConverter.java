package com.ossflow.shared.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;

@Converter(autoApply = true)
public class InstantStringConverter implements AttributeConverter<Instant, String> {

    @Override
    public String convertToDatabaseColumn(Instant instant) {
        return instant == null ? null : instant.toString();
    }

    @Override
    public Instant convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) return null;
        // SQLite CURRENT_TIMESTAMP produces "YYYY-MM-DD HH:MM:SS", ISO 8601 needs "T" and "Z"
        String normalized = value.replace(' ', 'T');
        if (!normalized.endsWith("Z") && !normalized.contains("+")) normalized += "Z";
        return Instant.parse(normalized);
    }
}
