package com.dhananjay.urlshortener.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
}