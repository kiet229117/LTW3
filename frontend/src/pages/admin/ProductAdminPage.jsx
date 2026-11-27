import { useEffect, useState } from "react";
import { productService } from "../../services/productService";
import { categoryService } from "../../services/categoryService";
import { brandService } from "../../services/brandService";

const initialForm = {
  sku: "",
  name: "",
  unit: "Cái",
  price: "",
  status: "ACTIVE",
  categoryId: "",
  brandId: "",
  imageUrl: ""
};

export default function ProductAdminPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(initialForm);
  const [fieldErrors, setFieldErrors] = useState({});
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    loadAll();
  }, []);

  async function loadAll() {
    try {
      const [prods, cats, brs] = await Promise.all([
        productService.getAll(),
        categoryService.getAll(),
        brandService.getAll()
      ]);
      setProducts(prods);
      setCategories(cats);
      setBrands(brs);
    } catch (err) {
      setErrorMessage(err.response?.data?.message || "Không thể tải dữ liệu sản phẩm.");
    }
  }

  function resetForm() {
    setEditingId(null);
    setForm(initialForm);
    setFieldErrors({});
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");
    setErrorMessage("");
    setFieldErrors({});

    const payload = {
      sku: form.sku || null,
      name: form.name,
      unit: form.unit || "Cái",
      price: form.price === "" ? null : Number(form.price),
      status: form.status || "ACTIVE",
      categoryId: form.categoryId ? Number(form.categoryId) : null,
      brandId: form.brandId ? Number(form.brandId) : null,
      imageUrl: form.imageUrl || null
    };

    try {
      if (editingId) {
        await productService.update(editingId, payload);
        setMessage("Cập nhật sản phẩm thành công!");
      } else {
        await productService.create(payload);
        setMessage("Thêm sản phẩm thành công!");
      }
      resetForm();
      loadAll();
    } catch (err) {
      const data = err.response?.data;
      setErrorMessage(data?.message || "Lưu sản phẩm thất bại.");
      if (data?.errors) {
        setFieldErrors(data.errors);
      }
    }
  }

  function handleEdit(p) {
    setEditingId(p.id);
    setForm({
      sku: p.sku || "",
      name: p.name || "",
      unit: p.unit || "Cái",
      price: p.price ?? "",
      status: p.status || "ACTIVE",
      categoryId: p.categoryId ?? "",
      brandId: p.brandId ?? "",
      imageUrl: p.imageUrls && p.imageUrls.length > 0 ? p.imageUrls[0] : ""
    });
    setFieldErrors({});
    setMessage("");
    setErrorMessage("");
  }

  async function handleDelete(id) {
    if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) return;
    try {
      await productService.delete(id);
      setMessage("Đã xóa sản phẩm thành công!");
      loadAll();
    } catch (err) {
      setErrorMessage(err.response?.data?.message || "Không thể xóa sản phẩm.");
    }
  }

  return (
    <div>
      <h1 className="page-title">Quản lý sản phẩm</h1>

      {message && <div className="alert-success">{message}</div>}
      {errorMessage && <div className="alert-error">{errorMessage}</div>}

      <div className="card">
        <h3 style={{ marginBottom: "14px", color: "var(--navy)" }}>
          {editingId ? `Cập nhật sản phẩm #${editingId}` : "Thêm sản phẩm mới"}
        </h3>
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-group">
              <label>Mã SKU</label>
              <input
                type="text"
                value={form.sku}
                onChange={(e) => setForm({ ...form, sku: e.target.value })}
                placeholder="Để trống để tự tạo SKU"
              />
              {fieldErrors.sku && <div className="error-text">{fieldErrors.sku}</div>}
            </div>

            <div className="form-group">
              <label>Tên sản phẩm *</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="Nhập tên sản phẩm..."
              />
              {fieldErrors.name && <div className="error-text">{fieldErrors.name}</div>}
            </div>

            <div className="form-group">
              <label>Giá nhập (VNĐ) *</label>
              <input
                type="number"
                value={form.price}
                onChange={(e) => setForm({ ...form, price: e.target.value })}
                placeholder="VD: 500000"
              />
              {fieldErrors.price && <div className="error-text">{fieldErrors.price}</div>}
            </div>

            <div className="form-group">
              <label>Đơn vị tính</label>
              <input
                type="text"
                value={form.unit}
                onChange={(e) => setForm({ ...form, unit: e.target.value })}
                placeholder="Cái, Chiếc, Hộp..."
              />
            </div>

            <div className="form-group">
              <label>Danh mục</label>
              <select
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
              >
                <option value="">-- Chọn danh mục --</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Thương hiệu</label>
              <select
                value={form.brandId}
                onChange={(e) => setForm({ ...form, brandId: e.target.value })}
              >
                <option value="">-- Chọn thương hiệu --</option>
                {brands.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Trạng thái</label>
              <select
                value={form.status}
                onChange={(e) => setForm({ ...form, status: e.target.value })}
              >
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
              </select>
            </div>

            <div className="form-group">
              <label>URL hình ảnh</label>
              <input
                type="text"
                value={form.imageUrl}
                onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
                placeholder="https://..."
              />
            </div>
          </div>

          <div style={{ display: "flex", gap: "10px" }}>
            <button type="submit" className="btn btn-primary">
              {editingId ? "Lưu cập nhật" : "Thêm sản phẩm"}
            </button>
            {editingId && (
              <button type="button" className="btn btn-outline" onClick={resetForm}>
                Hủy
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="card" style={{ overflowX: "auto" }}>
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>SKU</th>
              <th>Tên sản phẩm</th>
              <th>Danh mục</th>
              <th>Thương hiệu</th>
              <th>Giá</th>
              <th>Trạng thái</th>
              <th style={{ width: "150px" }}>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td><code>{p.sku}</code></td>
                <td><strong>{p.name}</strong></td>
                <td>{p.categoryName || p.category}</td>
                <td>{p.brandName || "-"}</td>
                <td style={{ color: "var(--danger)", fontWeight: 600 }}>
                  {Number(p.price || 0).toLocaleString("vi-VN")} đ
                </td>
                <td>
                  <span className="badge badge-approved">{p.status}</span>
                </td>
                <td>
                  <div style={{ display: "flex", gap: "8px" }}>
                    <button
                      type="button"
                      className="btn btn-outline btn-sm"
                      onClick={() => handleEdit(p)}
                    >
                      Sửa
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(p.id)}
                    >
                      Xóa
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
