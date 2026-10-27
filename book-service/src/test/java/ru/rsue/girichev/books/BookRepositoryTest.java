package ru.rsue.girichev.books;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.rsue.girichev.books.domain.Book;
import ru.rsue.girichev.books.domain.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;

/** Тест слоя данных: H2 с начальными данными из data.sql. */
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    BookRepository books;

    @Test
    void seedDataIsLoaded() {
        assertThat(books.count()).isEqualTo(5);
    }

    @Test
    void genreSearchIgnoresCase() {
        assertThat(books.findByGenreIgnoreCaseOrderById("ФАНТАСТИКА"))
                .extracting(Book::getTitle)
                .containsExactly("Сорок дней на Марсе");
    }
}
