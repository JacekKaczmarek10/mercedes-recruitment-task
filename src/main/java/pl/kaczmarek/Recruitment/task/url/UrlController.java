package pl.kaczmarek.Recruitment.task.url;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    ResponseEntity<UrlResponse> createShortUrl(@RequestBody UrlRequest urlRequest) {
        return ResponseEntity.ok(urlService.createShortUrl(urlRequest));
    }

    @GetMapping("/{id}")
    ResponseEntity<Object> redirectToLongUrl(@PathVariable String id) {
        final var longUrl = urlService.getLongUrl(id);
        return ResponseEntity.status(302).location(URI.create(longUrl)).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteShortUrl(@PathVariable String id) {
        urlService.deleteShortUrl(id);
        return ResponseEntity.noContent().build();
    }

}