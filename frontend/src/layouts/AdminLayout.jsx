import { NavLink, Link, Outlet } from "react-router-dom";

export default function AdminLayout() {
  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <div className="admin-brand">⚙️ Warehouse Admin</div>
        <NavLink to="/admin" end className="admin-nav-link">
          Dashboard
        </NavLink>
        <NavLink to="/admin/categories" className="admin-nav-link">
          Quản lý danh mục
        </NavLink>
        <NavLink to="/admin/brands" className="admin-nav-link">
          Quản lý thương hiệu
        </NavLink>
        <NavLink to="/admin/products" className="admin-nav-link">
          Quản lý sản phẩm
        </NavLink>
        <NavLink to="/admin/orders" className="admin-nav-link">
          Quản lý đơn nhập hàng
        </NavLink>

        <div style={{ marginTop: "auto", paddingTop: "20px" }}>
          <Link to="/" className="admin-nav-link">
            ← Về trang Client
          </Link>
        </div>
      </aside>

      <main className="admin-content">
        <Outlet />
      </main>
    </div>
  );
}
