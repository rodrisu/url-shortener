package com.example.shortener.controllers;

import com.example.shortener.dtos.ResponseDTO;
import com.example.shortener.dtos.UrlDTO;
import com.example.shortener.exceptions.NotFoundException;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.exceptions.ValidationException;
import com.example.shortener.services.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping(value = "/urls", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> shortenUrl(@RequestBody UrlDTO urlDTO) {
        log.info("Received body: {}", urlDTO.toString());
        try {
            return new ResponseEntity<>(
                    ResponseDTO.builder()
                            .shortUrl(urlService.shortenUrl(urlDTO.getUrl()))
                            .longUrl(urlDTO.getUrl())
                            .message("URL acortada correctamente")
                            .build()
            , HttpStatus.CREATED);
        } catch (ServiceException e) {
            log.warn("Exception Stack Trace: {}", Arrays.toString(e.getStackTrace()));
            return createErrorResponse(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Exception message: {}", e.getMessage());
            return createBadRequestResponse(e.getMessage(), urlDTO.getUrl());
        }

    }

    @GetMapping(value = "/urls/{shortUrlKey}", produces = {"application/json"})
    public ResponseEntity<?> getLongUrl(@PathVariable("shortUrlKey") String shortUrlKey) {
        log.info("Received value: {}", shortUrlKey);
        try {
            String longUrl = urlService.getLongUrl(shortUrlKey);
            return createSuccessResponse(longUrl);
        } catch (NotFoundException n){
            log.warn("Exception message: {}", n.getMessage());
            return createNotFoundResponse(n.getMessage(), shortUrlKey);
        } catch (ServiceException e) {
            log.warn("Exception Stack Trace: {}", Arrays.toString(e.getStackTrace()));
            return createErrorResponse(e.getMessage());
        }
    }

    @DeleteMapping(value = "/urls/{shortUrlKey}", produces = {"application/json"})
    public ResponseEntity<?> deleteShortUrl(@PathVariable String shortUrlKey) {
        log.info("Received value: {}", shortUrlKey);
        try {
            urlService.deleteShortUrl(shortUrlKey);
            return ResponseEntity.ok().build();
        } catch (ServiceException e) {
            log.warn("Exception Stack Trace: {}", Arrays.toString(e.getStackTrace()));
            return createErrorResponse(e.getMessage());
        } catch (NotFoundException n){
            log.warn("Exception message: {}", n.getMessage());
            return createNotFoundResponse(n.getMessage(), shortUrlKey);
        }
    }

    @GetMapping(value = "/urls/{shortUrlKey}/statistics", produces = {"application/json"})
    public ResponseEntity<?> getStatistics(@PathVariable String shortUrlKey) {
        log.info("Received value: {}", shortUrlKey);
        try {
            UrlDTO statistics = urlService.getStatistics(shortUrlKey);
            return ResponseEntity.ok(statistics);
        } catch (NotFoundException n){
            log.warn("Exception message: {}", n.getMessage());
            return createNotFoundResponse(n.getMessage(), shortUrlKey);
        }
    }

    @GetMapping(value = "/{shortUrlKey}")
    public RedirectView redirectUrl(@PathVariable String shortUrlKey, @RequestHeader("User-Agent") String userAgent) {
        log.info("Received value: {}", shortUrlKey);
        RedirectView redirectView;
        try {
            redirectView = urlService.redirectView(shortUrlKey, userAgent);
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return redirectView;
        } catch (ServiceException e) {
            log.warn("Exception Stack Trace: {}", Arrays.toString(e.getStackTrace()));
            return new RedirectView("/error");
        } catch (NotFoundException n) {
            log.warn("Exception message: {}", n.getMessage());
            return new RedirectView("/not-found");
        }
    }

    private ResponseEntity<ResponseDTO> createSuccessResponse(String longUrl) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .longUrl(longUrl)
                .build();
        return ResponseEntity.ok(responseDTO);
    }

    private ResponseEntity<ResponseDTO> createNotFoundResponse(String message, String shortUrl) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(message)
                .shortUrl(shortUrl)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
    }

    private ResponseEntity<ResponseDTO> createBadRequestResponse(String message, String longUrl) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(message)
                .longUrl(longUrl)
                .build();
        return ResponseEntity.badRequest().body(responseDTO);
    }

    private ResponseEntity<ResponseDTO> createErrorResponse(String message) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }
}
