package pl.kaczmarek.Recruitment.task.url;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.net.URISyntaxException;

@WebMvcTest(UrlController.class)
class UrlControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Nested
    class CreateShortUrlTest {

        private static final UrlRequest urlRequest = new UrlRequest("customId", "https://example.com", 3600L);
        private static final UrlResponse urlResponse = new UrlResponse("customId",
                                                                       "https://example.com",
                                                                       "http://localhost:8080/customId");

        @BeforeEach
        void setUp() {
            when(urlService.createShortUrl(urlRequest)).thenReturn(urlResponse);
        }

        @Test
        void shouldCallService() throws Exception {
            doRequest();

            verify(urlService).createShortUrl(urlRequest);
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest().andExpect(status().isOk());
        }

        @Test
        void shouldReturnResponseBody() throws Exception {
            doRequest().andExpect(jsonPath("$.id").value("customId"))
                .andExpect(jsonPath("$.longUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/customId"));
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(post("/api/shorten").contentType(MediaType.APPLICATION_JSON)
                                       .content(toJsonString(urlRequest)));
        }
    }

    @Nested
    class RedirectToLongUrlTest {

        private static final String id = "customId";
        private static final String longUrl = "https://example.com";

        @BeforeEach
        void setUp() {
            when(urlService.getLongUrl(id)).thenReturn(longUrl);
        }

        @Test
        void shouldCallService() throws Exception {
            doRequest();

            verify(urlService).getLongUrl(id);
        }

        @Test
        void shouldReturnRedirect() throws Exception {
            doRequest().andExpect(status().isFound()).andExpect(header().string("Location", longUrl));
        }

        @Test
        @Disabled
        void shouldReturnBadRequestForMalformedUrl() throws Exception {
            when(urlService.getLongUrl(id)).thenThrow(new URISyntaxException(longUrl, "Malformed URL"));

            doRequest().andExpect(status().isBadRequest())
                .andExpect(content().string("The URL provided is malformed and cannot be redirected."));
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(get("/api/{id}", id));
        }
    }

    @Nested
    class DeleteShortUrlTest {

        private static final String id = "customId";

        @Test
        void shouldCallService() throws Exception {
            doRequest();

            verify(urlService).deleteShortUrl(id);
        }

        @Test
        void shouldReturnNoContent() throws Exception {
            doRequest().andExpect(status().isNoContent());
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(delete("/api/{id}", id));
        }
    }
}
