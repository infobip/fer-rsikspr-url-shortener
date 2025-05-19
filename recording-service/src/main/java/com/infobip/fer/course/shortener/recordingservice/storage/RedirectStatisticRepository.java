package com.infobip.fer.course.shortener.recordingservice.storage;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedirectStatisticRepository extends JpaRepository<RedirectStatisticEntity, RedirectId> {

    /**
     * incrementClickCount je primjer ručno napisano upita na bazu. Obratite
     * pozornost na kombinaciju anotacija potrebnih za izvršavanje update upita:
     */
    @Modifying
    @Transactional
    @Query("update RedirectStatisticEntity s set s.clickCount = s.clickCount + 1 where s.redirectId = :id")
    void incrementClickCount(@Param("id") RedirectId id);

    List<RedirectStatisticEntity> findRedirectStatisticEntitiesByCustomerId(String customerId);

}