import { orderApi } from "./api";

export const orderService = {
  getAll: async () => {
    const response = await orderApi.get("/orders");
    return response.data;
  },

  getById: async (id) => {
    const response = await orderApi.get(`/orders/${id}`);
    return response.data;
  },

  create: async (data) => {
    const response = await orderApi.post("/orders", data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await orderApi.put(`/orders/${id}`, data);
    return response.data;
  },

  approve: async (id, approvedByUserId = 1) => {
    const response = await orderApi.put(`/orders/${id}/approve`, { approvedByUserId });
    return response.data;
  },

  receive: async (id) => {
    const response = await orderApi.put(`/orders/${id}/receive`);
    return response.data;
  },

  cancel: async (id) => {
    const response = await orderApi.put(`/orders/${id}/cancel`);
    return response.data;
  },

  delete: async (id) => {
    const response = await orderApi.delete(`/orders/${id}`);
    return response.data;
  }
};
