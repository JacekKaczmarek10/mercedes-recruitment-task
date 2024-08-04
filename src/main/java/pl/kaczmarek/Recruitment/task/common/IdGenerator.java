package pl.kaczmarek.Recruitment.task.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kaczmarek.Recruitment.task.url.UrlRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = ALPHANUMERIC.length();

    private final LastGeneratedIdRepository lastGeneratedIdRepository;
    private final UrlRepository urlRepository;

    public synchronized String generateUniqueId() {
        String newId;
        LastGeneratedId lastGeneratedId;
        do {
            lastGeneratedId = lastGeneratedIdRepository.findById(1L)
                .orElseGet(() -> new LastGeneratedId(1L, "A"));

            final var lastId = lastGeneratedId.getLastId();
            newId = incrementId(lastId);

            lastGeneratedId.setLastId(newId);
        } while (urlRepository.existsById(newId));

        lastGeneratedIdRepository.save(lastGeneratedId);

        log.debug("Generated unique ID: {}", newId);
        return newId;
    }

    String incrementId(String id) {
        final var chars = id.toCharArray();
        final var length = chars.length;

        for (int i = length - 1; i >= 0; i--) {
            final var index = ALPHANUMERIC.indexOf(chars[i]);
            if (index < BASE - 1) {
                chars[i] = ALPHANUMERIC.charAt(index + 1);
                return new String(chars);
            }
            chars[i] = ALPHANUMERIC.charAt(0);
        }
        return ALPHANUMERIC.charAt(0) + new String(chars);
    }

}