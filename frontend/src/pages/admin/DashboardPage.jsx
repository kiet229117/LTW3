import { useEffect, useState } from "react";
import { productService } from "../../services/productService";
import { orderService } from "../../services/orderService";

export default function DashboardPage() {
  const [stats, setStats] = useState({ products: 0, orders: 0, totalValue: 0 });

  useEffect(() => {
    Promise.allSettled([productService.getAll(), orderService.getAll()]).then(
      ([prodRes, ordRes]) => {
        const products = prodRes.status === "fulfilled" ? prodRes.value : [];
        const orders = ordRes.status === "fulfilled" ? ordRes.value : [];
        const totalValue = orders.reduce(
          (sum, o) => sum + Number(o.totalAmount || 0),
          0
        );
        setStats({
          products: products.length,
          orders: orders.length,
          totalValue
        });
      }
    );
  }, []);

  return (
    <div>
      <h1 className="page-title">Dashboard Quản Trị Kho</h1>
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Tổng số sản phẩm</div>
          <div className="stat-value">{stats.products}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Tổng đơn nhập hàng (PO)</div>
          <div className="stat-value">{stats.orders}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Tổng giá trị đơn nhập</div>
          <div className="stat-value" style={{ color: "var(--danger)" }}>
            {stats.totalValue.toLocaleString("vi-VN")} đ
          </div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: "8px", color: "var(--navy)" }}>
          Trạng thái kết nối Microservices
        </h3>
        <p style={{ color: "var(--text-muted)" }}>
          Product Service: <code>{import.meta.env.VITE_PRODUCT_API_URL || "http://localhost:8081/api"}</code>
          <br />
          Order Service: <code>{import.meta.env.VITE_ORDER_API_URL || "http://localhost:8082/api"}</code>
        </p>
      </div>
    </div>
  );
}
