package ru.rsue.girichev.authors.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsue.girichev.authors.api.AuthorRequest;
import ru.rsue.girichev.authors.domain.Author;
import ru.rsue.girichev.authors.domain.AuthorRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authors;

    public AuthorService(AuthorRepository authors) {
        this.authors = authors;
    }

    public List<Author> findAll(Long bookId) {
        return bookId == null ? authors.findAll(Sort.by("id")) : authors.findByBook(bookId);
    }

    public Author get(long id) {
        return authors.findById(id)
                .orElseThrow(() -> new NotFoundException("Автор с id " + id + " не найден"));
    }

    @Transactional
    public Author create(AuthorRequest r) {
        requireFreeEmail(r.email(), null);
        return authors.save(new Author(r.fullName(), r.phone(), r.email(), r.rating(), r.bookIds()));
    }

    @Transactional
    public Author update(long id, AuthorRequest r) {
        Author author = get(id);
        requireFreeEmail(r.email(), id);
        author.change(r.fullName(), r.phone(), r.email(), r.rating(), r.bookIds());
        return author;   // сохранится при фиксации транзакции
    }

    @Transactional
    public void delete(long id) {
        authors.delete(get(id));
    }

    private void requireFreeEmail(String email, Long ownId) {
        authors.findByEmailIgnoreCase(email)
                .filter(other -> !other.getId().equals(ownId))
                .ifPresent(other -> {
                    throw new ConflictException("E-mail " + email + " уже указан у автора с id " + other.getId());
                });
    }
}
