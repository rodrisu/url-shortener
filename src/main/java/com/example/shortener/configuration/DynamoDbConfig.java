package com.example.shortener.configuration;

import com.example.shortener.entities.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {


    private final String region;
    private final String tableName;

    public DynamoDbConfig(@Value("${aws.dynamodb.region}") String region,
                          @Value("${table.name}") String tableName) {
        this.region = region;
        this.tableName = tableName;
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .region(Region.of(region))
                        .build())
                .build();
    }

    @Bean
    public DynamoDbTable<Url> urlTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        TableSchema<Url> urlTableSchema = TableSchema.fromBean(Url.class);
        return dynamoDbEnhancedClient.table(tableName, urlTableSchema);
    }
}
