package ru.rsue.girichev.books;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.rsue.girichev.books.api.BookController;
import ru.rsue.girichev.books.domain.Binding;
import ru.rsue.girichev.books.domain.Book;
import ru.rsue.girichev.books.service.BookService;
import ru.rsue.girichev.books.service.NotFoundException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Тест веб-слоя: поднимается только контроллер, сервис заменён заглушкой (Mockito). */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BookService service;

    private static Book book(long id) {
        Book b = new Book("Ключ от маяка", Binding.PAPERBACK, "Северный ветер", 2023, "детектив");
        ReflectionTestUtils.setField(b, "id", id);
        return b;
    }

    @Test
    void bookIsReturnedWithHateoasLinks() throws Exception {
        when(service.get(2)).thenReturn(book(2));

        mvc.perform(get("/api/v1/books/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Ключ от маяка"))
                .andExpect(jsonPath("$.binding").value("PAPERBACK"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/books/2"))
                .andExpect(jsonPath("$._links.authors.href").value("http://localhost:8071/api/v1/authors?bookId=2"));
    }

    @Test
    void missingBookGives404InApiErrorFormat() throws Exception {
        when(service.get(99)).thenThrow(new NotFoundException("Книга с id 99 не найдена"));

        mvc.perform(get("/api/v1/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Книга с id 99 не найдена"))
                .andExpect(jsonPath("$.path").value("/api/v1/books/99"));
    }

    @Test
    void invalidBookIsRejectedBeforeService() throws Exception {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"binding\":\"HARDCOVER\",\"publisher\":\"АСТ\",\"publicationYear\":1200,\"genre\":\"роман\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.title").value("укажите название"))
                .andExpect(jsonPath("$.fields.publicationYear").value("год издания не раньше 1450"));
        verifyNoInteractions(service);
    }

    @Test
    void deleteGives204() throws Exception {
        mvc.perform(delete("/api/v1/books/5")).andExpect(status().isNoContent());
        verify(service).delete(5);
    }
}
