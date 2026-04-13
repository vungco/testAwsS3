package com.testawss3.benchmark.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableConfigurationProperties(BenchmarkProperties.class)
@EnableElasticsearchRepositories(basePackages = "com.testawss3.benchmark.es")
public class BenchmarkConfig {
}
