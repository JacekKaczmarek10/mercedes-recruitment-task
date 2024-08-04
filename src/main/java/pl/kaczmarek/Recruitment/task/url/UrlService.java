package pl.kaczmarek.Recruitment.task.url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.kaczmarek.Recruitment.task.common.IdGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final IdGenerator idGenerator;

    private final UrlRepository urlRepository;

    UrlResponse createShortUrl(final UrlRequest urlRequest) {
        checkIfUrlAlreadyExists(urlRequest.id());
        final var urlEntity = createUrlEntity(urlRequest);
        final var savedUrlEntity = saveUrlEntity(urlEntity);
        return createUrlResponse(savedUrlEntity);
    }

    void checkIfUrlAlreadyExists(final String id) {
        if (id != null && urlRepository.existsById(id)) {
            log.error("ID already in use: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID: " + id + " already in use");
        }
    }

    UrlEntity createUrlEntity(final UrlRequest urlRequest) {
        final var urlEntity = new UrlEntity();
        urlEntity.setId(urlRequest.id() != null ? urlRequest.id() : idGenerator.generateUniqueId());
        urlEntity.setLongUrl(urlRequest.longUrl());

        if (urlRequest.ttl() != null) {
            urlEntity.setTtl(urlRequest.ttl());
            urlEntity.setExpirationDate(LocalDateTime.now().plusSeconds(urlRequest.ttl()));
            log.debug("Set expiration date: {}", urlEntity.getExpirationDate());
        }

        return urlEntity;
    }

    UrlEntity saveUrlEntity(final UrlEntity urlEntity) {
        final var savedUrlEntity = urlRepository.save(urlEntity);
        log.info("Created short URL: {} -> {}", savedUrlEntity.getId(), savedUrlEntity.getLongUrl());
        return savedUrlEntity;
    }

    UrlResponse createUrlResponse(final UrlEntity savedUrlEntity) {
        return new UrlResponse(
            savedUrlEntity.getId(),
            savedUrlEntity.getLongUrl(),
            "http://localhost:8080/" + savedUrlEntity.getId()
        );
    }

    String getLongUrl(final String id) throws UrlExpiredException {
        final var urlEntity = urlRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No URL found for ID"));

        if (urlEntity.getExpirationDate() == null || urlEntity.getExpirationDate().isAfter(LocalDateTime.now())) {
            log.info("Redirecting to long URL: {} -> {}", id, urlEntity.getLongUrl());
            return urlEntity.getLongUrl();
        } else {
            urlRepository.delete(urlEntity);
            log.info("URL expired and deleted: {}", id);
            throw new UrlExpiredException(id);
        }
    }

    void deleteShortUrl(final String id) {
        if (urlRepository.existsById(id)) {
            urlRepository.deleteById(id);
            log.info("Deleted short URL: {}", id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No URL found for ID");
        }
    }

}
