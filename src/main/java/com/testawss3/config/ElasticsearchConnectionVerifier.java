package com.testawss3.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Ensures the app does not stay up without a working Elasticsearch cluster (fail-fast).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchConnectionVerifier implements ApplicationRunner {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            if (!elasticsearchClient.ping().value()) {
                throw new IllegalStateException("ping returned false");
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot connect to Elasticsearch at startup (check SPRING_ELASTICSEARCH_URIS and that the cluster is up): "
                            + e.getMessage(),
                    e);
        }
        log.info("Elasticsearch connection verified (ping OK)");
    }
}
