import { productApi } from "./api";

export const productService = {
  getAll: async () => {
    const response = await productApi.get("/products");
    return response.data;
  },

  getById: async (id) => {
    const response = await productApi.get(`/products/${id}`);
    return response.data;
  },

  create: async (data) => {
    const response = await productApi.post("/products", data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await productApi.put(`/products/${id}`, data);
    return response.data;
  },

  delete: async (id) => {
    const response = await productApi.delete(`/products/${id}`);
    return response.data;
  },

  search: async (keyword, category) => {
    const response = await productApi.get("/products/search", {
      params: { keyword, category }
    });
    return response.data;
  }
};
