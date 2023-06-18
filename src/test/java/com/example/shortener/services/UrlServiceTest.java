package com.example.shortener.services;

import com.example.shortener.dtos.UrlDTO;
import com.example.shortener.entities.Url;
import com.example.shortener.exceptions.NotFoundException;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.exceptions.ValidationException;
import com.example.shortener.repositories.RedisRepository;
import com.example.shortener.repositories.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private AsyncUrlService asyncUrlService;

    private UrlService urlService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        urlService = new UrlService(urlRepository, redisRepository, asyncUrlService);
    }


    @Test
    void shortenUrl_ValidUrl_ShouldReturnShortUrl() throws ServiceException {

        String longUrl = "https://www.google.com";
        String shortUrl = "aBc123";

        when(urlRepository.getUrl(shortUrl)).thenReturn(Optional.empty());
        doNothing().when(urlRepository).save(any(Url.class));

        String result = urlService.shortenUrl(longUrl);

        assertNotNull(result);
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(asyncUrlService, times(1)).saveInRedis(any(Url.class));
    }

    @Test
    void shortenUrl_NullUrl_ShouldThrowValidationException() {
        String longUrl = null;

        assertThrows(ValidationException.class, () -> urlService.shortenUrl(longUrl));
        verify(urlRepository, never()).getUrl(anyString());
        verify(urlRepository, never()).save(any(Url.class));
        verify(asyncUrlService, never()).saveInRedis(any(Url.class));
    }

    @Test
    void shortenUrl_InvalidUrl_ShouldThrowValidationException() {
        String longUrl = "invalid-url";

        assertThrows(ValidationException.class, () -> urlService.shortenUrl(longUrl));
        verify(urlRepository, never()).getUrl(anyString());
        verify(urlRepository, never()).save(any(Url.class));
        verify(asyncUrlService, never()).saveInRedis(any(Url.class));
    }

    @Test
    void shortenUrl_DuplicateShortUrl_ShouldThrowServiceException() {
        String longUrl = "https://www.google.com";

        when(urlRepository.getUrl(anyString())).thenReturn(Optional.of(new Url()));

        assertThrows(ServiceException.class, () -> urlService.shortenUrl(longUrl));
        verify(urlRepository, never()).save(any(Url.class));
        verify(asyncUrlService, never()).saveInRedis(any(Url.class));
    }

    @Test
    void getLongUrl_ExistingShortUrlKey_ShouldReturnLongUrl() throws ServiceException {
        String shortUrlKey = "aBc123";
        Url url = Url.builder()
                .longUrl("https://www.google.com")
                .build();

        when(redisRepository.findByKey(shortUrlKey)).thenReturn(null);
        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(shortUrlKey);

        assertNotNull(result);
        assertEquals(url.getLongUrl(), result);
        verify(asyncUrlService, times(1)).updateDurationOfGettingLongUrl(any(Url.class), anyString(), anyLong());
    }

    @Test
    void getLongUrl_NonExistingShortUrlKey_ShouldThrowNotFoundException() throws ServiceException {
        String shortUrlKey = "ABC123";

        when(redisRepository.findByKey(shortUrlKey)).thenReturn(null);
        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getLongUrl(shortUrlKey));
        verify(asyncUrlService, never()).updateDurationOfGettingLongUrl(any(Url.class), anyString(), anyLong());
    }

    @Test
    void deleteShortUrl_ExistingShortUrlKey_ShouldDeleteUrl() throws ServiceException, NotFoundException {
        String shortUrlKey = "aBc123";
        Url url = new Url();

        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.of(url));
        doNothing().when(urlRepository).delete(shortUrlKey);

        urlService.deleteShortUrl(shortUrlKey);

        verify(urlRepository, times(1)).delete(shortUrlKey);
        verify(asyncUrlService, times(1)).deleteInRedis(shortUrlKey);
    }

    @Test
    void deleteShortUrl_NonExistingShortUrlKey_ShouldThrowNotFoundException() {
        String shortUrlKey = "aBc123";

        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.deleteShortUrl(shortUrlKey));
        verify(urlRepository, never()).delete(anyString());
        verify(asyncUrlService, never()).deleteInRedis(anyString());
    }

    @Test
    void redirectView_ExistingShortUrlKey_ShouldReturnRedirectView() throws NotFoundException, IOException {
        String shortUrlKey = "aBc123";
        String userAgent = "Mozilla/5.0";
        Url url = Url.builder()
                .longUrl("https://www.google.com")
                .build();

        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.of(url));

        RedirectView redirectView = urlService.redirectView(shortUrlKey, userAgent);

        assertNotNull(redirectView);
        assertEquals(url.getLongUrl(), redirectView.getUrl());
        verify(asyncUrlService, times(1)).updateStatistics(any(Url.class), anyString(), anyLong());
    }

    @Test
    void redirectView_NonExistingShortUrlKey_ShouldThrowNotFoundException() throws ServiceException {
        String shortUrlKey = "aBc123";
        String userAgent = "Mozilla/5.0";

        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.redirectView(shortUrlKey, userAgent));
        verify(asyncUrlService, never()).updateStatistics(any(Url.class), anyString(), anyLong());
    }

    @Test
    void getStatistics_ExistingShortUrlKey_ShouldReturnUrlDTO() {
        String shortUrlKey = "aBc123";
        Url url = Url.builder()
                .longUrl("https://www.google.com")
                .build();

        when(redisRepository.findByKey(anyString())).thenReturn(null);
        when(urlRepository.getUrl(anyString())).thenReturn(Optional.of(url));

        UrlDTO urlDTO = urlService.getStatistics(shortUrlKey);

        assertNotNull(urlDTO);
        verify(asyncUrlService, times(1)).saveInRedis(any(Url.class));
    }

    @Test
    void getStatistics_NonExistingShortUrlKey_ShouldThrowNotFoundException() {
        String shortUrlKey = "aBc123";

        when(redisRepository.findByKey(shortUrlKey)).thenReturn(null);
        when(urlRepository.getUrl(shortUrlKey)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getStatistics(shortUrlKey));
        verify(asyncUrlService, never()).saveInRedis(any(Url.class));
    }
}