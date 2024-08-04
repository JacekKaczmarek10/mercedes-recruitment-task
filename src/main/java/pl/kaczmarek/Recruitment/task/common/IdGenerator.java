package pl.kaczmarek.Recruitment.task.common;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String generateUniqueId() {
        final var id = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            id.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        log.debug("Generated unique ID: {}", id);
        return id.toString();
    }

}
