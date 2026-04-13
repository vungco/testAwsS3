package com.testawss3.benchmark.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.benchmark")
public class BenchmarkProperties {

    /** Số bản ghi mặc định khi seed (POST /api/benchmark/seed không truyền size). */
    private int defaultSeedSize = 50_000;

    /** Giới hạn kết quả mỗi lần so sánh (DB và ES). */
    private int topLimit = 100;

    /** Kích thước batch khi insert JPA / index ES. */
    private int batchSize = 500;

    /** Không cho seed quá lớn (tránh OOM). */
    private int maxSeedSize = 200_000;
}
