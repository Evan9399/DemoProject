package com.example.demo.service;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.example.demo.entity.LogEntity;
import com.example.demo.repository.LogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private static final Logger logger = LogManager.getLogger(LogService.class);
    private LogEntity currentLog;

    public void startLog(String level, String message) {
        LocalDateTime now = LocalDateTime.now();
        // 建立log、存DB
        currentLog = LogEntity.builder()
                .level(level.toUpperCase())
                .message(message)
                .startTime(now)
                .endTime(now)
                .createdAt(now)
                .build();

        logToFramework(level, message);

        // 先寫入 DB，避免程式中途失敗
        try {
            logRepository.save(currentLog);
        } catch (Exception e) {
            logger.error("寫入 DB 失敗: {}", e.getMessage(), e);
        }
    }

    // 延續log累積
    public void appendLog(String level, String message) {
        if (currentLog != null) {
            String existing = currentLog.getMessage();

            currentLog.setMessage((existing == null ? "" : existing + "\n") + message);
            currentLog.setEndTime(LocalDateTime.now());

            try {
                logRepository.save(currentLog);
            } catch (Exception e) {
                logger.error("更新 DB 失敗: {}", e.getMessage(), e);
            }
        }
        logToFramework(level, message);
    }

    // 結束log存DB
    public void endLog(String level, String message) {
        if (currentLog != null) {
            String existing = currentLog.getMessage();
            currentLog.setMessage((existing == null ? "" : existing + "\n") + message);
            currentLog.setEndTime(LocalDateTime.now());

            try {
                logRepository.save(currentLog);
            } catch (Exception e) {
                logger.error("結束時寫入 DB 失敗: {}", e.getMessage(), e);
            }

            currentLog = null;
        }
        logToFramework(level, message);
    }

    // -----------------------------
    // 快速 log，只輸出到 Log4j2，不存 DB
    // -----------------------------
    public void logToFramework(String level, String message) {
        switch (level.toUpperCase()) {
            case "INFO":
                logger.info(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            default:
                logger.info(message);
        }
    }
}
