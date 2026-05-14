package com.dhananjay.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShortenRequest {
    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    private String url;
    private String customAlias;
    private LocalDateTime expiryDate;
}
