package ru.rsue.girichev.authors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.rsue.girichev.authors.api.AuthorRequest;
import ru.rsue.girichev.authors.domain.Author;
import ru.rsue.girichev.authors.domain.AuthorRepository;
import ru.rsue.girichev.authors.service.AuthorService;
import ru.rsue.girichev.authors.service.ConflictException;
import ru.rsue.girichev.authors.service.NotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Модульный тест бизнес-логики без Spring: репозиторий заменён заглушкой. */
@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    AuthorRepository repository;

    @InjectMocks
    AuthorService service;

    private final AuthorRequest request =
            new AuthorRequest("Новиков Пётр Ильич", "+7 (904) 555-66-77", "p.novikov@example.com", 3.9, Set.of(2L));

    @Test
    void duplicateEmailIsRejectedAndNothingIsSaved() {
        Author existing = new Author("Другой Автор", "+7 (900) 000-00-00", "p.novikov@example.com", 4.0, Set.of());
        ReflectionTestUtils.setField(existing, "id", 7L);
        when(repository.findByEmailIgnoreCase("p.novikov@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("id 7");
        verify(repository, never()).save(any());
    }

    @Test
    void updateOfMissingAuthorGivesNotFound() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(42, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Автор с id 42 не найден");
    }
}
