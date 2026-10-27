package ru.rsue.girichev.books.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsue.girichev.books.api.BookRequest;
import ru.rsue.girichev.books.domain.Book;
import ru.rsue.girichev.books.domain.BookRepository;

import java.time.Year;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository books;

    public BookService(BookRepository books) {
        this.books = books;
    }

    public List<Book> findAll(String genre) {
        return genre == null || genre.isBlank()
                ? books.findAll(Sort.by("id"))
                : books.findByGenreIgnoreCaseOrderById(genre);
    }

    public Book get(long id) {
        return books.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга с id " + id + " не найдена"));
    }

    @Transactional
    public Book create(BookRequest r) {
        checkYear(r.publicationYear());
        return books.save(new Book(r.title(), r.binding(), r.publisher(), r.publicationYear(), r.genre()));
    }

    @Transactional
    public Book update(long id, BookRequest r) {
        checkYear(r.publicationYear());
        Book book = get(id);
        book.change(r.title(), r.binding(), r.publisher(), r.publicationYear(), r.genre());
        return book;   // сохранится при фиксации транзакции
    }

    @Transactional
    public void delete(long id) {
        books.delete(get(id));
    }

    /** Год издания не может быть больше текущего. */
    private static void checkYear(int year) {
        if (year > Year.now().getValue()) {
            throw new IllegalArgumentException("Год издания " + year + " ещё не наступил");
        }
    }
}
