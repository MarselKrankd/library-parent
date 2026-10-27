package ru.rsue.girichev.authors.service;

/** Нарушена уникальность (e-mail уже занят) — контроллер вернёт 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
