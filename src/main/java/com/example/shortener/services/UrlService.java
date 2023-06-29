package com.example.shortener.services;

import com.example.shortener.dtos.UrlDTO;
import com.example.shortener.exceptions.NotFoundException;
import com.example.shortener.exceptions.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

@Service
public interface UrlService {

    String shortenUrl(String longUrl) throws ServiceException;

    String getLongUrl(String shortUrlKey) throws ServiceException;

    void deleteShortUrl(String shortUrlKey) throws ServiceException, NotFoundException;

    RedirectView redirectView(String shortUrlKey, String userAgent) throws ServiceException, NotFoundException;

    UrlDTO getStatistics(String shortUrlKey);
}
