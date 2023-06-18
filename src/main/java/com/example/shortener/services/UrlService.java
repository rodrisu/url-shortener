package com.example.shortener.services;

import com.example.shortener.dtos.UrlDTO;
import com.example.shortener.entities.Url;
import com.example.shortener.exceptions.NotFoundException;
import com.example.shortener.exceptions.ServiceException;
import com.example.shortener.exceptions.ValidationException;
import com.example.shortener.repositories.RedisRepository;
import com.example.shortener.repositories.UrlRepository;
import com.example.shortener.utils.UrlGenerator;
import com.example.shortener.utils.UrlMapper;
import com.example.shortener.utils.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.io.IOException;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisRepository redisRepository;
    private final AsyncUrlService asyncUrlService;

    public String shortenUrl(String longUrl) throws ServiceException {
        long startTime = System.currentTimeMillis();

        if (longUrl == null) {
            log.warn("URL is null");
            throw new ValidationException("Debe ingresar el parametro correctamente");
        }
        if (!UrlValidator.INSTANCE.validateURL(longUrl)) {
            log.warn("Invalid URL");
            throw new ValidationException("Url ingresada no es valida");
        }

        String shortUrl = UrlGenerator.INSTANCE.generateShortUrl();

        if(urlRepository.getUrl(shortUrl).isPresent()) {
            log.warn("URL with key {} already exists in the database", shortUrl);
            throw new ServiceException("Ocurrio un error, intente nuevamente, por favor");
        }

        Url url;

        try {
            url = Url.builder()
                    .key(UrlGenerator.INSTANCE.getKeyFromShortUrl(shortUrl))
                    .longUrl(longUrl)
                    .shortUrl(shortUrl)
                    .build();
            urlRepository.save(url);
        } catch (DynamoDbException e){
            throw new ServiceException("Ocurrio un error, intente nuevamente mas tarde");
        }

        asyncUrlService.saveInRedis(url);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("Request shortenUrl - Duration: {} ms", duration);

        asyncUrlService.updateDurationPostRequest(url, duration);

        log.info("Short URL successfully generated: {}", shortUrl);

        return shortUrl;
    }

    public String getLongUrl(String shortUrlKey) throws ServiceException {
        long startTime = System.currentTimeMillis();
        String dateTime = String.valueOf(ZonedDateTime.now());

        Url url = getUrl(shortUrlKey);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("Request getLongUrl - Date and time: {}, Duration: {} ms", dateTime, duration);

        asyncUrlService.updateDurationOfGettingLongUrl(url, dateTime, duration);


        return url.getLongUrl();
    }

    public void deleteShortUrl(String shortUrlKey) throws ServiceException, NotFoundException {
        try {
            urlRepository.getUrl(shortUrlKey)
                    .orElseThrow(() -> new NotFoundException("URL no existe para ser eliminada"));
            urlRepository.delete(shortUrlKey);
        } catch (DynamoDbException e) {
            throw new ServiceException("Ocurrio un error al intentar eliminar, intente nuevamente mas tarde");
        }
        asyncUrlService.deleteInRedis(shortUrlKey);
    }

    public RedirectView redirectView(String shortUrlKey, String userAgent) throws ServiceException, NotFoundException {
        long startTime = System.currentTimeMillis();
        try {
            Url url = getUrl(shortUrlKey);
            RedirectView redirectView = new RedirectView(url.getLongUrl());

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.info("Request redirectUrl - Duration: {} ms", duration);

            asyncUrlService.updateStatistics(url, userAgent, duration);

            return redirectView;
        } catch (IOException e) {
            throw new ServiceException("Ocurrio un error, intente nuevamente");
        }


    }

    public UrlDTO getStatistics(String shortUrlKey) {
        return UrlMapper.mapToDTO(getUrl(shortUrlKey));
    }

    private Url getUrl(String shortUrlKey) {
        Url url = redisRepository.findByKey(shortUrlKey);
        if (url != null) {
            log.info("Url IS cached");
            return url;
        }

        log.info("Url is NOT cached");
        url = urlRepository.getUrl(shortUrlKey)
                .orElseThrow(() -> new NotFoundException("URL no encontrada"));

        asyncUrlService.saveInRedis(url);
        return url;
    }
}
