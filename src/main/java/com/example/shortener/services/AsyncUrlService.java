package com.example.shortener.services;

import com.example.shortener.entities.Url;
import com.example.shortener.exceptions.ServiceException;
import org.springframework.stereotype.Service;

@Service
public interface AsyncUrlService {

    void updateStatistics(String shortUrlKey, String userAgent, long duration) throws ServiceException;

    void updateDurationPostRequest(Url url, long duration) throws ServiceException;

    void updateDurationOfGettingLongUrl(String shortUrlKey, String dateTime, long duration) throws ServiceException;

    void saveInRedis(String shortUrlKey, String longUrl);

    void deleteInRedis(String shortUrlKey);
}
