package com.example.shortener.utils;

import com.example.shortener.dtos.UrlDTO;
import com.example.shortener.entities.Url;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlMapper {

    public static UrlDTO mapToDTO(Url url) {
        return UrlDTO.builder()
                .url(url.getShortUrl())
                .dates(url.getDatesWhenClicked())
                .ipAddresses(url.getIpAddresses())
                .totalClicks(url.getTotalClicks())
                .webExplorers(url.getWebExplorers())
                .durationOfRedirections(url.getDurationOfRedirections())
                .durationOfCreation(url.getDurationOfCreation())
                .durationOfGettingLongUrl(url.getDurationOfGettingLongUrl())
                .build();
    }
}
