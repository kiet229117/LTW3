import { Link, useLocation } from "react-router-dom";

export default function OrderSuccessPage() {
  const location = useLocation();
  const order = location.state?.order;

  return (
    <div className="success-card">
      <div className="success-badge">✓</div>
      <h1 className="page-title" style={{ marginBottom: "8px" }}>
        Đặt hàng nhập kho thành công!
      </h1>
      <p style={{ color: "var(--text-muted)" }}>
        Đơn đặt hàng (PO) đã được Order Service xử lý đồng bộ với Product Service và lưu vào CSDL.
      </p>

      {order ? (
        <div className="order-summary-box">
          <p>
            <strong>Mã đơn hàng:</strong> {order.orderCode || `#${order.id}`}
          </p>
          <p>
            <strong>Nhà cung cấp:</strong> {order.supplierName}
          </p>
          <p>
            <strong>Trạng thái:</strong>{" "}
            <span className="badge badge-pending">{order.status}</span>
          </p>
          <p>
            <strong>Tổng tiền:</strong>{" "}
            <span style={{ color: "var(--danger)", fontWeight: 700 }}>
              {Number(order.totalAmount || 0).toLocaleString("vi-VN")} đ
            </span>
          </p>

          {order.items && order.items.length > 0 && (
            <div style={{ marginTop: "12px" }}>
              <strong>Chi tiết sản phẩm:</strong>
              <ul style={{ paddingLeft: "20px", marginTop: "6px" }}>
                {order.items.map((item) => (
                  <li key={item.id || item.productId}>
                    {item.productName} — SL: {item.quantity} ×{" "}
                    {Number(item.unitPrice || 0).toLocaleString("vi-VN")} đ ={" "}
                    <strong>
                      {Number(item.subTotal || 0).toLocaleString("vi-VN")} đ
                    </strong>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      ) : (
        <div className="order-summary-box">
          Không có dữ liệu đơn hàng gần nhất. Bạn có thể tạo đơn mới tại trang sản phẩm.
        </div>
      )}

      <div style={{ display: "flex", gap: "12px", justifyContent: "center" }}>
        <Link to="/products" className="btn btn-primary">
          Tiếp tục đặt hàng
        </Link>
        <Link to="/admin/orders" className="btn btn-outline">
          Xem danh sách đơn hàng
        </Link>
      </div>
    </div>
  );
}
