package cc.creamcookie.config.hibernate;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Converter;
import java.util.List;
import java.util.Map;

/**
 * @author eomjeongjae
 * @since 2019-02-27
 */
@Slf4j
@Converter(autoApply = true)
public class ListAttributeConverter extends ObjectAttributeConverter<List> {

    @Override
    protected Class<List> getInstance() {
        return List.class;
    }

}

