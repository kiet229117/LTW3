import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <div>
      <section className="hero-card">
        <h1>Hệ thống Quản lý Kho Nhập Hàng</h1>
        <p>
          Ứng dụng kiến trúc Microservices kết hợp Spring Boot (RestClient, DTO, Validation) và ReactJS.
          Tra cứu danh mục sản phẩm và thực hiện quy trình tạo đơn đặt hàng nhập kho (PO) đồng bộ giữa Product Service &amp; Order Service.
        </p>
        <div className="hero-actions">
          <Link to="/products" className="btn btn-success">
            Xem danh sách sản phẩm &amp; Đặt hàng
          </Link>
          <Link to="/admin" className="btn btn-outline">
            Vào trang quản trị (Admin)
          </Link>
        </div>
      </section>

      <section className="feature-grid">
        <div className="feature-card">
          <h3>Product Service (Port 8081)</h3>
          <p>
            Cung cấp danh mục, thương hiệu, sản phẩm, hình ảnh và đơn giá chuẩn hóa qua DTO &amp; Validation.
          </p>
        </div>
        <div className="feature-card">
          <h3>Order Service (Port 8082)</h3>
          <p>
            Giao tiếp đồng bộ bằng RestClient với Product Service để kiểm tra sản phẩm, tính tổng tiền và lưu đơn hàng.
          </p>
        </div>
        <div className="feature-card">
          <h3>ReactJS Client &amp; Admin</h3>
          <p>
            Kết nối trực tiếp các Microservices qua Axios, cấu hình CORS tập trung và biến môi trường Vite.
          </p>
        </div>
      </section>
    </div>
  );
}
