package com.testawss3.benchmark.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchItemEsRepository extends ElasticsearchRepository<SearchItemDocument, Long> {
}
