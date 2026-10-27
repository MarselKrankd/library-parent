package ru.rsue.girichev.authors.service;

/** Запрошенной записи нет — контроллер вернёт 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
