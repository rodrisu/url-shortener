package com.example.shortener.repositories;

import com.example.shortener.entities.Url;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisRepository {

    private final RedisTemplate<String, Url> redisTemplate;

    public void save(Url url) {
        redisTemplate.opsForValue()
                .set(url.getKey(), url);
    }

    public Url findByKey(String shortUrlKey) {
        return redisTemplate.opsForValue()
                .get(shortUrlKey);
    }

    public void deleteByKey(String shortUrlKey) {
        redisTemplate.delete(shortUrlKey);
    }

}
