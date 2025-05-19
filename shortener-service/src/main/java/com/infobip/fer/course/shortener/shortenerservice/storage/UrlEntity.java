package com.infobip.fer.course.shortener.shortenerservice.storage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "urls")
@EntityListeners(AuditingEntityListener.class)
public class UrlEntity implements Persistable<String> {

    @Id
    @NotNull
    @Size(max = 32)
    @Column(nullable = false, name = "short_code", length = 32)
    private String shortCode;

    @NotNull
    @Size(max = 2048)
    @Column(nullable = false, name = "full_url", length = 2048)
    private String fullUrl;

    @NotNull
    @Size(max = 512)
    @Column(nullable = false, name = "customer_id", length = 512)
    private String customerId;

    @NotNull
    @CreatedDate
    @Column(nullable = false, name = "created_date")
    private Instant createdDate;

    @Transient
    private boolean existsInDb;

    public UrlEntity() {
    }

    public UrlEntity(String shortCode, String fullUrl, String customerId) {
        this.shortCode = shortCode;
        this.fullUrl = fullUrl;
        this.customerId = customerId;
    }

    @PostLoad
    @PrePersist
    public void markAsExistingInDb() {
        // JPA repozitorij implementira save metodu tako da inserta entitet u bazu
        // ukoliko ne postoji, ili ga updatea ukoliko već postoji. U našem slučaju
        // poslovna logika u Shortener servisu očekuje da save postojećeg entiteta
        // vrati DataIntegrityViolationException iznimku umjesto da izvrši update. Da
        // bi to postigli implementiramo Persistable sučelje u kojem eksplicitno navodimo
        // je li entitet novi (u kojem slučaju će se izvršiti insert) ili već postoji
        // u bazi (u kojem slučaju će se izvršiti update). Stanje entiteta pratimo
        // pomoću varijable existsInDb koja se postavlja na true nakon što se entitet
        // učita iz baze (annotacija @PostLoad) ili nakon što se entitet spremi u bazu
        // (annotacija @PrePersist). Na taj način se entiteti kreirani pozivom konstruktora
        // izravno iz našeg aplikacijskog koda smatraju uvijek novima te za njih repozitorij
        // uvijek pokuša izvršiti insert.
        this.existsInDb = true;
    }

    @Override
    public String getId() {
        return shortCode;
    }

    @Override
    public boolean isNew() {
        return !existsInDb;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "UrlEntity{" +
                "shortCode='" + shortCode + '\'' +
                ", fullUrl='" + fullUrl + '\'' +
                ", customerId='" + customerId + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UrlEntity urlEntity = (UrlEntity) o;
        return Objects.equals(shortCode, urlEntity.shortCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shortCode);
    }

}
