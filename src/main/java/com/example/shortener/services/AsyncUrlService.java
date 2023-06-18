package com.example.shortener.services;

import com.example.shortener.entities.Url;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.repositories.RedisRepository;
import com.example.shortener.repositories.UrlRepository;
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
public class AsyncUrlService {

    private final UrlRepository urlRepository;
    private final RedisRepository redisRepository;

    @Async
    protected void updateStatistics(Url url, String userAgent, long duration) throws ServiceException {
        log.info("Updating statistics");
        try {
            url.incrementTotalClicks();
            url.addIpAddress(getIpAddress());
            url.addDate(LocalDateTime.now().toString());
            url.addWebExplorer(userAgent);
            url.addDurationOfRedirection(duration);
            urlRepository.update(url);
            saveInRedis(url);

        } catch (IOException | DynamoDbException e) {
            throw new ServiceException("Ocurrió un error, inténtelo nuevamente más tarde");
        }
    }

    @Async
    protected void updateDurationPostRequest(Url url, long duration) throws ServiceException {
        log.info("Adding duration time that took to make the POST request. Url: {}. Duration: {} ms", url, duration);
        try {
            url.setDurationOfCreation(duration);
            urlRepository.update(url);
            saveInRedis(url);
        } catch (DynamoDbException e) {
            throw new ServiceException("Ocurrió un error, inténtelo nuevamente más tarde");
        }
    }

    @Async
    protected void updateDurationOfGettingLongUrl(Url url, String dateTime, long duration) throws ServiceException {
        log.info("Adding duration time that took to make the GET request for the longUrl");
        try {
            url.addDurationOfGettingLongUrl(dateTime, duration);
            urlRepository.update(url);
            saveInRedis(url);
        } catch (DynamoDbException e) {
            throw new ServiceException("Ocurrió un error, inténtelo nuevamente más tarde");
        }
    }

    String getIpAddress() throws IOException {
        URL url = new URL("https://checkip.amazonaws.com");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @Async
    protected void saveInRedis(Url url) {
        log.info("Saving in Redis");
        redisRepository.save(url);
    }

    @Async
    protected void deleteInRedis(String shortUrlKey) {
        redisRepository.deleteByKey(shortUrlKey);
    }
}
