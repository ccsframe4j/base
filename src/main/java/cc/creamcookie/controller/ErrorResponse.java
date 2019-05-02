package cc.creamcookie.controller;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jeongjae Eom
 * @since 2019-01-03
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ErrorResponse {

    private HttpStatus status;

    private String code;

    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private List<FieldError> fieldError;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FieldError {
        private String objectName;
        private String field;
        private Object rejectedValue;
        private String defaultMessage;
    }

}