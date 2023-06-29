package com.example.shortener.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UrlGenerator {

    public static final UrlGenerator INSTANCE = new UrlGenerator();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALPHABET.length();
    private static final String URL_DOMAIN = "https://cut.it/";
    private static final int ID_LENGTH = 6;

    /**
     * Join URL base with unique shortUrl Key.
     * @return complete short URL as a String
     */
    public String generateShortUrl() {
        return URL_DOMAIN + generateShortUrlKey();
    }

    /**
     * Generate unique keys for short URLs.
     * @return unique key as a String
     */
    private static String generateShortUrlKey() {
        StringBuilder shortUrlKey = new StringBuilder(ID_LENGTH);

        for (int i = 0; i < ID_LENGTH; i++) {
            int index = ThreadLocalRandom.current().nextInt(BASE);
            shortUrlKey.append(ALPHABET.charAt(index));
        }
        return shortUrlKey.toString();
    }

    /**
     * Get the key part from a complete short URL.
     * @param shortUrl complete
     * @return shortUrl key as a String
     */
    public String getKeyFromShortUrl(String shortUrl) {
        log.info("Short URL recibida: {}", shortUrl);
        return shortUrl.substring(shortUrl.lastIndexOf("/") + 1);
    }
}
