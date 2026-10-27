package ru.rsue.girichev.authors.api;

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
import ru.rsue.girichev.authors.domain.Author;
import ru.rsue.girichev.authors.service.AuthorService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService service;
    /** Внешний адрес book-service: из автора можно перейти к каждой его книге. */
    private final String bookServiceUrl;

    public AuthorController(AuthorService service, @Value("${library.book-service-url}") String bookServiceUrl) {
        this.service = service;
        this.bookServiceUrl = bookServiceUrl;
    }

    @GetMapping
    public CollectionModel<EntityModel<Author>> list(@RequestParam(required = false) Long bookId) {
        return CollectionModel.of(service.findAll(bookId).stream().map(this::toModel).toList(),
                linkTo(methodOn(AuthorController.class).list(bookId)).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Author> get(@PathVariable long id) {
        return toModel(service.get(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Author>> create(@Valid @RequestBody AuthorRequest request) {
        EntityModel<Author> model = toModel(service.create(request));
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<Author> update(@PathVariable long id, @Valid @RequestBody AuthorRequest request) {
        return toModel(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Ссылки: сам автор, список авторов и по одной ссылке на каждую книгу автора в book-service. */
    private EntityModel<Author> toModel(Author author) {
        EntityModel<Author> model = EntityModel.of(author,
                linkTo(methodOn(AuthorController.class).get(author.getId())).withSelfRel(),
                linkTo(methodOn(AuthorController.class).list(null)).withRel("authors"));
        author.getBookIds().forEach(bookId -> model.add(Link.of(bookServiceUrl + "/api/v1/books/" + bookId, "books")));
        return model;
    }
}
