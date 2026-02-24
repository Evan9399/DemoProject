# DemoProject
Spring Boot backend system with scheduled tasks, batch processing, and database logging. | JPA | MySQL
📌 專案介紹
本專案為一個後端資料同步系統，使用 Spring Boot 開發。
主要功能為：
串接外部 API 取得文章資料
批次儲存至資料庫
提供條件查詢、更新、刪除 API
設計全域例外處理機制
設計資料庫 Log 紀錄系統
實作排程任務自動抓取資料

🧩 系統架構
Controller
↓
Service（業務邏輯）
↓
Repository（JPA）
↓
MySQL

⚙ 技術棧
Java 17
Spring Boot
Spring Data JPA
MySQL
RestTemplate
Log4j2
Scheduled Task

🚀 核心設計重點
1️⃣ 批次資料寫入優化
使用 saveAll() 進行批次寫入，減少資料庫 round-trip，提高效能。
2️⃣ 全域例外處理
使用 @RestControllerAdvice 統一回傳格式，避免重複 try-catch。
3️⃣ 排程任務設計
使用 @Scheduled(cron = "0 0 10 * * *")
每日定時同步資料，並加入例外保護避免排程中斷。
4️⃣ 自訂 LogService
同步 Log4j2 與 DB Log
API 流程記錄 start / append / end
即使發生例外仍可保存紀錄

🛠 如何啟動
建立 MySQL 資料庫
修改 application.yml 連線設定
mvn spring-boot:run
