Health Log System (健康日誌系統)

專案簡介
本專案為一個全端健康日誌管理系統，旨在透過數據分析提供用戶健康風險評估。系統具備紀錄、統計與視覺化功能，幫助用戶追蹤睡眠、步數與心情指標。

技術堆疊
後端: Java Spring Boot

資料庫: MySQL

前端: HTML5, TailwindCSS (via CDN), Chart.js

部署: GitHub (程式碼庫)

系統架構與資料流向
系統採用前後端分離架構，透過 REST API 進行數據交互。

交互流程說明
1.資料輸入: 用戶透過前端頁面輸入健康數據（睡眠時數、步數、心情得分）。
2.數據傳輸: 前端透過 fetch API 將 JSON 格式數據發送至 Spring Boot 後端。
3.後端處理: 接收請求後，將數據儲存至 MySQL 資料庫，並根據風險評估決策樹（Risk Decision Tree）計算風險等級。
4.視覺化呈現: 前端再次發起 GET 請求取得歷史資料，並利用 Chart.js 繪製雙 Y 軸折線圖，呈現 90 天內的趨勢變化。

系統架構圖 (Mermaid)
graph LR
    User[用戶] -- 輸入數據 --> Front[前端界面: HTML/Chart.js]
    Front -- REST API --> Back[後端: Spring Boot]
    Back -- 儲存/查詢 --> DB[(資料庫: MySQL)]
    Back -- 回傳 JSON --> Front

## 部署狀態說明
本專案已完成 Docker 化與 Railway 雲端環境配置。
由於 Railway 免費方案的網路限制與 MySQL 初始化延遲，線上 Demo 連結可能出現 502/504 錯誤，此為環境連線逾時所致。
本專案原始碼與 Dockerfile 設定皆已完整提交，助教可透過本地部署驗證功能：
- ./mvnw spring-boot:run
