package ru.rsue.girichev.authors.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.Map;

/** Единый формат ответа об ошибке. fields — ошибки по полям (только для 400). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(OffsetDateTime timestamp, int status, String error, String message, String path,
                       Map<String, String> fields) {
}
