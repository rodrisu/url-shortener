package com.example.shortener.configuration;

import com.example.shortener.entities.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {


    private final String region;
    private final String tableName;
    private final String dynamoDbEndpoint;
    private final String accessKey;
    private final String secretKey;

    public DynamoDbConfig(@Value("${amazon.dynamodb.region}") String region,
                          @Value("${table.name}") String tableName,
                          @Value("${amazon.dynamodb.endpoint}") String dynamoDbEndpoint,
                          @Value("${amazon.aws.accesskey}") String accessKey,
                          @Value("${amazon.aws.secretkey}") String secretKey
                          ) {
        this.region = region;
        this.tableName = tableName;
        this.dynamoDbEndpoint = dynamoDbEndpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .endpointOverride(URI.create(dynamoDbEndpoint))
                        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
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
