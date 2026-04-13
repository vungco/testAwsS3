package com.testawss3.benchmark.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SearchCompareResponse {
    String query;
    int limit;
    long dbTimeMs;
    long esTimeMs;
    int dbHitCount;
    int esHitCount;
    /** dbTimeMs / esTimeMs khi esTimeMs > 0 (ước lượng DB chậm gấp mấy lần). */
    Double dbSlowerThanEsFactor;
    String note;
}
