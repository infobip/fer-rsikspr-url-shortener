package com.infobip.fer.course.shortener.redirectservice.storage;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<UrlEntity, String> {

    @Override
    @Cacheable("urlsCache")
        // Spring će automatski cachirati rezultate metode
    Optional<UrlEntity> findById(String shortCode);
}