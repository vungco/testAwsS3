package com.testawss3.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class S3ObjectInfoResponse {
    List<String> keys;
}
