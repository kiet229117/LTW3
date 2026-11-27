import { useEffect, useState } from "react";
import { orderService } from "../../services/orderService";

function getStatusBadgeClass(status) {
  switch ((status || "").toUpperCase()) {
    case "APPROVED":
      return "badge badge-approved";
    case "RECEIVED":
      return "badge badge-received";
    case "CANCELLED":
      return "badge badge-cancelled";
    default:
      return "badge badge-pending";
  }
}

export default function OrderAdminPage() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    loadOrders();
  }, []);

  async function loadOrders() {
    try {
      const data = await orderService.getAll();
      setOrders(data);
    } catch (err) {
      setErrorMessage(err.response?.data?.message || "Không thể tải danh sách đơn hàng.");
    }
  }

  async function handleAction(actionFn, successMsg) {
    setMessage("");
    setErrorMessage("");
    try {
      await actionFn();
      setMessage(successMsg);
      loadOrders();
    } catch (err) {
      setErrorMessage(err.response?.data?.message || "Thao tác thất bại.");
    }
  }

  return (
    <div>
      <h1 className="page-title">Quản lý đơn đặt hàng nhập kho (PO)</h1>

      {message && <div className="alert-success">{message}</div>}
      {errorMessage && <div className="alert-error">{errorMessage}</div>}

      <div className="card" style={{ overflowX: "auto" }}>
        <table className="data-table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Nhà cung cấp</th>
              <th>Sản phẩm đặt nhập</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th style={{ width: "260px" }}>Xử lý nghiệp vụ</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: "center", color: "var(--text-muted)" }}>
                  Chưa có đơn hàng nào.
                </td>
              </tr>
            ) : (
              orders.map((order) => (
                <tr key={order.id}>
                  <td>
                    <strong>{order.orderCode || `#${order.id}`}</strong>
                  </td>
                  <td>{order.supplierName}</td>
                  <td>
                    {order.items && order.items.length > 0 ? (
                      <ul style={{ paddingLeft: "16px", margin: 0 }}>
                        {order.items.map((item) => (
                          <li key={item.id || item.productId}>
                            {item.productName} (×{item.quantity})
                          </li>
                        ))}
                      </ul>
                    ) : (
                      "-"
                    )}
                  </td>
                  <td style={{ color: "var(--danger)", fontWeight: 700 }}>
                    {Number(order.totalAmount || 0).toLocaleString("vi-VN")} đ
                  </td>
                  <td>
                    <span className={getStatusBadgeClass(order.status)}>
                      {order.status}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
                      {order.status === "PENDING" && (
                        <button
                          type="button"
                          className="btn btn-primary btn-sm"
                          onClick={() =>
                            handleAction(
                              () => orderService.approve(order.id),
                              `Đã duyệt đơn ${order.orderCode}`
                            )
                          }
                        >
                          Duyệt
                        </button>
                      )}
                      {order.status === "APPROVED" && (
                        <button
                          type="button"
                          className="btn btn-success btn-sm"
                          onClick={() =>
                            handleAction(
                              () => orderService.receive(order.id),
                              `Đã xác nhận nhập kho đơn ${order.orderCode}`
                            )
                          }
                        >
                          Nhập kho
                        </button>
                      )}
                      {order.status !== "RECEIVED" && order.status !== "CANCELLED" && (
                        <button
                          type="button"
                          className="btn btn-outline btn-sm"
                          onClick={() =>
                            handleAction(
                              () => orderService.cancel(order.id),
                              `Đã hủy đơn ${order.orderCode}`
                            )
                          }
                        >
                          Hủy
                        </button>
                      )}
                      <button
                        type="button"
                        className="btn btn-danger btn-sm"
                        onClick={() => {
                          if (confirm("Xóa đơn hàng này?")) {
                            handleAction(
                              () => orderService.delete(order.id),
                              `Đã xóa đơn ${order.orderCode}`
                            );
                          }
                        }}
                      >
                        Xóa
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
