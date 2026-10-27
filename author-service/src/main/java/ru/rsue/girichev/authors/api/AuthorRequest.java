package ru.rsue.girichev.authors.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

/** Тело запросов POST и PUT. bookIds — идентификаторы книг из book-service (можно не указывать). */
public record AuthorRequest(
        @NotBlank(message = "укажите ФИО") @Size(max = 150) String fullName,
        @NotBlank(message = "укажите телефон")
        @Pattern(regexp = "\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}", message = "телефон в формате +7 (900) 123-45-67")
        String phone,
        @NotBlank(message = "укажите e-mail") @Email(message = "некорректный e-mail") String email,
        @NotNull(message = "укажите рейтинг")
        @DecimalMin(value = "0.0", message = "рейтинг от 0 до 5")
        @DecimalMax(value = "5.0", message = "рейтинг от 0 до 5") Double rating,
        Set<Long> bookIds) {
}
