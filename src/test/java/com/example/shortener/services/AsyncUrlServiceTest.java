package com.example.shortener.services;

import com.example.shortener.entities.Url;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.repositories.RedisRepository;
import com.example.shortener.repositories.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AsyncUrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisRepository redisRepository;

    private AsyncUrlService asyncUrlService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        asyncUrlService = new AsyncUrlService(urlRepository, redisRepository);
    }

    @Test
    void updateStatistics_ShouldUpdateUrlStatisticsAndSaveInRedis() throws IOException {
        Url url = buildUrl();
        String userAgent = "Mozilla/5.0";
        long duration = 1000L;

        doNothing().when(urlRepository).update(any(Url.class));
        doNothing().when(redisRepository).save(any(Url.class));

        asyncUrlService.updateStatistics(url, userAgent, duration);

        verify(urlRepository, times(1)).update(url);
        verify(redisRepository, times(1)).save(url);
        assertEquals(1, url.getTotalClicks());
        assertEquals(1, url.getDatesWhenClicked().size());
        assertEquals(1, url.getWebExplorers().size());
        assertEquals(userAgent, url.getWebExplorers().get(0));
        assertEquals(1, url.getDurationOfRedirections().size());
        assertEquals(duration, url.getDurationOfRedirections().get(0).longValue());
    }

    @Test
    void updateStatistics_ShouldThrowServiceExceptionWhenUpdateFails() {
        Url url = buildUrl();
        String userAgent = "Mozilla/5.0";
        long duration = 1000L;

        doThrow(DynamoDbException.class).when(urlRepository).update(any(Url.class));

        assertThrows(ServiceException.class, () -> asyncUrlService.updateStatistics(url, userAgent, duration));
        verify(redisRepository, never()).save(any(Url.class));
    }

    @Test
    void updateDurationPostRequest_ShouldUpdateUrlDurationOfCreationAndSaveInRedis() throws ServiceException {
        Url url = buildUrl();
        long duration = 2000L;

        doNothing().when(urlRepository).update(any(Url.class));
        doNothing().when(redisRepository).save(any(Url.class));

        asyncUrlService.updateDurationPostRequest(url, duration);

        verify(urlRepository, times(1)).update(url);
        verify(redisRepository, times(1)).save(url);
        assertEquals(duration, url.getDurationOfCreation());
    }

    @Test
    void updateDurationPostRequest_ShouldThrowServiceExceptionWhenUpdateFails() {
        Url url = buildUrl();
        long duration = 2000L;

        doThrow(DynamoDbException.class).when(urlRepository).update(any(Url.class));

        assertThrows(ServiceException.class, () -> asyncUrlService.updateDurationPostRequest(url, duration));
        verify(redisRepository, never()).save(any(Url.class));
    }

    @Test
    void updateDurationOfGettingLongUrl_ShouldUpdateUrlDurationOfGettingLongUrlAndSaveInRedis() throws ServiceException {
        Url url = buildUrl();
        String dateTime = LocalDateTime.now().toString();
        long duration = 3000L;

        doNothing().when(urlRepository).update(any(Url.class));
        doNothing().when(redisRepository).save(any(Url.class));

        asyncUrlService.updateDurationOfGettingLongUrl(url, dateTime, duration);

        verify(urlRepository, times(1)).update(url);
        verify(redisRepository, times(1)).save(url);
        assertEquals(1, url.getDurationOfGettingLongUrl().size());
        assertEquals(duration, url.getDurationOfGettingLongUrl().get(dateTime).longValue());
    }

    @Test
    void updateDurationOfGettingLongUrl_ShouldThrowServiceExceptionWhenUpdateFails() {
        Url url = buildUrl();
        String dateTime = LocalDateTime.now().toString();
        long duration = 3000L;

        doThrow(DynamoDbException.class).when(urlRepository).update(any(Url.class));

        assertThrows(ServiceException.class, () -> asyncUrlService.updateDurationOfGettingLongUrl(url, dateTime, duration));
        verify(redisRepository, never()).save(any(Url.class));
    }

    @Test
    void saveInRedis_ShouldSaveUrlInRedisRepository() {
        Url url = new Url();

        doNothing().when(redisRepository).save(any(Url.class));

        asyncUrlService.saveInRedis(url);

        verify(redisRepository, times(1)).save(url);
    }

    @Test
    void deleteInRedis_ShouldDeleteUrlFromRedisRepository() {
        String shortUrlKey = "aBc123";

        asyncUrlService.deleteInRedis(shortUrlKey);

        verify(redisRepository, times(1)).deleteByKey(shortUrlKey);
    }

    private Url buildUrl() {
        return Url.builder()
                .ipAddresses(new ArrayList<>())
                .datesWhenClicked(new ArrayList<>())
                .durationOfRedirections(new ArrayList<>())
                .webExplorers(new ArrayList<>())
                .durationOfGettingLongUrl(new HashMap<>())
                .build();
    }
}