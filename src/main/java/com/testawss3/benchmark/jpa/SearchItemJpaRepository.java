package com.testawss3.benchmark.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchItemJpaRepository extends JpaRepository<SearchItemEntity, Long> {

    /**
     * Tìm kiểu SQL thông thường: LIKE hai phía — không tận dụng index B-Tree tốt, đặc biệt với dữ liệu lớn.
     */
    @Query("""
            SELECT e FROM SearchItemEntity e
            WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(e.body) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    List<SearchItemEntity> searchLike(@Param("q") String q, Pageable pageable);
}
