package com.testawss3.benchmark.service;

import com.testawss3.benchmark.config.BenchmarkProperties;
import com.testawss3.benchmark.dto.SearchCompareResponse;
import com.testawss3.benchmark.es.SearchItemDocument;
import com.testawss3.benchmark.jpa.SearchItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BenchmarkSearchService {

    private final SearchItemJpaRepository jpaRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BenchmarkProperties benchmarkProperties;

    public SearchCompareResponse compare(String query, Integer limitOverride) {
        if (!StringUtils.hasText(query)) {
            throw new IllegalArgumentException("Tham số q (query) không được để trống");
        }
        String q = query.trim();
        int limit = limitOverride != null ? limitOverride : benchmarkProperties.getTopLimit();
        limit = Math.min(Math.max(limit, 1), 500);

        long tDb = System.nanoTime();
        int dbHits = jpaRepository.searchLike(q, PageRequest.of(0, limit)).size();
        long dbMs = (System.nanoTime() - tDb) / 1_000_000L;

        long tEs = System.nanoTime();
        Criteria criteria = new Criteria("title").matches(q).or(new Criteria("body").matches(q));
        CriteriaQuery cq = new CriteriaQuery(criteria, PageRequest.of(0, limit));
        SearchHits<SearchItemDocument> hits = elasticsearchOperations.search(cq, SearchItemDocument.class);
        int esHits = hits.getSearchHits().size();
        long esMs = (System.nanoTime() - tEs) / 1_000_000L;

        Double factor = esMs > 0 ? (double) dbMs / (double) esMs : null;
        String note = "DB: LIKE hai phía trên title/body (full scan tốn kém). "
                + "ES: inverted index + match (minh họa tốc độ đọc tìm kiếm full-text). "
                + "Kết quả có thể khác nhau do cách tokenize/analyzer.";

        return SearchCompareResponse.builder()
                .query(q)
                .limit(limit)
                .dbTimeMs(dbMs)
                .esTimeMs(esMs)
                .dbHitCount(dbHits)
                .esHitCount(esHits)
                .dbSlowerThanEsFactor(factor)
                .note(note)
                .build();
    }
}
