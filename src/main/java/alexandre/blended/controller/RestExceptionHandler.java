package alexandre.blended.controller;

import alexandre.blended.exception.BadRequestException;
import alexandre.blended.exception.EmailException;
import alexandre.blended.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private Map<String, Object> createBody(HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        return body;
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        Map<String, Object> body = createBody(HttpStatus.NOT_FOUND);

        body.put("error", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({RuntimeException.class, BadRequestException.class, EmailException.class})
    protected ResponseEntity<Object> handleRuntime(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = createBody(HttpStatus.BAD_REQUEST);

        body.put("error", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}