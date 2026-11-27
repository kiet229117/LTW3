import { BrowserRouter, Routes, Route } from "react-router-dom";
import ClientLayout from "../layouts/ClientLayout";
import AdminLayout from "../layouts/AdminLayout";
import HomePage from "../pages/client/HomePage";
import ProductListPage from "../pages/client/ProductListPage";
import ProductDetailPage from "../pages/client/ProductDetailPage";
import OrderSuccessPage from "../pages/client/OrderSuccessPage";
import DashboardPage from "../pages/admin/DashboardPage";
import CategoryAdminPage from "../pages/admin/CategoryAdminPage";
import BrandAdminPage from "../pages/admin/BrandAdminPage";
import ProductAdminPage from "../pages/admin/ProductAdminPage";
import OrderAdminPage from "../pages/admin/OrderAdminPage";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<ClientLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/products" element={<ProductListPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/order-success" element={<OrderSuccessPage />} />
        </Route>

        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="categories" element={<CategoryAdminPage />} />
          <Route path="brands" element={<BrandAdminPage />} />
          <Route path="products" element={<ProductAdminPage />} />
          <Route path="orders" element={<OrderAdminPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
