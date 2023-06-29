package com.example.shortener.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository {

    void save(String shortUrlKey, String longUrl);

    String findByKey(String shortUrlKey);

    void deleteByKey(String shortUrlKey);
}
