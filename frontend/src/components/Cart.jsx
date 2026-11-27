export default function Cart({
  items,
  supplierName,
  onSupplierChange,
  onUpdateQuantity,
  onRemoveItem,
  onCreateOrder,
  loading
}) {
  const totalAmount = items.reduce(
    (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0),
    0
  );

  return (
    <aside className="cart-panel">
      <div className="cart-title">🛒 Giỏ hàng ({items.length})</div>

      {items.length === 0 ? (
        <div className="cart-empty">
          Chưa có sản phẩm nào được chọn. Hãy chọn sản phẩm bên trái để tạo đơn nhập hàng.
        </div>
      ) : (
        <>
          <div>
            {items.map((item) => (
              <div key={item.id} className="cart-item">
                <div className="cart-item-info">
                  <div className="cart-item-name">{item.name}</div>
                  <div className="cart-item-price">
                    {Number(item.price || 0).toLocaleString("vi-VN")} đ
                  </div>
                  <div className="cart-qty-controls">
                    <button
                      type="button"
                      className="btn-qty"
                      onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}
                    >
                      -
                    </button>
                    <span>{item.quantity}</span>
                    <button
                      type="button"
                      className="btn-qty"
                      onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}
                    >
                      +
                    </button>
                  </div>
                </div>
                <button
                  type="button"
                  className="btn-remove"
                  onClick={() => onRemoveItem(item.id)}
                  title="Xóa sản phẩm"
                >
                  Xóa
                </button>
              </div>
            ))}
          </div>

          <div className="cart-supplier">
            <label htmlFor="supplierName">Nhà cung cấp</label>
            <input
              id="supplierName"
              type="text"
              placeholder="Nhập tên nhà cung cấp..."
              value={supplierName}
              onChange={(e) => onSupplierChange(e.target.value)}
            />
          </div>

          <div className="cart-total-row">
            <span>Tổng tiền dự kiến:</span>
            <span className="cart-total-amount">
              {totalAmount.toLocaleString("vi-VN")} đ
            </span>
          </div>

          <button
            type="button"
            className="btn btn-success btn-full"
            onClick={onCreateOrder}
            disabled={loading}
          >
            {loading ? "Đang xử lý đơn hàng..." : "✅ Đặt hàng nhập kho"}
          </button>
        </>
      )}
    </aside>
  );
}
