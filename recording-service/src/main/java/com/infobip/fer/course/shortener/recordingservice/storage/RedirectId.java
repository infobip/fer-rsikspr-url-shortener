package com.infobip.fer.course.shortener.recordingservice.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RedirectId implements Serializable {

    @Serial
    private static final long serialVersionUID = -4939787036493847106L;

    @NotNull
    @Size(max = 32)
    @Column(nullable = false, name = "short_code", length = 32)
    private String shortCode;

    @NotNull
    @Size(max = 512)
    @Column(nullable = false, name = "user_agent", length = 512)
    private String userAgent;

    public RedirectId() {
    }

    public RedirectId(String shortCode, String userAgent) {
        this.shortCode = shortCode;
        this.userAgent = userAgent;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "RedirectId{" +
                "shortCode='" + shortCode + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RedirectId that = (RedirectId) o;
        return Objects.equals(shortCode, that.shortCode) && Objects.equals(userAgent, that.userAgent);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(shortCode);
        result = 31 * result + Objects.hashCode(userAgent);
        return result;
    }
}
