import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import { orderService } from "../../services/orderService";
import ProductCard from "../../components/ProductCard";
import Cart from "../../components/Cart";

export default function ProductListPage() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [supplierName, setSupplierName] = useState("Công ty TNHH Thiết bị Số Hoàng Gia");
  const [keyword, setKeyword] = useState("");
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    fetchProducts();
  }, []);

  async function fetchProducts() {
    try {
      setLoadingProducts(true);
      setErrorMessage("");
      const data = await productService.getAll();
      setProducts(data);
    } catch (error) {
      setErrorMessage(
        error.response?.data?.message ||
          "Không thể tải danh sách sản phẩm. Vui lòng kiểm tra Product Service (port 8081)."
      );
    } finally {
      setLoadingProducts(false);
    }
  }

  function handleAddToCart(product, quantity) {
    setSelectedProducts((prev) => {
      const existing = prev.find((item) => item.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        );
      }
      return [...prev, { ...product, quantity }];
    });
  }

  function handleUpdateQuantity(productId, newQty) {
    if (newQty <= 0) {
      handleRemoveItem(productId);
      return;
    }
    setSelectedProducts((prev) =>
      prev.map((item) =>
        item.id === productId ? { ...item, quantity: newQty } : item
      )
    );
  }

  function handleRemoveItem(productId) {
    setSelectedProducts((prev) => prev.filter((item) => item.id !== productId));
  }

  async function handleCreateOrder() {
    if (selectedProducts.length === 0) {
      alert("Vui lòng chọn sản phẩm!");
      return;
    }

    const orderData = {
      supplierName: supplierName.trim() || "Nhà cung cấp mặc định",
      items: selectedProducts.map((product) => ({
        productId: product.id,
        quantity: Number(product.quantity)
      }))
    };

    try {
      setSubmitting(true);
      setErrorMessage("");
      const result = await orderService.create(orderData);
      setSelectedProducts([]);
      navigate("/order-success", { state: { order: result } });
    } catch (error) {
      const msg =
        error.response?.data?.message ||
        "Không thể đặt hàng! Vui lòng kiểm tra Order Service (port 8082) và Product Service (port 8081).";
      setErrorMessage(msg);
      alert(msg);
    } finally {
      setSubmitting(false);
    }
  }

  const filteredProducts = products.filter((p) => {
    if (!keyword.trim()) return true;
    const kw = keyword.toLowerCase();
    return (
      (p.name && p.name.toLowerCase().includes(kw)) ||
      (p.sku && p.sku.toLowerCase().includes(kw)) ||
      (p.categoryName && p.categoryName.toLowerCase().includes(kw))
    );
  });

  return (
    <div>
      <h1 className="page-title">Danh sách sản phẩm &amp; Tạo đơn nhập hàng</h1>

      {errorMessage && <div className="alert-error">{errorMessage}</div>}

      <div className="shop-layout">
        <div>
          <div className="filter-bar">
            <input
              type="text"
              placeholder="Tìm kiếm theo tên sản phẩm, mã SKU hoặc danh mục..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <button type="button" className="btn btn-outline" onClick={fetchProducts}>
              Tải lại
            </button>
          </div>

          {loadingProducts ? (
            <div className="card">Đang tải danh sách sản phẩm...</div>
          ) : filteredProducts.length === 0 ? (
            <div className="card">Không tìm thấy sản phẩm phù hợp.</div>
          ) : (
            <div className="product-grid">
              {filteredProducts.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onAddToCart={handleAddToCart}
                />
              ))}
            </div>
          )}
        </div>

        <Cart
          items={selectedProducts}
          supplierName={supplierName}
          onSupplierChange={setSupplierName}
          onUpdateQuantity={handleUpdateQuantity}
          onRemoveItem={handleRemoveItem}
          onCreateOrder={handleCreateOrder}
          loading={submitting}
        />
      </div>
    </div>
  );
}
