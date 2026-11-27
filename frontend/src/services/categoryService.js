import { productApi } from "./api";

export const categoryService = {
  getAll: async () => {
    const response = await productApi.get("/categories");
    return response.data;
  },

  getById: async (id) => {
    const response = await productApi.get(`/categories/${id}`);
    return response.data;
  },

  create: async (data) => {
    const response = await productApi.post("/categories", data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await productApi.put(`/categories/${id}`, data);
    return response.data;
  },

  delete: async (id) => {
    const response = await productApi.delete(`/categories/${id}`);
    return response.data;
  }
};
