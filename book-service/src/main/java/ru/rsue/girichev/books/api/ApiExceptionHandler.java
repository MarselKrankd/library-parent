package ru.rsue.girichev.books.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.rsue.girichev.books.service.NotFoundException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.TreeMap;

/** Перевод исключений в ответы с кодами 400 и 404. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException e, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, e.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> fields = new TreeMap<>();
        e.getFieldErrors().forEach(f -> fields.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Данные книги заполнены неверно", request, fields);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badValue(IllegalArgumentException e, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unreadable(HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Тело запроса не является корректным JSON книги", request, null);
    }

    private static ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request,
                                                  Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ApiError(OffsetDateTime.now().withNano(0), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI(), fields));
    }
}
