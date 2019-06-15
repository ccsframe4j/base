package cc.creamcookie.config.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

/**
 * @author eomjeongjae
 * @since 2019-02-27
 */
public abstract class ObjectAttributeConverter<T> implements AttributeConverter<T, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    protected abstract Class<T> getInstance();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, getInstance());
        } catch (Exception e) {
        }
        return null;
    }
}