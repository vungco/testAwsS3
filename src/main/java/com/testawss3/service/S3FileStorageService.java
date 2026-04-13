package com.testawss3.service;

import com.testawss3.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileStorageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void upload(String objectKey, MultipartFile file) throws IOException {
        String bucket = requireBucket();
        String key = normalizeKey(objectKey);
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .build();
        s3Client.putObject(put, RequestBody.fromBytes(file.getBytes()));
        log.debug("Uploaded s3://{}/{}", bucket, key);
    }

    public byte[] download(String objectKey) {
        String bucket = requireBucket();
        String key = normalizeKey(objectKey);
        GetObjectRequest get = GetObjectRequest.builder().bucket(bucket).key(key).build();
        return s3Client.getObjectAsBytes(get).asByteArray();
    }

    public void delete(String objectKey) {
        String bucket = requireBucket();
        String key = normalizeKey(objectKey);
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        log.debug("Deleted s3://{}/{}", bucket, key);
    }

    public List<String> listKeys(String prefix) {
        String bucket = requireBucket();
        String p = StringUtils.hasText(prefix) ? prefix : "";
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket(bucket).prefix(p).build();
        return s3Client.listObjectsV2(req).contents().stream().map(S3Object::key).toList();
    }

    private String requireBucket() {
        if (!StringUtils.hasText(s3Properties.getBucket())) {
            throw new IllegalStateException("aws.s3.bucket chưa được cấu hình trong application.yaml");
        }
        return s3Properties.getBucket().trim();
    }

    private static String normalizeKey(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            throw new IllegalArgumentException("objectKey không được để trống");
        }
        return objectKey.trim().replaceAll("^/+", "");
    }
}
