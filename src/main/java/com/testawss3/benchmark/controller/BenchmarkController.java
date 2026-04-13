package com.testawss3.benchmark.controller;

import com.testawss3.benchmark.config.BenchmarkProperties;
import com.testawss3.benchmark.dto.SearchCompareResponse;
import com.testawss3.benchmark.dto.SeedResponse;
import com.testawss3.benchmark.service.BenchmarkSearchService;
import com.testawss3.benchmark.service.BenchmarkSeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/benchmark")
@RequiredArgsConstructor
@Tag(name = "Benchmark DB vs Elasticsearch", description = "Seed dữ liệu lớn và so sánh thời gian tìm kiếm")
public class BenchmarkController {

    private final BenchmarkSeedService seedService;
    private final BenchmarkSearchService searchService;
    private final BenchmarkProperties benchmarkProperties;

    @Operation(summary = "Xóa dữ liệu cũ, seed N bản ghi vào H2 + index Elasticsearch")
    @PostMapping("/seed")
    public ResponseEntity<SeedResponse> seed(
            @RequestParam(name = "size", required = false) Integer size) {
        int n = size != null ? size : benchmarkProperties.getDefaultSeedSize();
        return ResponseEntity.ok(seedService.seed(n));
    }

    @Operation(summary = "Đồng bộ lại toàn bộ từ DB sang Elasticsearch (sau khi sửa tay DB)")
    @PostMapping("/reindex")
    public ResponseEntity<Void> reindex() {
        seedService.reindexFromDb();
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "So sánh thời gian: SQL LIKE vs Elasticsearch match (cùng giới hạn limit)")
    @GetMapping("/compare")
    public SearchCompareResponse compare(
            @RequestParam("q") String q,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return searchService.compare(q, limit);
    }
}
