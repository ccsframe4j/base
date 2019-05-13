package cc.creamcookie.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Jeongjae Eom
 * @since 2019-01-03
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = RestController.class)
public class CommonRestControllerAdvice extends ResponseEntityExceptionHandler {

    private ModelMapper modelMapper;

    public CommonRestControllerAdvice(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(ex.getBindingResult());
        return new ResponseEntity<>(ErrorResponse.builder().status(status).code(ex.getClass().getSimpleName()).message(ex.getMessage()).fieldError(fieldErrors).build(), status);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(ex.getBindingResult());
        return new ResponseEntity<>(ErrorResponse.builder().status(status).code(ex.getClass().getSimpleName()).message(ex.getMessage()).fieldError(fieldErrors).build(), status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return errorResponseEntity(status, ex);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request) {
        log.info("request.getUserPrincipal(): {}", request.getUserPrincipal());
        return errorResponseEntity(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    ResponseEntity<Object> handleIllegalArgumentExceptionException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return errorResponseEntity(status, ex);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> handleException(Exception ex, HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return errorResponseEntity(status, ex);
    }

    private ResponseEntity<Object> errorResponseEntity(HttpStatus status, Exception ex) {
        log.error("Error occurred", ex);
        return new ResponseEntity<>(ErrorResponse.builder().status(status).code(ex.getClass().getSimpleName()).message(ex.getMessage()).build(), status);
    }

    private List<ErrorResponse.FieldError> getFieldErrors(BindingResult bindingResult) {
        return modelMapper.map(bindingResult.getFieldErrors(), new TypeToken<List<ErrorResponse.FieldError>>() {
        }.getType());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        log.info("statusCode: {}", statusCode);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}