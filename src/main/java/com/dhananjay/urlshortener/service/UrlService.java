package com.dhananjay.urlshortener.service;

import com.dhananjay.urlshortener.dto.ShortenRequest;
import com.dhananjay.urlshortener.dto.ShortenResponse;
import com.dhananjay.urlshortener.dto.UrlResponse;
import com.dhananjay.urlshortener.entity.UrlMapping;
import com.dhananjay.urlshortener.exception.AliasAlreadyExistsException;
import com.dhananjay.urlshortener.exception.UrlExpiredException;
import com.dhananjay.urlshortener.exception.UrlNotFoundException;
import com.dhananjay.urlshortener.repository.UrlRepository;
import com.dhananjay.urlshortener.util.Base62Util;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${app.base-url}")
    private String baseUrl;

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private final UrlRepository repository;
    public ShortenResponse createShortUrl(ShortenRequest request) {

        log.info("Creating short URL for: {}", request.getUrl());
        UrlMapping shortUrl = UrlMapping.builder()
                .originalUrl(request.getUrl())
                .createdAt(LocalDateTime.now())
                .expiryDate(request.getExpiryDate())
                .clickCount(0L)
                .build();

        String shortCode;
        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {

            boolean aliasExists = repository
                    .findByShortCode(request.getCustomAlias())
                    .isPresent();

            if (aliasExists) {
                log.error("Custom alias already exists: {}", request.getCustomAlias());
                throw new AliasAlreadyExistsException("Custom alias already exists");
            }

            shortCode = request.getCustomAlias();

        } else {
            shortUrl = repository.save(shortUrl);
            shortCode = Base62Util.encode(shortUrl.getId());
        }

        shortUrl.setShortCode(shortCode);
        repository.save(shortUrl);

        String generatedShortUrl = baseUrl + "/" + shortCode;
        String qrCodeUrl = baseUrl + "/api/qr/" + shortCode;
        log.info("Short URL created successfully: {}", generatedShortUrl);

        return ShortenResponse.builder()
                .originalUrl(request.getUrl())
                .shortUrl(generatedShortUrl)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }
    public String getOriginalUrl(String shortCode) {

        log.info("Fetching original URL for code: {}", shortCode);

        UrlMapping shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.error("Short URL not found: {}", shortCode);
                    return new UrlNotFoundException("Short URL not found");
                });

        if (shortUrl.getExpiryDate() != null && shortUrl.getExpiryDate().isBefore(LocalDateTime.now())) {

            log.error("Short URL expired: {}", shortCode);
            throw new UrlExpiredException("Short URL has expired");
        }

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        repository.save(shortUrl);
        log.info("Redirecting to original URL: {}", shortUrl.getOriginalUrl());
        return shortUrl.getOriginalUrl();
    }

    public List<UrlResponse> getAllUrls() {

        return repository.findAll()
                .stream()
                .map(urlMapping -> UrlResponse.builder()
                        .id(urlMapping.getId())
                        .originalUrl(urlMapping.getOriginalUrl())
                        .shortCode(urlMapping.getShortCode())
                        .shortUrl(baseUrl + "/" + urlMapping.getShortCode())
                        .clickCount(urlMapping.getClickCount())
                        .createdAt(urlMapping.getCreatedAt())
                        .expiryDate(urlMapping.getExpiryDate())
                        .build())
                .toList();
    }

    public void deleteUrl(Long id) {

        UrlMapping urlMapping = repository
                .findById(id)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        repository.delete(urlMapping);

        log.info("URL deleted successfully: {}", id);
    }
}
