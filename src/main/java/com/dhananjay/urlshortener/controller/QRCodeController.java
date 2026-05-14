package com.dhananjay.urlshortener.controller;

import com.dhananjay.urlshortener.service.QRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
public class QRCodeController {

    private final QRCodeService qrCodeService;
    @GetMapping(value = "/{shortCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getQRCode(@PathVariable String shortCode) throws WriterException, IOException {
        String shortUrl = "http://localhost:8080/" + shortCode;
        return qrCodeService.generateQRCode(shortUrl);
    }
}
