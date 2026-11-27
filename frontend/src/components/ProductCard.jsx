import { useState } from "react";
import { Link } from "react-router-dom";

export default function ProductCard({ product, onAddToCart }) {
  const [quantity, setQuantity] = useState(1);

  const imageUrl =
    product.imageUrls && product.imageUrls.length > 0
      ? product.imageUrls[0]
      : "https://via.placeholder.com/300x200?text=No+Image";

  const handleAdd = () => {
    const qty = Math.max(1, Number(quantity) || 1);
    onAddToCart(product, qty);
    setQuantity(1);
  };

  return (
    <div className="product-card">
      <div className="product-img-wrap">
        <img src={imageUrl} alt={product.name} />
      </div>

      <span className="product-sku">{product.sku || `SP-${product.id}`}</span>
      <Link to={`/products/${product.id}`} className="product-name">
        {product.name}
      </Link>

      <div className="product-meta">
        {product.categoryName || product.category}
        {product.brandName ? ` • ${product.brandName}` : ""}
        {product.unit ? ` • ĐVT: ${product.unit}` : ""}
      </div>

      <div className="product-price">
        {Number(product.price || 0).toLocaleString("vi-VN")} đ
      </div>

      <div className="qty-row">
        <label htmlFor={`qty-${product.id}`}>Số lượng:</label>
        <input
          id={`qty-${product.id}`}
          type="number"
          min="1"
          className="qty-input"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
        />
      </div>

      <button
        type="button"
        className="btn btn-primary btn-full"
        onClick={handleAdd}
      >
        🛒 Thêm vào giỏ
      </button>
    </div>
  );
}
