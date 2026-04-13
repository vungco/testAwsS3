package com.testawss3.benchmark.service;

import com.testawss3.benchmark.config.BenchmarkProperties;
import com.testawss3.benchmark.dto.SeedResponse;
import com.testawss3.benchmark.es.SearchItemDocument;
import com.testawss3.benchmark.es.SearchItemEsRepository;
import com.testawss3.benchmark.jpa.SearchItemEntity;
import com.testawss3.benchmark.jpa.SearchItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class BenchmarkSeedService {

    private static final String[] VOCAB = {
            "java", "spring", "elasticsearch", "database", "mysql", "postgres", "h2", "index", "query",
            "search", "fulltext", "inverted", "token", "analyzer", "shard", "cluster", "node", "latency",
            "benchmark", "throughput", "disk", "memory", "cpu", "thread", "pool", "cache", "buffer",
            "transaction", "commit", "rollback", "orm", "jpa", "hibernate", "jdbc", "sql", "like", "match",
            "document", "field", "mapping", "bulk", "reindex", "vietnam", "hanoi", "saigon", "cloud",
            "aws", "s3", "lambda", "microservice", "rest", "api", "json", "yaml", "docker", "compose",
            "linux", "kernel", "network", "http", "tcp", "tls", "auth", "oauth", "jwt", "session",
            "logging", "metric", "trace", "observability", "prometheus", "grafana", "kibana", "lucene"
    };

    private final SearchItemJpaRepository jpaRepository;
    private final SearchItemEsRepository esRepository;
    private final BenchmarkProperties benchmarkProperties;

    @Transactional
    public SeedResponse seed(int requestedSize) {
        int size = Math.min(Math.max(requestedSize, 1), benchmarkProperties.getMaxSeedSize());
        long tAll = System.nanoTime();

        esRepository.deleteAll();
        jpaRepository.deleteAll();

        int batch = Math.max(100, benchmarkProperties.getBatchSize());
        long tJpa = System.nanoTime();
        List<SearchItemEntity> buf = new ArrayList<>(batch);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            SearchItemEntity e = new SearchItemEntity();
            e.setTitle(buildTitle(rnd));
            e.setBody(buildBody(rnd));
            buf.add(e);
            if (buf.size() >= batch) {
                jpaRepository.saveAll(buf);
                buf.clear();
            }
        }
        if (!buf.isEmpty()) {
            jpaRepository.saveAll(buf);
        }
        long jpaMs = msSince(tJpa);

        long tEs = System.nanoTime();
        reindexFromDb();
        long esMs = msSince(tEs);

        return SeedResponse.builder()
                .rowsInserted(size)
                .jpaInsertMs(jpaMs)
                .elasticsearchIndexMs(esMs)
                .totalMs(msSince(tAll))
                .build();
    }

    public void reindexFromDb() {
        esRepository.deleteAll();
        int pageSize = 1_000;
        int page = 0;
        Page<SearchItemEntity> slice;
        do {
            slice = jpaRepository.findAll(PageRequest.of(page, pageSize));
            List<SearchItemDocument> docs = slice.getContent().stream()
                    .map(e -> SearchItemDocument.builder()
                            .id(e.getId())
                            .title(e.getTitle())
                            .body(e.getBody())
                            .build())
                    .toList();
            if (!docs.isEmpty()) {
                esRepository.saveAll(docs);
            }
            page++;
        } while (slice.hasNext());
    }

    private static String buildTitle(ThreadLocalRandom rnd) {
        int n = rnd.nextInt(4, 10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(VOCAB[rnd.nextInt(VOCAB.length)]);
        }
        if (rnd.nextInt(5) == 0) {
            sb.append(" benchmark");
        }
        return sb.toString();
    }

    private static String buildBody(ThreadLocalRandom rnd) {
        int n = rnd.nextInt(40, 120);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(VOCAB[rnd.nextInt(VOCAB.length)]);
        }
        return sb.toString();
    }

    private static long msSince(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000L;
    }
}
