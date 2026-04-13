package com.testawss3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@RequiredArgsConstructor
public class AwsS3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        var builder = S3Client.builder();
        if (StringUtils.hasText(s3Properties.getRegion())) {
            builder.region(Region.of(s3Properties.getRegion().trim()));
        }
        String access = trimToNull(s3Properties.getAccessKey());
        String secret = trimToNull(s3Properties.getSecretKey());
        if (access != null && secret != null) {
            String token = trimToNull(s3Properties.getSessionToken());
            var credentials = token != null
                    ? AwsSessionCredentials.create(access, secret, token)
                    : AwsBasicCredentials.create(access, secret);
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        } else if (access != null || secret != null) {
            throw new IllegalStateException(
                    "aws.s3.access-key và aws.s3.secret-key phải cùng có hoặc cùng để trống (dùng default credential chain).");
        }
        return builder.build();
    }

    private static String trimToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
