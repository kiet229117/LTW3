import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { productService } from "../../services/productService";
import { orderService } from "../../services/orderService";

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [supplierName, setSupplierName] = useState("Công ty TNHH Thiết bị Số Hoàng Gia");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    productService
      .getById(id)
      .then((data) => setProduct(data))
      .catch((err) =>
        setErrorMessage(err.response?.data?.message || "Không tìm thấy sản phẩm.")
      )
      .finally(() => setLoading(false));
  }, [id]);

  async function handleQuickOrder() {
    try {
      setSubmitting(true);
      const result = await orderService.create({
        supplierName: supplierName.trim() || "Nhà cung cấp mặc định",
        items: [{ productId: product.id, quantity: Math.max(1, Number(quantity) || 1) }]
      });
      navigate("/order-success", { state: { order: result } });
    } catch (err) {
      alert(err.response?.data?.message || "Không thể tạo đơn đặt hàng.");
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) return <div className="card">Đang tải thông tin sản phẩm...</div>;
  if (errorMessage || !product) {
    return (
      <div className="card">
        <div className="alert-error">{errorMessage || "Không tìm thấy sản phẩm."}</div>
        <Link to="/products" className="btn btn-outline">
          ← Quay lại danh sách
        </Link>
      </div>
    );
  }

  const imageUrl =
    product.imageUrls && product.imageUrls.length > 0
      ? product.imageUrls[0]
      : "https://via.placeholder.com/400x300?text=No+Image";

  return (
    <div>
      <Link to="/products" className="btn btn-outline btn-sm" style={{ marginBottom: "18px" }}>
        ← Quay lại danh sách sản phẩm
      </Link>

      <div className="card" style={{ display: "grid", gridTemplateColumns: "340px 1fr", gap: "28px" }}>
        <div className="product-img-wrap" style={{ height: "280px" }}>
          <img src={imageUrl} alt={product.name} />
        </div>

        <div>
          <span className="product-sku">{product.sku}</span>
          <h1 className="page-title" style={{ marginTop: "6px", marginBottom: "10px" }}>
            {product.name}
          </h1>
          <p style={{ color: "var(--text-muted)", marginBottom: "12px" }}>
            Danh mục: <strong>{product.categoryName || product.category}</strong> • Thương hiệu:{" "}
            <strong>{product.brandName || "Chưa cập nhật"}</strong> • ĐVT:{" "}
            <strong>{product.unit}</strong>
          </p>
          <div className="product-price" style={{ fontSize: "1.6rem", marginBottom: "18px" }}>
            {Number(product.price || 0).toLocaleString("vi-VN")} đ
          </div>

          <div className="form-grid" style={{ maxWidth: "480px" }}>
            <div className="form-group">
              <label>Số lượng nhập</label>
              <input
                type="number"
                min="1"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Nhà cung cấp</label>
              <input
                type="text"
                value={supplierName}
                onChange={(e) => setSupplierName(e.target.value)}
              />
            </div>
          </div>

          <button
            type="button"
            className="btn btn-success"
            onClick={handleQuickOrder}
            disabled={submitting}
          >
            {submitting ? "Đang tạo đơn..." : "✅ Đặt nhập nhanh sản phẩm này"}
          </button>
        </div>
      </div>
    </div>
  );
}
