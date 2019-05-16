package cc.creamcookie.config.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

/**
 * @author eomjeongjae
 * @since 2019-02-27
 */
@Slf4j
@Converter(autoApply = true)
public class ListAttributeConverter implements AttributeConverter<List<Object>, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }
        return null;
    }

    @Override
    public List<Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, List.class) : null;
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }
        return null;
    }
}
