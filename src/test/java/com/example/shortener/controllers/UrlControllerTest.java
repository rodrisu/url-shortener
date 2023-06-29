package com.example.shortener.controllers;

import com.example.shortener.dtos.ResponseDTO;
import com.example.shortener.dtos.UrlDTO;
import com.example.shortener.exceptions.NotFoundException;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.exceptions.ValidationException;
import com.example.shortener.services.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UrlControllerTest {
    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shortenUrl_ValidUrl_ReturnsCreatedResponse201() throws ServiceException, ValidationException {
        UrlDTO urlDTO = UrlDTO.builder().url("https://www.google.com").build();
        String shortUrl = "https://me.li/abc123";

        when(urlService.shortenUrl(urlDTO.getUrl())).thenReturn(shortUrl);

        ResponseEntity<?> response = urlController.shortenUrl(urlDTO);

        verify(urlService, times(1)).shortenUrl(urlDTO.getUrl());
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(shortUrl, ((ResponseDTO) response.getBody()).getShortUrl());
    }

    @Test
    void shortenUrl_ValidUrl_ThrowsServiceException500() throws ServiceException, ValidationException {
        UrlDTO urlDTO = UrlDTO.builder().url("https://www.google.com").build();

        when(urlService.shortenUrl(urlDTO.getUrl())).thenThrow(new ServiceException("Internal server error"));

        ResponseEntity<?> response = urlController.shortenUrl(urlDTO);

        verify(urlService, times(1)).shortenUrl(urlDTO.getUrl());
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(("Internal server error"), ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void shortenUrl_InvalidUrl_ThrowsValidationException404() throws ServiceException, ValidationException {
        UrlDTO urlDTO = UrlDTO.builder().url("invalid-url").build();

        when(urlService.shortenUrl(urlDTO.getUrl())).thenThrow(new ValidationException("Url ingresada no es valida"));

        ResponseEntity<?> response = urlController.shortenUrl(urlDTO);

        verify(urlService, times(1)).shortenUrl(urlDTO.getUrl());
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(("Url ingresada no es valida"), ((ResponseDTO) response.getBody()).getMessage());
        assertEquals(urlDTO.getUrl(), ((ResponseDTO) response.getBody()).getLongUrl());
    }

    @Test
    void getLongUrl_ValidShortUrl_ReturnsSuccessResponse200() throws NotFoundException, ValidationException, ServiceException {
        String shortUrlKey = "abc123";
        String longUrl = "https://www.google.com";

        when(urlService.getLongUrl(shortUrlKey)).thenReturn(longUrl);

        ResponseEntity<?> response = urlController.getLongUrl(shortUrlKey);

        verify(urlService, times(1)).getLongUrl(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(longUrl, ((ResponseDTO) response.getBody()).getLongUrl());
    }

    @Test
    void getLongUrl_ValidShortUrl_ThrowsNotFoundException404() throws NotFoundException, ValidationException, ServiceException {
        String shortUrlKey = "abc123";

        when(urlService.getLongUrl(shortUrlKey)).thenThrow(new NotFoundException("URL no encontrada"));

        ResponseEntity<?> response = urlController.getLongUrl(shortUrlKey);

        verify(urlService, times(1)).getLongUrl(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("URL no encontrada", ((ResponseDTO) response.getBody()).getMessage());
        assertEquals(((ResponseDTO) response.getBody()).getShortUrl(), shortUrlKey);
    }

    @Test
    void getLongUrl_ValidShortUrl_ThrowsServiceException500() throws NotFoundException, ValidationException, ServiceException {
        String shortUrlKey = "abc123";

        when(urlService.getLongUrl(shortUrlKey)).thenThrow(new ServiceException("Internal server error"));

        ResponseEntity<?> response = urlController.getLongUrl(shortUrlKey);

        verify(urlService, times(1)).getLongUrl(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void deleteShortUrl_ValidShortUrl_ReturnsOkResponse200() throws ServiceException, NotFoundException {
        String shortUrlKey = "abc123";

        ResponseEntity<?> response = urlController.deleteShortUrl(shortUrlKey);

        verify(urlService, times(1)).deleteShortUrl(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteShortUrl_ValidShortUrl_ThrowsServiceException500() throws ServiceException, NotFoundException {
        String shortUrlKey = "abc123";

        doThrow(new ServiceException("Internal server error")).when(urlService).deleteShortUrl(shortUrlKey);

        ResponseEntity<?> response = urlController.deleteShortUrl(shortUrlKey);

        verify(urlService, times(1)).deleteShortUrl(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void deleteShortUrl_InvalidShortUrl_ThrowsNotFoundException404() throws ServiceException, NotFoundException {
        String shortUrlKey = "abc123";

        doThrow(new NotFoundException("URL no encontrada")).when(urlService).deleteShortUrl(shortUrlKey);

        ResponseEntity<?> response = urlController.deleteShortUrl(shortUrlKey);

        verify(urlService, times(1)).deleteShortUrl(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getStatistics_ValidShortUrl_ReturnsOkResponse200() throws NotFoundException {
        String shortUrlKey = "abc123";
        UrlDTO statistics = UrlDTO.builder()
                .url("https://me.li/abc123")
                .durationOfCreation(200)
                .build();

        when(urlService.getStatistics(shortUrlKey)).thenReturn(statistics);

        ResponseEntity<?> response = urlController.getStatistics(shortUrlKey);

        verify(urlService, times(1)).getStatistics(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statistics, response.getBody());
    }

    @Test
    void getStatistics_InvalidShortUrl_ThrowsNotFoundException404() throws NotFoundException {
        String shortUrlKey = "abc123";

        ResponseDTO responseDTO = ResponseDTO.builder()
                .shortUrl(shortUrlKey)
                .message("URL no encontrada")
                .build();

        when(urlService.getStatistics(shortUrlKey)).thenThrow(new NotFoundException("URL no encontrada"));

        ResponseEntity<?> response = urlController.getStatistics(shortUrlKey);

        verify(urlService, times(1)).getStatistics(shortUrlKey);
        verifyNoMoreInteractions(urlService);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void redirectUrl_ValidShortUrl_ReturnsRedirectViewWithPermanentRedirect301() throws ServiceException {
        String shortUrlKey = "abc123";
        String userAgent = "Mozilla/5.0";

        RedirectView mockedRedirectView = new RedirectView("https://www.google.com");
        mockedRedirectView.setStatusCode(HttpStatus.PERMANENT_REDIRECT);

        when(urlService.redirectView(shortUrlKey, userAgent)).thenReturn(mockedRedirectView);

        RedirectView redirectView = urlController.redirectUrl(shortUrlKey, userAgent);

        verify(urlService, times(1)).redirectView(shortUrlKey, userAgent);
        verifyNoMoreInteractions(urlService);
        assertEquals("https://www.google.com", redirectView.getUrl());
    }

    @Test
    void redirectUrl_ValidShortUrl_ThrowsServiceException500() throws ServiceException {
        String shortUrlKey = "abc123";
        String userAgent = "Mozilla/5.0";

        when(urlService.redirectView(shortUrlKey, userAgent)).thenThrow(new ServiceException("Internal server error"));

        RedirectView redirectView = urlController.redirectUrl(shortUrlKey, userAgent);

        verify(urlService, times(1)).redirectView(shortUrlKey, userAgent);
        verifyNoMoreInteractions(urlService);
        assertEquals("/error", redirectView.getUrl());
    }

    @Test
    void redirectUrl_InvalidShortUrl_ThrowsNotFoundException404() throws ServiceException {
        String shortUrlKey = "abc123";
        String userAgent = "Mozilla/5.0";

        when(urlService.redirectView(shortUrlKey, userAgent)).thenThrow(new NotFoundException("URL no encontrada"));

        RedirectView redirectView = urlController.redirectUrl(shortUrlKey, userAgent);

        verify(urlService, times(1)).redirectView(shortUrlKey, userAgent);
        verifyNoMoreInteractions(urlService);
        assertEquals("/not-found", redirectView.getUrl());
    }
}