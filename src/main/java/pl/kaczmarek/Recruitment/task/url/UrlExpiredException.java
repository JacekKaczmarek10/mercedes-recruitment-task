package pl.kaczmarek.Recruitment.task.url;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UrlExpiredException extends ResponseStatusException {
    public UrlExpiredException(String id) {
        super(HttpStatus.NOT_FOUND, "URL is expired for ID: " + id);
    }
}