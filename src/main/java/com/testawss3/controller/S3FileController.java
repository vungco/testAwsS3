package com.testawss3.controller;

import com.testawss3.dto.S3ObjectInfoResponse;
import com.testawss3.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "S3", description = "Upload / download / list / xóa object trên S3")
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3FileController {

    private final S3FileStorageService s3FileStorageService;

    @Operation(summary = "Upload file lên S3")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(
            @RequestParam("key") String key,
            @RequestParam("file") MultipartFile file) throws IOException {
        s3FileStorageService.upload(key, file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Tải object từ S3")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam("key") String key) {
        byte[] body = s3FileStorageService.download(key);
        String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encoded + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(body);
    }

    @Operation(summary = "Xóa object trên S3")
    @DeleteMapping("/object")
    public ResponseEntity<Void> delete(@RequestParam("key") String key) {
        s3FileStorageService.delete(key);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Liệt kê key trong bucket (theo prefix tùy chọn)")
    @GetMapping("/list")
    public S3ObjectInfoResponse list(@RequestParam(value = "prefix", required = false) String prefix) {
        return S3ObjectInfoResponse.builder()
                .keys(s3FileStorageService.listKeys(prefix))
                .build();
    }
}
