package ru.rsue.girichev.authors.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByEmailIgnoreCase(String email);

    /** Авторы книги: GET /api/v1/authors?bookId=3 */
    @Query("select distinct a from Author a join a.bookIds b where b = :bookId order by a.id")
    List<Author> findByBook(Long bookId);
}
