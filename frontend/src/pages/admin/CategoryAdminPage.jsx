import { useEffect, useState } from "react";
import { categoryService } from "../../services/categoryService";

export default function CategoryAdminPage() {
  const [categories, setCategories] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({ name: "", description: "" });
  const [fieldErrors, setFieldErrors] = useState({});
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const data = await categoryService.getAll();
      setCategories(data);
    } catch (err) {
      setErrorMessage(err.response?.data?.message || "Không thể tải danh sách danh mục.");
    }
  }

  function resetForm() {
    setEditingId(null);
    setForm({ name: "", description: "" });
    setFieldErrors({});
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");
    setErrorMessage("");
    setFieldErrors({});

    try {
      if (editingId) {
        await categoryService.update(editingId, form);
        setMessage("Cập nhật danh mục thành công!");
      } else {
        await categoryService.create(form);
        setMessage("Thêm danh mục thành công!");
      }
      resetForm();
      loadData();
    } catch (err) {
      const data = err.response?.data;
      setErrorMessage(data?.message || "Thao tác thất bại.");
      if (data?.errors) {
        setFieldErrors(data.errors);
      }
    }
  }

  function handleEdit(item) {
    setEditingId(item.id);
    setForm({ name: item.name || "", description: item.description || "" });
    setFieldErrors({});
    setMessage("");
    setErrorMessage("");
  }

  async function handleDelete(id) {
    if (!confirm("Bạn có chắc muốn xóa danh mục này?")) return;
    try {
      await categoryService.delete(id);
      setMessage("Đã xóa danh mục thành công!");
      loadData();
    } catch (err) {
      setErrorMessage(err.response?.data?.message || "Không thể xóa danh mục.");
    }
  }

  return (
    <div>
      <h1 className="page-title">Quản lý danh mục sản phẩm</h1>

      {message && <div className="alert-success">{message}</div>}
      {errorMessage && <div className="alert-error">{errorMessage}</div>}

      <div className="card">
        <h3 style={{ marginBottom: "14px", color: "var(--navy)" }}>
          {editingId ? `Cập nhật danh mục #${editingId}` : "Thêm danh mục mới"}
        </h3>
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-group">
              <label>Tên danh mục *</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="VD: Accessories, Laptop..."
              />
              {fieldErrors.name && <div className="error-text">{fieldErrors.name}</div>}
            </div>
            <div className="form-group">
              <label>Mô tả</label>
              <input
                type="text"
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                placeholder="Mô tả ngắn về danh mục"
              />
              {fieldErrors.description && (
                <div className="error-text">{fieldErrors.description}</div>
              )}
            </div>
          </div>

          <div style={{ display: "flex", gap: "10px" }}>
            <button type="submit" className="btn btn-primary">
              {editingId ? "Lưu cập nhật" : "Thêm mới"}
            </button>
            {editingId && (
              <button type="button" className="btn btn-outline" onClick={resetForm}>
                Hủy
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="card">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên danh mục</th>
              <th>Mô tả</th>
              <th style={{ width: "160px" }}>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((cat) => (
              <tr key={cat.id}>
                <td>{cat.id}</td>
                <td><strong>{cat.name}</strong></td>
                <td>{cat.description}</td>
                <td>
                  <div style={{ display: "flex", gap: "8px" }}>
                    <button
                      type="button"
                      className="btn btn-outline btn-sm"
                      onClick={() => handleEdit(cat)}
                    >
                      Sửa
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(cat.id)}
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
