import axios from "axios";

// 後端 Spring Boot URL
const API_BASE = "http://localhost:8080/page";

// GET 根據 sentimentTag 取得文章
export const getPagesByTag = (tag) => {
  return axios.get(`${API_BASE}/getPagesByTag/${tag}`);
};

// POST 根據 sentimentTag + 時間區間查詢文章
export const findPagesByTag = (tag, requestBody) => {
  return axios.post(`${API_BASE}/findPages/${tag}`, requestBody);
};

// 如果後面要加新增、刪除功能，也可以放在這裡
export const addArticle = (data) => axios.post(`${API_BASE}/articles`, data);
export const deleteArticle = (id) => axios.delete(`${API_BASE}/articles/${id}`);
