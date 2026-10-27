package ru.rsue.girichev.books.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.rsue.girichev.books.domain.Binding;

/** Тело запросов POST и PUT: id не передаётся — его назначает база данных. */
public record BookRequest(
        @NotBlank(message = "укажите название") @Size(max = 200) String title,
        @NotNull(message = "укажите переплёт: HARDCOVER или PAPERBACK") Binding binding,
        @NotBlank(message = "укажите издательство") String publisher,
        @NotNull(message = "укажите год издания") @Min(value = 1450, message = "год издания не раньше 1450") Integer publicationYear,
        @NotBlank(message = "укажите жанр") String genre) {
}
