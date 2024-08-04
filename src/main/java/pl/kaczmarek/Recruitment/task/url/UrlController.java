package pl.kaczmarek.Recruitment.task.url;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    ResponseEntity<UrlResponse> createShortUrl(@RequestBody UrlRequest urlRequest) {
        return ResponseEntity.ok(urlService.createShortUrl(urlRequest));
    }

    @GetMapping("/{id}")
    ResponseEntity<Object> redirectToLongUrl(@PathVariable String id) {
        try {
            final var longUrl = urlService.getLongUrl(id);
            return ResponseEntity.status(302).location(new URI(longUrl)).build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DefaultResponse("The URL provided is malformed and cannot be redirected."));
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteShortUrl(@PathVariable String id) {
        urlService.deleteShortUrl(id);
        return ResponseEntity.noContent().build();
    }

}