package ru.rsue.girichev.books.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    /** Поиск по жанру без учёта регистра: GET /api/v1/books?genre=фантастика */
    List<Book> findByGenreIgnoreCaseOrderById(String genre);
}
