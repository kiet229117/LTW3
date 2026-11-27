import { NavLink, Link } from "react-router-dom";

export default function Header() {
  return (
    <header className="client-header">
      <Link to="/" className="header-brand">
        <span>📦</span>
        <span>WarehousePro</span>
      </Link>

      <nav className="header-nav">
        <NavLink to="/" end>
          Trang chủ
        </NavLink>
        <NavLink to="/products">
          Sản phẩm &amp; Đặt hàng
        </NavLink>
      </nav>

      <div className="header-actions">
        <Link to="/admin" className="btn-admin-link">
          Trang quản trị (Admin)
        </Link>
      </div>
    </header>
  );
}
