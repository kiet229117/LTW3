import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { productService } from "../../services/productService";
import { categoryService } from "../../services/categoryService";
import { brandService } from "../../services/brandService";
import { orderService } from "../../services/orderService";

export default function DashboardPage() {
  const [stats, setStats] = useState({
    categories: 0,
    brands: 0,
    products: 0,
    orders: 0,
    totalValue: 0
  });
  const [recentOrders, setRecentOrders] = useState([]);

  useEffect(() => {
    Promise.allSettled([
      categoryService.getAll(),
      brandService.getAll(),
      productService.getAll(),
      orderService.getAll()
    ]).then(([catRes, brRes, prodRes, ordRes]) => {
      const categories = catRes.status === "fulfilled" ? catRes.value : [];
      const brands = brRes.status === "fulfilled" ? brRes.value : [];
      const products = prodRes.status === "fulfilled" ? prodRes.value : [];
      const orders = ordRes.status === "fulfilled" ? ordRes.value : [];
      const totalValue = orders.reduce(
        (sum, o) => sum + Number(o.totalAmount || 0),
        0
      );
      setStats({
        categories: categories.length,
        brands: brands.length,
        products: products.length,
        orders: orders.length,
        totalValue
      });
      setRecentOrders(orders.slice(-5).reverse());
    });
  }, []);

  return (
    <div>
      <h1 className="page-title">Dashboard Quản Trị Kho</h1>
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Danh mục</div>
          <div className="stat-value">{stats.categories}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Thương hiệu</div>
          <div className="stat-value">{stats.brands}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Sản phẩm</div>
          <div className="stat-value">{stats.products}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Đơn nhập hàng (PO)</div>
          <div className="stat-value">{stats.orders}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Tổng giá trị đơn nhập</div>
          <div className="stat-value" style={{ color: "var(--danger)", fontSize: "1.35rem" }}>
            {stats.totalValue.toLocaleString("vi-VN")} đ
          </div>
        </div>
      </div>

      <div className="card">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "14px" }}>
          <h3 style={{ color: "var(--navy)" }}>Đơn nhập hàng gần đây</h3>
          <Link to="/admin/orders" className="btn btn-outline btn-sm">
            Xem tất cả →
          </Link>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Nhà cung cấp</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            {recentOrders.length === 0 ? (
              <tr>
                <td colSpan="4" style={{ textAlign: "center", color: "var(--text-muted)" }}>
                  Chưa có dữ liệu đơn hàng.
                </td>
              </tr>
            ) : (
              recentOrders.map((o) => (
                <tr key={o.id}>
                  <td><strong>{o.orderCode || `#${o.id}`}</strong></td>
                  <td>{o.supplierName}</td>
                  <td style={{ color: "var(--danger)", fontWeight: 600 }}>
                    {Number(o.totalAmount || 0).toLocaleString("vi-VN")} đ
                  </td>
                  <td><span className="badge badge-approved">{o.status}</span></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
