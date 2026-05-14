package com.dhananjay.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ShortenResponse {
    private String originalUrl;
    private String shortUrl;
    private String qrCodeUrl;
}
