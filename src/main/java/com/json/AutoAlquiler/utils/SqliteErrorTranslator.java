package com.json.AutoAlquiler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqliteErrorTranslator {

    private static final Logger sqliteLogger = LoggerFactory.getLogger("sqlite.errors");
    private static final Map<String, String> CONCEPT_DICTIONARY = new HashMap<>();
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile("failed:\\s*([^\\)]+)");

    static {
        CONCEPT_DICTIONARY.put("identification", "Número de Identificación");
        CONCEPT_DICTIONARY.put("id_type", "Tipo de Documento");
        CONCEPT_DICTIONARY.put("phone", "Teléfono de Contacto");
        CONCEPT_DICTIONARY.put("email", "Correo Electrónico");
        CONCEPT_DICTIONARY.put("country_id", "País de Residencia");
        CONCEPT_DICTIONARY.put("location_id", "Datos de Ubicación");
    }

    public static String translate(Throwable ex) {
        sqliteLogger.error("Anomalía estructural en SQLite detectada: ", ex);
        
        String rawMessage = ex.getMessage();
        if (rawMessage == null) {
            return "No se pudo procesar la transacción debido a una inconsistencia de datos anónima.";
        }

        if (rawMessage.contains("SQLITE_CONSTRAINT_UNIQUE")) {
            return processUniqueConstraint(rawMessage);
        }
        if (rawMessage.contains("SQLITE_CONSTRAINT_NOTNULL")) {
            return processNotNullConstraint(rawMessage);
        }
        if (rawMessage.contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
            return "La operación no puede completarse porque el registro contiene dependencias activas en el sistema.";
        }
        if (rawMessage.contains("SQLITE_CONSTRAINT_CHECK")) {
            return "Los valores suministrados violan los criterios de validación lógicos de la base de datos.";
        }
        System.out.println("Error no mapeado: "+rawMessage);
        return "Error de integridad estructural al intentar escribir en el almacenamiento físico.";
    }

    private static String processUniqueConstraint(String rawMessage) {
        Matcher matcher = CONSTRAINT_PATTERN.matcher(rawMessage);
        if (matcher.find()) {
            String[] technicalTokens = matcher.group(1).split(",");
            StringBuilder cleanConcepts = new StringBuilder();
            
            for (int i = 0; i < technicalTokens.length; i++) {
                String token = technicalTokens[i].trim();
                if (token.contains(".")) {
                    token = token.substring(token.lastIndexOf(".") + 1);
                }
                
                cleanConcepts.append(CONCEPT_DICTIONARY.getOrDefault(token, token));
                if (i < technicalTokens.length - 1) {
                    cleanConcepts.append(" en conjunto con ");
                }
            }
            return "Ya existe un registro en el sistema con el mismo valor para: " + cleanConcepts.toString() + ".";
        }
        return "El registro que intenta almacenar ya se encuentra duplicado en el sistema.";
    }

    private static String processNotNullConstraint(String rawMessage) {
        Matcher matcher = CONSTRAINT_PATTERN.matcher(rawMessage);
        if (matcher.find()) {
            String token = matcher.group(1).trim();
            if (token.contains(".")) {
                token = token.substring(token.lastIndexOf(".") + 1);
            }
            return "El campo '" + CONCEPT_DICTIONARY.getOrDefault(token, token) + "' es obligatorio y requiere un valor válido.";
        }
        return "Se omitió un parámetro estructural requerido por la base de datos.";
    }
}