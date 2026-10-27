INSERT INTO author (full_name, phone, email, rating) VALUES ('Лебедева Ольга Николаевна', '+7 (900) 111-22-33', 'o.lebedeva@example.com', 4.6);
INSERT INTO author (full_name, phone, email, rating) VALUES ('Карпенко Игорь Сергеевич', '+7 (901) 222-33-44', 'i.karpenko@example.com', 4.1);
INSERT INTO author (full_name, phone, email, rating) VALUES ('Орлов Максим Андреевич', '+7 (902) 333-44-55', 'm.orlov@example.com', 4.8);
INSERT INTO author (full_name, phone, email, rating) VALUES ('Соколова Анна Викторовна', '+7 (903) 444-55-66', 'a.sokolova@example.com', 4.3);

-- связи «автор — книга» (идентификаторы книг из book-service)
INSERT INTO author_book (author_id, book_id) VALUES (1, 1);
INSERT INTO author_book (author_id, book_id) VALUES (1, 5);
INSERT INTO author_book (author_id, book_id) VALUES (2, 2);
INSERT INTO author_book (author_id, book_id) VALUES (3, 3);
INSERT INTO author_book (author_id, book_id) VALUES (4, 3);
INSERT INTO author_book (author_id, book_id) VALUES (4, 4);
