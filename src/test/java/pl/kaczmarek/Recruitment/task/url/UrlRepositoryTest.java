package pl.kaczmarek.Recruitment.task.url;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    private final String id = "shortId";
    private final String longUrl = "https://example.com";

    @BeforeEach
    void setUp() {
        final var urlEntity = new UrlEntity();
        urlEntity.setId(id);
        urlEntity.setLongUrl(longUrl);
        urlEntity.setExpirationDate(LocalDateTime.now().plusDays(1));
        urlRepository.save(urlEntity);
    }

    @Test
    void shouldFindById() {
        final var foundEntity = urlRepository.findById(id);

        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getId()).isEqualTo(id);
        assertThat(foundEntity.get().getLongUrl()).isEqualTo(longUrl);
    }

    @Test
    void shouldNotFindByIdIfNotExists() {
        final var foundEntity = urlRepository.findById("nonExistentId");

        assertThat(foundEntity).isNotPresent();
    }

    @Test
    void shouldDeleteById() {
        urlRepository.deleteById(id);

        final var foundEntity = urlRepository.findById(id);

        assertThat(foundEntity).isNotPresent();
    }

    @Test
    void shouldNotThrowExceptionWhenDeletingNonExistentId() {
        urlRepository.deleteById("nonExistentId");

        final var foundEntity = urlRepository.findById(id);

        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getId()).isEqualTo(id);
    }

}
