package pl.kaczmarek.Recruitment.task.common;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IdGeneratorTest {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private LastGeneratedIdRepository lastGeneratedIdRepository;

    @Mock
    private UniqueIdChecker uniqueIdChecker;

    @BeforeEach
    void setUp() {
        lastGeneratedIdRepository.deleteAll();
    }

    @Nested
    class GenerateUniqueIdTest {

        @Test
        void shouldGenerateNewIdWhenNoLastGeneratedIdExists() {
            final var newId = idGenerator.generateUniqueId();

            assertThat(newId).isEqualTo("B");
            final var savedId = lastGeneratedIdRepository.findById(1L);
            assertThat(savedId).isPresent();
            assertThat(savedId.get().getLastId()).isEqualTo("B");
        }

        @Test
        void shouldGenerateNewIdWhenLastGeneratedIdExistsAndIsUnique() {
            final var lastGeneratedId = new LastGeneratedId(1L, "B");
            lastGeneratedIdRepository.save(lastGeneratedId);

            final var newId = idGenerator.generateUniqueId();

            assertThat(newId).isEqualTo("C");
            final var savedId = lastGeneratedIdRepository.findById(1L);
            assertThat(savedId).isPresent();
            assertThat(savedId.get().getLastId()).isEqualTo("C");
        }

        @Test
        void shouldGenerateNewIdWhenLastGeneratedIdIsNotUnique() {
            final var lastGeneratedId = new LastGeneratedId(1L, "A");
            lastGeneratedIdRepository.save(lastGeneratedId);

            final var newId = idGenerator.generateUniqueId();

            assertThat(newId).isEqualTo("B");
            final var savedId = lastGeneratedIdRepository.findById(1L);
            assertThat(savedId).isPresent();
            assertThat(savedId.get().getLastId()).isEqualTo("B");
        }

        @Test
        void shouldHandleMultipleCharacterIdsCorrectly() {
            final var lastGeneratedId = new LastGeneratedId(1L, "9Z");
            lastGeneratedIdRepository.save(lastGeneratedId);

            final var newId = idGenerator.generateUniqueId();

            assertThat(newId).isEqualTo("9a");
            final var savedId = lastGeneratedIdRepository.findById(1L);
            assertThat(savedId).isPresent();
            assertThat(savedId.get().getLastId()).isEqualTo("9a");
        }

        @Test
        void shouldHandleIdOverflowCorrectly() {
            final var lastGeneratedId = new LastGeneratedId(1L, "9z");
            lastGeneratedIdRepository.save(lastGeneratedId);

            final var newId = idGenerator.generateUniqueId();

            assertThat(newId).isEqualTo("90");
            final var savedId = lastGeneratedIdRepository.findById(1L);
            assertThat(savedId).isPresent();
            assertThat(savedId.get().getLastId()).isEqualTo("90");
        }
    }

    @Nested
    class IsUrlAlreadyAddedTest {

        @Test
        void shouldReturnFalseWhenUrlDoesNotExist() {
            when(uniqueIdChecker.existsById("newId")).thenReturn(false);

            boolean result = idGenerator.isUrlAlreadyAdded("newId");

            assertThat(result).isFalse();
        }
    }
    @Nested
    class IncrementIdTest {

        @Test
        void shouldIncrementSingleCharacterId() {
            final var id = "A";

            final var newId = idGenerator.incrementId(id);

            assertThat(newId).isEqualTo("B");
        }

        @Test
        void shouldIncrementMultipleCharacterId() {
            final var id = "AZ";

            final var newId = idGenerator.incrementId(id);

            assertThat(newId).isEqualTo("Aa");
        }

        @Test
        void shouldWrapAroundAlphabet() {
            final var id = "z";

            final var newId = idGenerator.incrementId(id);

            assertThat(newId).isEqualTo("0");
        }

        @Test
        void shouldHandleOverflowCorrectly() {
            final var id = "9z";

            final var newId = idGenerator.incrementId(id);

            assertThat(newId).isEqualTo("90");
        }
    }
}
