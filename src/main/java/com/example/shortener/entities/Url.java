package com.example.shortener.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDbBean
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Url {

    @Getter(AccessLevel.NONE)
    private String key;
    private String longUrl;
    private String shortUrl;

    private int totalClicks;
    @Getter(AccessLevel.NONE)
    private List<String> ipAddresses;
    @Getter(AccessLevel.NONE)
    private List<String> webExplorers;
    @Getter(AccessLevel.NONE)
    private List<String> datesWhenClicked;
    @Getter(AccessLevel.NONE)
    private List<Long> durationOfRedirections;
    @Getter(AccessLevel.NONE)
    private Map<String, Long> durationOfGettingLongUrl;
    private long durationOfCreation;

    @DynamoDbPartitionKey
    public String getKey() {
        return key;
    }

    public List<String> getIpAddresses() {
        return ipAddresses == null ? new ArrayList<>() : ipAddresses;
    }

    public List<String> getWebExplorers() {
        return webExplorers == null ? new ArrayList<>() : webExplorers;
    }

    public List<String> getDatesWhenClicked() {
        return datesWhenClicked == null ? new ArrayList<>() : datesWhenClicked;
    }

    public List<Long> getDurationOfRedirections() {
        return durationOfRedirections == null ? new ArrayList<>() : durationOfRedirections;
    }

    public Map<String, Long> getDurationOfGettingLongUrl() {
        return durationOfGettingLongUrl == null ? new HashMap<>() : durationOfGettingLongUrl;
    }

    public void incrementTotalClicks() {
        totalClicks++;
    }

    public void addIpAddress(String ipAddress) {
        ipAddresses.add(ipAddress);
    }

    public void addWebExplorer(String webExplorer) {
        webExplorers.add(webExplorer);
    }

    public void addDate(String date) {
        datesWhenClicked.add(date);
    }

    public void addDurationOfRedirection(Long durationOfRedirection) {
        durationOfRedirections.add(durationOfRedirection);
    }

    public void addDurationOfGettingLongUrl(String dateTime, Long durationOfGettingLongUrlMs) {
        durationOfGettingLongUrl.put(dateTime, durationOfGettingLongUrlMs);
    }
}
