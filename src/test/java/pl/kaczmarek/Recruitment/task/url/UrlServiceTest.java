package pl.kaczmarek.Recruitment.task.url;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.kaczmarek.Recruitment.task.common.IdGenerator;

@SpringBootTest
class UrlServiceTest {

    @InjectMocks
    @Spy
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private IdGenerator idGenerator;

    @Nested
    class CreateShortUrlTest {

        private final String id = "shortId";
        private final String longUrl = "https://example.com";
        private final UrlRequest urlRequest = new UrlRequest(id, longUrl, null);
        private final UrlEntity urlEntity = new UrlEntity();
        private final UrlResponse urlResponse = new UrlResponse(id, longUrl, "http://localhost:8080/" + id);

        @BeforeEach
        void setUp() {
            urlEntity.setId(id);
            urlEntity.setLongUrl(longUrl);
            when(urlRepository.existsById(id)).thenReturn(false);
            when(urlRepository.save(any())).thenReturn(urlEntity);
        }

        @Test
        void shouldCallCheckIfUrlAlreadyExists() {
            callService();

            verify(urlService).checkIfUrlAlreadyExists(any());
        }

        @Test
        void shouldCallCreateUrlEntity() {
            when(idGenerator.generateUniqueId()).thenReturn(id);

            callService();

            verify(urlService).createUrlEntity(urlRequest);
        }

        @Test
        void shouldCallSaveUrlEntity() {
            when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

            callService();

            verify(urlService).saveUrlEntity(any());
        }

        @Test
        void shouldCreateUrlResponse() {
            when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

            callService();

            verify(urlService).createUrlResponse(urlEntity);
        }

        @Test
        void shouldReturnUrlResponse() {
            when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

            final var responseEntity = callService();

            assertThat(responseEntity).isEqualTo(urlResponse);
        }

        private UrlResponse callService() {
            return urlService.createShortUrl(urlRequest);
        }
    }

    @Nested
    class CheckIfUrlAlreadyExistsTest {

        private final String id = "shortId";

        @Test
        void shouldThrowExceptionIfIdAlreadyExists() {
            when(urlRepository.existsById(id)).thenReturn(true);

            final var thrown = catchThrowable(this::callService);

            assertThat(thrown).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ID: " + id + " already in use");
        }

        @Test
        void shouldNotThrowExceptionIfIdDoesNotExist() {
            when(urlRepository.existsById(id)).thenReturn(false);

            assertThatCode(this::callService).doesNotThrowAnyException();
        }

        private void callService() {
            urlService.checkIfUrlAlreadyExists(id);
        }
    }

    @Nested
    class CreateUrlEntityTest {

        private final UrlRequest urlRequest = new UrlRequest("shortId", "http://example.com", 3600L);

        @Test
        void shouldCreateUrlEntityWithTtl() {
            when(idGenerator.generateUniqueId()).thenReturn(urlRequest.id());

            final var entity = urlService.createUrlEntity(urlRequest);

            assertThat(entity.getId()).isEqualTo(urlRequest.id());
            assertThat(entity.getLongUrl()).isEqualTo(urlRequest.longUrl());
            assertThat(entity.getTtl()).isEqualTo(urlRequest.ttl());
            assertThat(entity.getExpirationDate()).isNotNull();
        }

        @Test
        void shouldCreateUrlEntityWithoutTtl() {
            final var requestWithoutTtl = new UrlRequest("shortId", "http://example.com", null);
            final var entity = urlService.createUrlEntity(requestWithoutTtl);

            assertThat(entity.getExpirationDate()).isNull();
        }
    }

    @Nested
    class SaveUrlEntityTest {

        private final UrlEntity urlEntity = new UrlEntity();

        @Test
        void shouldSaveUrlEntity() {
            when(urlRepository.save(urlEntity)).thenReturn(urlEntity);

            final var savedEntity = urlService.saveUrlEntity(urlEntity);

            assertThat(savedEntity).isEqualTo(urlEntity);
            verify(urlRepository).save(urlEntity);
        }
    }

    @Nested
    class CreateUrlResponseTest {

        @Test
        void shouldCreateUrlResponse() {
            final String id = "shortId";
            final String longUrl = "https://example.com";
            final UrlEntity urlEntity = new UrlEntity();
            urlEntity.setId(id);
            urlEntity.setLongUrl(longUrl);

            final var urlResponse = urlService.createUrlResponse(urlEntity);

            assertThat(urlResponse).isNotNull();
            assertThat(urlResponse.id()).isEqualTo(id);
            assertThat(urlResponse.longUrl()).isEqualTo(longUrl);
            assertThat(urlResponse.shortUrl()).isEqualTo("http://localhost:8080/" + id);
        }

        @Test
        void shouldReturnCorrectShortUrl() {
            final String id = "anotherShortId";
            final UrlEntity urlEntity = new UrlEntity();
            urlEntity.setId(id);

            final var urlResponse = urlService.createUrlResponse(urlEntity);

            assertThat(urlResponse.shortUrl()).isEqualTo("http://localhost:8080/" + id);
        }
    }

    @Nested
    class GetLongUrlTest {

        private final String id = "shortId";
        private final String longUrl = "https://example.com";
        private final UrlEntity urlEntity = new UrlEntity();

        @BeforeEach
        void setUp() {
            urlEntity.setId(id);
            urlEntity.setLongUrl(longUrl);
            urlEntity.setExpirationDate(LocalDateTime.now().plusDays(1));
        }

        @Test
        void shouldRetrieveUrlEntity() {
            when(urlRepository.findById(id)).thenReturn(Optional.of(urlEntity));

            callService();

            verify(urlRepository).findById(id);
        }


        @Test
        void shouldThrowNotFoundExceptionIfUrlNotFound() {
            when(urlRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(this::callService)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No URL found for ID")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        void shouldReturnLongUrl() {
            when(urlRepository.findById(id)).thenReturn(Optional.of(urlEntity));

            final var longUrlResponse = callService();

            assertThat(longUrlResponse).isEqualTo(longUrl);
        }

        @Test
        void shouldThrowExceptionIfUrlExpired() {
            urlEntity.setExpirationDate(LocalDateTime.now().minusDays(1));
            when(urlRepository.findById(id)).thenReturn(Optional.of(urlEntity));

            assertThatThrownBy(this::callService)
                .isInstanceOf(UrlExpiredException.class)
                .hasMessageContaining(id);
            verify(urlRepository).delete(urlEntity);
        }

        private String callService() throws UrlExpiredException {
            return urlService.getLongUrl(id);
        }
    }

    @Nested
    class DeleteShortUrlTest {

        private final String id = "shortId";

        @Test
        void shouldCheckIfUrlExists() {
            when(urlRepository.existsById(id)).thenReturn(true);

            callService();

            verify(urlRepository).existsById(id);
        }

        @Test
        void shouldDeleteUrl() {
            when(urlRepository.existsById(id)).thenReturn(true);

            callService();

            verify(urlRepository).deleteById(id);
        }

        @Test
        void shouldThrowExceptionIfUrlDoesNotExist() {
            when(urlRepository.existsById(id)).thenReturn(false);

            final var thrown = catchThrowable(this::callService);

            assertThat(thrown).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No URL found for ID");
        }

        private void callService() {
            urlService.deleteShortUrl(id);
        }
    }
}