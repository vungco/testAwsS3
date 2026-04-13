package com.testawss3.benchmark.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SeedResponse {
    long rowsInserted;
    long jpaInsertMs;
    long elasticsearchIndexMs;
    long totalMs;
}
