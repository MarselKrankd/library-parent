package ru.rsue.girichev.books.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.rsue.girichev.books.domain.Book;
import ru.rsue.girichev.books.service.BookService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService service;
    /** Внешний адрес author-service: из книги можно перейти к её авторам. */
    private final String authorServiceUrl;

    public BookController(BookService service, @Value("${library.author-service-url}") String authorServiceUrl) {
        this.service = service;
        this.authorServiceUrl = authorServiceUrl;
    }

    @GetMapping
    public CollectionModel<EntityModel<Book>> list(@RequestParam(required = false) String genre) {
        return CollectionModel.of(service.findAll(genre).stream().map(this::toModel).toList(),
                linkTo(methodOn(BookController.class).list(genre)).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Book> get(@PathVariable long id) {
        return toModel(service.get(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Book>> create(@Valid @RequestBody BookRequest request) {
        EntityModel<Book> model = toModel(service.create(request));
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<Book> update(@PathVariable long id, @Valid @RequestBody BookRequest request) {
        return toModel(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Гиперссылки HATEOAS: сама книга, весь каталог и авторы книги в соседнем микросервисе. */
    private EntityModel<Book> toModel(Book book) {
        return EntityModel.of(book,
                linkTo(methodOn(BookController.class).get(book.getId())).withSelfRel(),
                linkTo(methodOn(BookController.class).list(null)).withRel("books"),
                Link.of(authorServiceUrl + "/api/v1/authors?bookId=" + book.getId(), "authors"));
    }
}
