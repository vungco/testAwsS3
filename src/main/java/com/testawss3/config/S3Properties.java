package com.testawss3.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    /**
     * Tên bucket bắt buộc khi upload/list/delete.
     */
    private String bucket;

    /**
     * Region AWS, ví dụ ap-southeast-1. Nếu để trống SDK dùng default chain.
     */
    private String region;

    /**
     * Access key — thường map từ env {@code AWS_ACCESS_KEY_ID} (xem application.yaml).
     * Để trống thì SDK dùng default credential chain (env, ~/.aws/credentials, IAM role…).
     */
    private String accessKey;

    /**
     * Secret key — map từ env {@code AWS_SECRET_ACCESS_KEY}.
     */
    private String secretKey;

    /**
     * Session token (STS/temporary) — map từ env {@code AWS_SESSION_TOKEN}, có thể để trống.
     */
    private String sessionToken;
}
