package ru.rsue.girichev.authors;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Интеграционный тест: приложение запускается целиком на случайном порту, запросы идут по HTTP. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorApiIntegrationTest {

    @Autowired
    TestRestTemplate http;

    private static HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    @Test
    void authorsOfBookAreFilteredAndLinkedToBookService() {
        String body = http.getForObject("/api/v1/authors?bookId=3", String.class);

        List<String> names = JsonPath.read(body, "$._embedded.authors[*].fullName");
        assertThat(names).containsExactly("Орлов Максим Андреевич", "Соколова Анна Викторовна");
        List<String> bookLinks = JsonPath.read(body, "$._embedded.authors[1]._links.books[*].href");
        assertThat(bookLinks).containsExactly("http://localhost:8070/api/v1/books/3", "http://localhost:8070/api/v1/books/4");
    }

    @Test
    void fullCrudCycleOverHttp() {
        String newAuthor = """
                {"fullName":"Новиков Пётр Ильич","phone":"+7 (904) 555-66-77",
                 "email":"p.novikov@example.com","rating":3.9,"bookIds":[2]}""";
        ResponseEntity<String> created = http.postForEntity("/api/v1/authors", json(newAuthor), String.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getHeaders().getLocation()).isNotNull();
        int id = JsonPath.read(created.getBody(), "$.id");

        ResponseEntity<String> updated = http.exchange("/api/v1/authors/" + id, HttpMethod.PUT,
                json(newAuthor.replace("3.9", "4.4")), String.class);
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Double) JsonPath.read(updated.getBody(), "$.rating")).isEqualTo(4.4);

        ResponseEntity<String> conflict = http.postForEntity("/api/v1/authors", json(newAuthor), String.class);
        assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<Void> deleted = http.exchange("/api/v1/authors/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(http.getForEntity("/api/v1/authors/" + id, String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void wrongPhoneAndRatingAreRejected() {
        String bad = """
                {"fullName":"Тест","phone":"89001234567","email":"t@example.com","rating":7}""";
        ResponseEntity<String> response = http.postForEntity("/api/v1/authors", json(bad), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat((String) JsonPath.read(response.getBody(), "$.fields.phone")).isEqualTo("телефон в формате +7 (900) 123-45-67");
        assertThat((String) JsonPath.read(response.getBody(), "$.fields.rating")).isEqualTo("рейтинг от 0 до 5");
    }
}
