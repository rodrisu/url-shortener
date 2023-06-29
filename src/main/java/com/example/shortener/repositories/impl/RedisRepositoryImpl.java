package com.example.shortener.repositories.impl;

import com.example.shortener.repositories.RedisRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String shortUrlKey, String longUrl) {
        redisTemplate.opsForValue()
                .set(shortUrlKey, longUrl);
    }

    public String findByKey(String shortUrlKey) {
        return redisTemplate.opsForValue()
                .get(shortUrlKey);
    }

    public void deleteByKey(String shortUrlKey) {
        redisTemplate.delete(shortUrlKey);
    }

}
