package com.example.shortener.services.impl;

import com.example.shortener.entities.Url;
import com.example.shortener.exceptions.NotFoundException;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.repositories.RedisRepository;
import com.example.shortener.repositories.UrlRepository;
import com.example.shortener.services.AsyncUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncUrlServiceImpl implements AsyncUrlService {

    private final UrlRepository urlRepository;
    private final RedisRepository redisRepository;
    private static final String SERVICE_EXCEPTION_MESSAGE = "Ocurrió un error, inténtelo nuevamente más tarde";

    @Async
    public void updateStatistics(String shortUrlKey, String userAgent, long duration) throws ServiceException {
        log.info("Updating statistics");
        try {
            Url url = getUrl(shortUrlKey);
            url.incrementTotalClicks();
            url.addIpAddress(getIpAddress());
            url.addDate(LocalDateTime.now().toString());
            url.addWebExplorer(userAgent);
            url.addDurationOfRedirection(duration);
            urlRepository.update(url);

        } catch (IOException | DynamoDbException e) {
            throw new ServiceException(SERVICE_EXCEPTION_MESSAGE);
        }
    }

    @Async
    public void updateDurationPostRequest(Url url, long duration) throws ServiceException {
        log.info("Adding duration time that took to make the POST request. Url: {}. Duration: {} ms", url, duration);
        try {
            url.setDurationOfCreation(duration);
            urlRepository.update(url);
        } catch (DynamoDbException e) {
            throw new ServiceException(SERVICE_EXCEPTION_MESSAGE);
        }
    }

    @Async
    public void updateDurationOfGettingLongUrl(String shortUrlKey, String dateTime, long duration) throws ServiceException {
        log.info("Adding duration time that took to make the GET request for the longUrl");
        try {
            Url url = getUrl(shortUrlKey);
            url.addDurationOfGettingLongUrl(dateTime, duration);
            urlRepository.update(url);
        } catch (DynamoDbException e) {
            throw new ServiceException(SERVICE_EXCEPTION_MESSAGE);
        }
    }

    private Url getUrl(String shortUrlKey) {
        return urlRepository.getUrl(shortUrlKey)
                .orElseThrow(() -> new NotFoundException("URL no encontrada"));
    }

    private String getIpAddress() throws IOException {
        URL url = new URL("https://checkip.amazonaws.com");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @Async
    public void saveInRedis(String shortUrlKey, String longUrl) {
        log.info("Saving in Redis");
        redisRepository.save(shortUrlKey, longUrl);
    }

    @Async
    public void deleteInRedis(String shortUrlKey) {
        redisRepository.deleteByKey(shortUrlKey);
    }
}
