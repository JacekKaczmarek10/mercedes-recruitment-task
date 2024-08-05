package pl.kaczmarek.Recruitment.task.url;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class UrlExpiredException extends ResponseStatusException {
    UrlExpiredException(String id) {
        super(HttpStatus.NOT_FOUND, "URL is expired for ID: " + id);
    }
}