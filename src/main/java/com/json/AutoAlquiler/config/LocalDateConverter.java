package com.json.AutoAlquiler.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Converter(autoApply = true) // hace que afecte a todos los LocalDate del proyecto automáticamente
public class LocalDateConverter implements AttributeConverter<LocalDate, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public LocalDate convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        // Toma los milisegundos UNIX de la BD y los reconstruye limpiamente en un LocalDate de Java
        return Instant.ofEpochMilli(dbData).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
