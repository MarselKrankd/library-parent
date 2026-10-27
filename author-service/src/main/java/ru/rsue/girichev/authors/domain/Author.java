package ru.rsue.girichev.authors.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.TreeSet;

/**
 * Автор — таблица 2 варианта 1: ФИО, телефон, e-mail, рейтинг.
 * bookIds — идентификаторы книг из book-service (у книги может быть несколько авторов,
 * у автора — несколько книг). Таблицы книг здесь нет: каждый микросервис владеет своими данными.
 */
@Entity
@Table(name = "author")
@Relation(itemRelation = "author", collectionRelation = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private double rating;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "author_book", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "book_id")
    private Set<Long> bookIds = new TreeSet<>();

    protected Author() {
        // нужен JPA
    }

    public Author(String fullName, String phone, String email, double rating, Set<Long> bookIds) {
        change(fullName, phone, email, rating, bookIds);
    }

    public void change(String fullName, String phone, String email, double rating, Set<Long> bookIds) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.rating = rating;
        this.bookIds.clear();
        if (bookIds != null) {
            this.bookIds.addAll(bookIds);
        }
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getRating() { return rating; }
    /** В порядке возрастания — для стабильного JSON. */
    public Set<Long> getBookIds() { return new TreeSet<>(bookIds); }
}
