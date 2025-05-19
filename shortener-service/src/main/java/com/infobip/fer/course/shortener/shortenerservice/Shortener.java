package com.infobip.fer.course.shortener.shortenerservice;

import com.infobip.fer.course.shortener.shortenerservice.exception.FailedShorteningException;
import com.infobip.fer.course.shortener.shortenerservice.storage.UrlEntity;
import com.infobip.fer.course.shortener.shortenerservice.storage.UrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

@Service
public class Shortener {

    private static final long MAX_ATTEMPTS = 1000L;

    private final Random randomSeed;
    private final UrlRepository urlRepository;

    public Shortener(Random randomSeed, UrlRepository urlRepository) {
        this.randomSeed = randomSeed;
        this.urlRepository = urlRepository;
    }

    public String shorten(String fullUrl, String customerId) {
        var hash = hash(fullUrl, customerId);
        return tryToFindUniqueShortCode(hash, fullUrl, customerId);
    }

    /**
     * hash metoda generira jednostavan hash koji uključuje customerId i fullUrl
     * koristeći MD5 algoritam. Bitno je da se oba argumenta koriste kao ulazni
     * podaci za hash.
     */
    private String hash(String longUrl, String customerId) {
        try {
            var combinedData = customerId + longUrl;
            var md5 = MessageDigest.getInstance("MD5");
            md5.update(combinedData.getBytes());
            var binaryHash = md5.digest();
            return Base64.getEncoder().encodeToString(binaryHash);
        } catch (NoSuchAlgorithmException e) {
            throw FailedShorteningException.becauseMd5HashingAlgorithmIsMissing(e);
        }
    }

    /**
     * tryToFindUniqueShortCode metoda pokušava za dani hash odabrati jedinstveni
     * short code koji još ne postoji u bazi. To rabi na naivan način, pokušavajući
     * spremiti entitet u bazu te odabirom novog skupa znamenki ukoliko prilikom
     * spremanja dođe do DataIntegrityViolationException iznimke.
     */
    private String tryToFindUniqueShortCode(String hash, String fullUrl, String customerId) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            var shortCode = pickRandomChars(hash);
            var entity = new UrlEntity(shortCode, fullUrl, customerId);
            try {
                urlRepository.saveAndFlush(entity);
                return shortCode;
            } catch (DataIntegrityViolationException ignored) {
                // Već postoji UrlEntity redak s istim shortCode-om u bazi,
                // pokušajmo odabrati novi niz od 6 znakova iz hasha.
            } catch (RuntimeException e) {
                throw FailedShorteningException.becauseOfUnexpectedDatabaseError(e);
            }
        }

        throw FailedShorteningException.becauseFindingUniqueShortCodeTookTooLong(hash, MAX_ATTEMPTS);
    }

    /**
     * pickRandomChars metoda prima cijeli hash i vraća nasumično odabranih 6 znakova
     * Na taj način se osigurava dužina short code-a.
     */
    private String pickRandomChars(String hash) {
        return randomSeed.ints(6, 0, hash.length())
                .map(hash::charAt)
                .collect(StringBuffer::new, StringBuffer::appendCodePoint, StringBuffer::append)
                .toString();
    }

}