package pl.kaczmarek.Recruitment.task.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LastGeneratedIdRepositoryTest {

    @Autowired
    private LastGeneratedIdRepository repository;

    private LastGeneratedId lastGeneratedId;

    @BeforeEach
    void setUp() {
        lastGeneratedId = new LastGeneratedId(1L, "ID1234");
        repository.save(lastGeneratedId);
    }

    @Test
    void shouldFindById() {
        final var found = repository.findById(lastGeneratedId.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLastId()).isEqualTo(lastGeneratedId.getLastId());
    }

    @Test
    void shouldNotFindByIdIfNotExists() {
        final var found = repository.findById(999L);

        assertThat(found).isNotPresent();
    }

    @Test
    void shouldSaveEntity() {
        final var newId = new LastGeneratedId(2L, "ID5678");
        repository.save(newId);

        final var found = repository.findById(newId.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLastId()).isEqualTo("ID5678");
    }

    @Test
    void shouldUpdateEntity() {
        lastGeneratedId.setLastId("ID9999");
        repository.save(lastGeneratedId);

        final var found = repository.findById(lastGeneratedId.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLastId()).isEqualTo("ID9999");
    }

    @Test
    void shouldDeleteById() {
        repository.deleteById(lastGeneratedId.getId());

        final var found = repository.findById(lastGeneratedId.getId());

        assertThat(found).isNotPresent();
    }

    @Test
    void shouldNotThrowExceptionWhenDeletingNonExistentId() {
        assertThatCode(this::deleteById).doesNotThrowAnyException();
    }

    private void deleteById() {
        repository.deleteById(999L);
    }
}
