package cc.creamcookie.utils;

import org.springframework.http.MediaType;

import java.util.List;

/**
 * @author eomjeongjae
 * @since 2019-05-03
 */
public class Utils {

    public static boolean isJsonProducesRequest(String accept) {
        List<MediaType> mediaTypes = MediaType.parseMediaTypes(accept);
        MediaType mediaType = mediaTypes.stream().findFirst().orElse(MediaType.ALL);
        if (!MediaType.ALL.getType().equals(mediaType.getType()) && mediaType.isCompatibleWith(MediaType.APPLICATION_JSON_UTF8)) {
            return true;
        }
        return false;
    }

}
