import { productApi } from "./api";

export const brandService = {
  getAll: async () => {
    const response = await productApi.get("/brands");
    return response.data;
  },

  getById: async (id) => {
    const response = await productApi.get(`/brands/${id}`);
    return response.data;
  },

  create: async (data) => {
    const response = await productApi.post("/brands", data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await productApi.put(`/brands/${id}`, data);
    return response.data;
  },

  delete: async (id) => {
    const response = await productApi.delete(`/brands/${id}`);
    return response.data;
  }
};
