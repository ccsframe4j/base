package cc.creamcookie.config.hibernate;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Converter;
import java.util.Map;

/**
 * @author eomjeongjae
 * @since 2019-02-27
 */
@Slf4j
@Converter(autoApply = true)
public class MapAttributeConverter extends ObjectAttributeConverter<Map> {

    @Override
    protected Class<Map> getInstance() {
        return Map.class;
    }

}
