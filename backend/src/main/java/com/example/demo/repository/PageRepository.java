package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PageEntity;

import jakarta.transaction.Transactional;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, String> {

        List<PageEntity> findBySentimentTag(String sentimentTag);

        List<PageEntity> findBySentimentTagAndCreateTimeBetween(
                        String sentimentTag,
                        LocalDateTime startDate,
                        LocalDateTime endDate);

        @Transactional
        int deleteBySentimentTagAndCreateTimeBetween(String sentimentTag, LocalDateTime start, LocalDateTime end);

        // int deleteBySentimentTagAndSnameAndPostTimeBetween(String sentimentTag, LocalDateTime start, LocalDateTime end,
                        // String sName);

        List<PageEntity> findBySentimentTagAndPostTimeBetween(
                        String sentimentTag,
                        LocalDateTime start,
                        LocalDateTime end,
                        Pageable pageable);

        List<PageEntity> findByPostTimeBetween(LocalDateTime start, LocalDateTime end);
}
