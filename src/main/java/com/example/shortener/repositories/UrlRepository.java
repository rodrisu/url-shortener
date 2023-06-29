package com.example.shortener.repositories;

import com.example.shortener.entities.Url;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository {

    void save(Url url);

    Optional<Url> getUrl(String urlKey);

    void delete(String urlKey);

    void update(Url url);
}
