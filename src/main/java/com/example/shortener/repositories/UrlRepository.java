package com.example.shortener.repositories;

import com.example.shortener.entities.Url;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class UrlRepository {

    private final DynamoDbTable<Url> urlTable;

    public void save(Url url) {
        log.info("Saving in DynamoDB {}", url.toString());
        urlTable.putItem(url);
    }

    public Optional<Url> getUrl(String urlKey) {
        log.info("Looking in DynamoDB for key {}", urlKey);
        Key key = Key.builder()
                .partitionValue(urlKey)
                .build();
        return Optional.ofNullable(urlTable.getItem(key));
    }

    public void delete(String urlKey) {
        log.info("Deleting from DynamoDB key {}", urlKey);
        Key key = Key.builder()
                .partitionValue(urlKey)
                .build();
        urlTable.deleteItem(key);
    }

    public void update(Url url) {
        log.info("Updating in DynamoDB {}", url.toString());
        urlTable.updateItem(url);
    }

}
