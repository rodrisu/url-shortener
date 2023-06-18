package com.example.shortener.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlValidator {
    public static final UrlValidator INSTANCE = new UrlValidator();

    /**
     * Regular expression pattern for validating URLs.
     * <p>
     * Pattern breakdown:
     * - ^(https?://)? : Matches an optional HTTP or HTTPS protocol.
     * - (www\\.)? : Matches an optional "www" subdomain.
     * - ([a-zA-Z0-9-]+\\.){1,} : Matches the domain, which consists of at least one subdomain or label.
     * - [a-zA-Z]{2,} : Matches the top-level domain (TLD) with a minimum length of 2 characters.
     * - (:[0-9]{1,5})? : Matches an optional colon followed by one to five digits for the port number.
     * - (/\\S*)?$ : Matches an optional path, starting with a forward slash (/), followed by any non-whitespace characters.
     */
    private static final String URL_REGEX = "^(https?://)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:[0-9]{1,5})?(/\\S*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public boolean validateURL(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
    }
}
