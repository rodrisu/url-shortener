package com.example.shortener.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UrlDTO {

    private String url;
    private int totalClicks;
    private List<String> ipAddresses;
    private List<String> webExplorers;
    private List<String> dates;
    private List<Long> durationOfRedirections;
    private Map<String, Long> durationOfGettingLongUrl;
    private long durationOfCreation;
}
