package com.infobip.fer.course.shortener.recordingservice.storage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Persistable;

import java.util.Objects;

@Entity
@Table(name = "redirects")
public class RedirectStatisticEntity implements Persistable<RedirectId> {

    // Primjer kompozitnog ključa koji se sastoji od 2 kolone u bazi:
    @EmbeddedId
    private RedirectId redirectId;

    @NotNull
    @Size(max = 2048)
    @Column(nullable = false, name = "full_url", length = 2048)
    private String fullUrl;

    @NotNull
    @Size(max = 512)
    @Column(nullable = false, name = "customer_id", length = 512)
    private String customerId;

    @NotNull
    @Column(nullable = false, name = "click_count")
    private Long clickCount;

    public RedirectStatisticEntity() {
    }

    public RedirectStatisticEntity(RedirectId redirectId, String fullUrl, String customerId, Long clickCount) {
        this.redirectId = redirectId;
        this.fullUrl = fullUrl;
        this.customerId = customerId;
        this.clickCount = clickCount;
    }

    public RedirectId getRedirectId() {
        return redirectId;
    }

    public void setRedirectId(RedirectId redirectId) {
        this.redirectId = redirectId;
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

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    @Override
    public RedirectId getId() {
        return redirectId;
    }

    @Override
    public boolean isNew() {
        // Isti problem kao i kod UrlEntity klase u shortener-serviceu,
        // ovdje ga rješavamo na malo jednostavniji način. S ovom implementacijom
        // svaki poziv na save metodu u repozitoriju će rezultirati sa inser upitom
        // na bazu. U ovom slučaju nam je to dovoljno je jedini update koji nam treba
        // je implementiran kroz ručno napisani query u repozitoriju.
        return true;
    }

    @Override
    public String toString() {
        return "RedirectStatisticEntity{" +
                "redirectId=" + redirectId +
                ", fullUrl='" + fullUrl + '\'' +
                ", customerId='" + customerId + '\'' +
                ", clickCount=" + clickCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RedirectStatisticEntity that = (RedirectStatisticEntity) o;
        return Objects.equals(redirectId, that.redirectId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(redirectId);
    }
}
