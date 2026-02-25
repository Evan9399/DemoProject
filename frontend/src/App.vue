<script setup>
import { ref } from "vue";
import axios from "axios";

// 前綴 URL
const PAGE_API = "http://localhost:8080/page";
const AI_API = "http://localhost:8080/ai";

// 文章與批次摘要
const articles = ref([]);
const batchSummaries = ref([]);

// 查詢參數
const sentimentTag = ref("P");
const startDate = ref("");
const endDate = ref("");

// 更新文章
const updateId = ref("");
const updateField = ref("");
const updateValue = ref("");

// 錯誤訊息
const msg = ref("");

// -----------------------------
// 文章相關 API
// -----------------------------
const fetchNewPage = async () => {
  try {
    const res = await axios.get(`${PAGE_API}/fetchNewPage`);
    msg.value = `新增文章成功: 共 ${res.data.result.length} 筆`;
  } catch (err) {
    console.error(err);
    msg.value = "新增文章失敗";
  }
};

const fetchByTag = async () => {
  try {
    const res = await axios.get(
      `${PAGE_API}/getPagesByTag/${sentimentTag.value}`,
    );
    articles.value = res.data;
    msg.value = `查詢 ${articles.value.length} 筆文章`;
  } catch (err) {
    console.error(err);
    msg.value = "查詢文章失敗";
  }
};

const fetchByDateRange = async () => {
  try {
    const body = { startDate: startDate.value, endDate: endDate.value };
    const res = await axios.post(
      `${PAGE_API}/findPages/${sentimentTag.value}`,
      body,
    );
    articles.value = res.data;
    msg.value = `時間區間查詢: ${articles.value.length} 筆`;
  } catch (err) {
    console.error(err);
    msg.value = "時間區間查詢失敗";
  }
};

const deleteArticles = async () => {
  try {
    const body = { startTime: startDate.value, endTime: endDate.value };
    const res = await axios.delete(`${PAGE_API}/delete/${sentimentTag.value}`, {
      data: body,
    });
    msg.value = `刪除 ${res.data.deletedCount} 筆文章`;
    fetchByTag();
  } catch (err) {
    console.error(err);
    msg.value = "刪除失敗";
  }
};

const updateArticle = async () => {
  if (!updateId.value || !updateField.value) return;
  try {
    const body = {
      id: updateId.value,
      fields: { [updateField.value]: updateValue.value },
    };
    const res = await axios.put(`${PAGE_API}/update`, body);
    msg.value = res.data.message;
    fetchByTag();
  } catch (err) {
    console.error(err);
    msg.value = "更新文章失敗";
  }
};

// -----------------------------
// AI 相關 API
// -----------------------------
const fetchBatchSummaries = async () => {
  try {
    const body = { startDate: startDate.value, endDate: endDate.value };
    const res = await axios.post(
      `${AI_API}/batch-summaries/${sentimentTag.value}`,
      body,
    );
    batchSummaries.value = res.data.data;
    msg.value = `批次摘要完成: ${batchSummaries.value.length} 筆`;
  } catch (err) {
    console.error(err);
    msg.value = "批次摘要失敗";
  }
};

const exportDailyCsv = async () => {
  try {
    const res = await axios.get(`${AI_API}/exportDaily`, {
      params: { startDate: startDate.value, endDate: endDate.value },
      responseType: "blob",
    });
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute(
      "download",
      `DailySummary_${startDate.value}_to_${endDate.value}.csv`,
    );
    document.body.appendChild(link);
    link.click();
    msg.value = "CSV 下載完成";
  } catch (err) {
    console.error(err);
    msg.value = "CSV 下載失敗";
  }
};
</script>

<template>
  <div style="max-width: 800px; margin: auto">
    <h1>文章與 AI 摘要管理</h1>

    <div style="margin-bottom: 15px; color: green">{{ msg }}</div>

    <!-- 新增文章 -->
    <button @click="fetchNewPage">新增最新文章</button>

    <!-- sentimentTag 查詢 -->
    <div style="margin-top: 15px">
      <input v-model="sentimentTag" placeholder="輸入 P/N/M" />
      <button @click="fetchByTag">查詢文章</button>
    </div>

    <!-- 時間區間查詢 -->
    <div style="margin-top: 10px">
      <input type="date" v-model="startDate" /> ~
      <input type="date" v-model="endDate" />
      <button @click="fetchByDateRange">時間區間查詢</button>
      <button @click="deleteArticles" style="margin-left: 10px">
        刪除文章
      </button>
    </div>

    <!-- 更新文章 -->
    <div style="margin-top: 10px">
      <input v-model="updateId" placeholder="文章 ID" />
      <input v-model="updateField" placeholder="欄位名稱" />
      <input v-model="updateValue" placeholder="欄位值" />
      <button @click="updateArticle">更新文章</button>
    </div>

    <!-- AI 批次摘要 -->
    <div style="margin-top: 10px">
      <button @click="fetchBatchSummaries">生成批次摘要</button>
      <button @click="exportDailyCsv" style="margin-left: 10px">
        匯出每日摘要 CSV
      </button>
    </div>

    <!-- 顯示文章 -->
    <ul v-if="articles.length" style="margin-top: 15px">
      <li v-for="article in articles" :key="article.id">
        <strong>{{ article.title }}</strong> ({{ article.sentimentTag }})<br />
        {{ article.content }}<br />
        {{ article.postTime }}
      </li>
    </ul>

    <!-- 顯示批次摘要 -->
    <ul v-if="batchSummaries.length" style="margin-top: 15px">
      <li v-for="batch in batchSummaries" :key="batch.id">
        <strong>{{ batch.title }}</strong
        ><br />
        {{ batch.aiSummary }}
      </li>
    </ul>

    <div v-if="!articles.length && !batchSummaries.length">沒有資料</div>
  </div>
</template>
