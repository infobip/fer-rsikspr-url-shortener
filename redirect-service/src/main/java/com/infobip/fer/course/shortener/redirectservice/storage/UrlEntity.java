package com.infobip.fer.course.shortener.redirectservice.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "urls")
public class UrlEntity {

    @Id
    @Column(nullable = false, name = "short_code")
    private String shortCode;

    @Column(nullable = false, name = "full_url")
    private String fullUrl;

    @Column(nullable = false, name = "customer_id")
    private String customerId;

    public UrlEntity() {
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

    @Override
    public String toString() {
        return "UrlEntity{" +
                "shortCode='" + shortCode + '\'' +
                ", fullUrl='" + fullUrl + '\'' +
                ", customerId='" + customerId + '\'' +
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
