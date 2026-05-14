package com.dhananjay.urlshortener.controller;

import com.dhananjay.urlshortener.dto.ShortenRequest;
import com.dhananjay.urlshortener.dto.ShortenResponse;
import com.dhananjay.urlshortener.dto.UrlResponse;
import com.dhananjay.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/url")
public class UrlController {

    @GetMapping("/test")
    public String test(){
        return "Welcome !!";
    }

    private final UrlService shortUrlService;

    @PostMapping("/shorten")
    public ShortenResponse shortenUrl(@Valid @RequestBody ShortenRequest request) {
        return shortUrlService.createShortUrl(request);
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {

        String originalUrl = shortUrlService.getOriginalUrl(shortCode);

        response.sendRedirect(originalUrl);
    }

    @GetMapping("/all")
    public List<UrlResponse> getAllUrls() {

        return shortUrlService.getAllUrls();
    }

    @DeleteMapping("/{id}")
    public String deleteUrl(@PathVariable Long id) {

        shortUrlService.deleteUrl(id);

        return "URL deleted successfully";
    }
}
