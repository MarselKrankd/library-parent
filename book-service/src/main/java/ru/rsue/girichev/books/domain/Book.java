package ru.rsue.girichev.books.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.hateoas.server.core.Relation;

/** Книга — таблица 1 варианта 1: название, переплёт, издательство, год издания, жанр. */
@Entity
@Table(name = "book")
@Relation(itemRelation = "book", collectionRelation = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Binding binding;

    @Column(nullable = false)
    private String publisher;

    @Column(name = "publication_year", nullable = false)
    private int publicationYear;

    @Column(nullable = false)
    private String genre;

    protected Book() {
        // нужен JPA
    }

    public Book(String title, Binding binding, String publisher, int publicationYear, String genre) {
        change(title, binding, publisher, publicationYear, genre);
    }

    public void change(String title, Binding binding, String publisher, int publicationYear, String genre) {
        this.title = title;
        this.binding = binding;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.genre = genre;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Binding getBinding() { return binding; }
    public String getPublisher() { return publisher; }
    public int getPublicationYear() { return publicationYear; }
    public String getGenre() { return genre; }
}
