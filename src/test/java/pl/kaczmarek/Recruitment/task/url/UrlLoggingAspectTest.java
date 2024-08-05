package pl.kaczmarek.Recruitment.task.url;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UrlLoggingAspectTest {

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private final String shortUrl = "http://localhost:8080/api/";
    private final String exampleUrl = "https://example.com";
    private final String id = "shortId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateShortUrlTest {

        private final UrlRequest urlRequest = new UrlRequest(id, exampleUrl, null);
        private final UrlResponse urlResponse = new UrlResponse(id, exampleUrl, shortUrl + id);

        @Test
        void shouldCreateShortUrl() {
            when(urlService.createShortUrl(urlRequest)).thenReturn(urlResponse);

            final var response = urlController.createShortUrl(urlRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(urlResponse);
        }
    }

    @Nested
    class RedirectToLongUrlTest {

        @Test
        void shouldRedirectToLongUrl() {
            when(urlService.getLongUrl(id)).thenReturn(exampleUrl);

            final var response = urlController.redirectToLongUrl(id);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(exampleUrl));
        }

        @Test
        void shouldThrowExceptionIfUrlNotFound() {
            when(urlService.getLongUrl(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No URL found for ID"));

            final var thrown = assertThrows(ResponseStatusException.class, () -> urlController.redirectToLongUrl(id));

            assertThat(thrown.getReason()).isEqualTo("No URL found for ID");
        }
    }

    @Nested
    class DeleteShortUrlTest {

        @Test
        void shouldDeleteShortUrl() {
            doNothing().when(urlService).deleteShortUrl(id);

            final var response = urlController.deleteShortUrl(id);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(urlService, times(1)).deleteShortUrl(id);
        }

        @Test
        void shouldThrowExceptionIfUrlNotFound() {
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No URL found for ID")).when(urlService).deleteShortUrl(id);

            final var thrown = assertThrows(ResponseStatusException.class, () -> urlController.deleteShortUrl(id));

            assertThat(thrown.getReason()).isEqualTo("No URL found for ID");
        }
    }
}
