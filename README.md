# LAB 02 – XÂY DỰNG DATABASE VÀ RESTFUL API CRUD CHO MICROSERVICES
**Đề tài:** Hệ thống Quản lý Kho Nhập Hàng (Inbound Warehouse Management System)  
**Database (XAMPP MySQL):** `ltw3_kiet`

---

## Câu A. Lựa chọn đề tài và xác định các Microservices

### A./ Xác định các chức năng của hệ thống

#### 1. Admin – Trang quản trị kho (Dành cho Quản lý kho & Nhân viên thu mua)
| STT | Nhóm chức năng | Chức năng dự kiến |
| :-: | :--- | :--- |
| 1 | Quản lý danh mục & sản phẩm | Quản lý loại sản phẩm (`categories`), thương hiệu (`brands`), sản phẩm (`products`), hình ảnh (`product_images`), giá nhập, đơn vị tính |
| 2 | Quản lý đơn đặt hàng nhập (PO) | Tạo đơn mua hàng từ nhà cung cấp, phê duyệt đơn nhập (`approve`), hủy đơn (`cancel`), theo dõi chi tiết đơn (`order_items`) |
| 3 | Quản lý tồn kho & kiểm kê | Theo dõi số lượng tồn kho theo vị trí kệ, cảnh báo hàng sắp hết (`low-stock`), điều chỉnh kiểm kê (`adjust`), xem lịch sử biến động (`inventory_transactions`) |
| 4 | Quản lý nhân sự & phân quyền | Quản lý tài khoản nhân viên kho (`WAREHOUSE_MANAGER`, `WAREHOUSE_KEEPER`, `PURCHASING_STAFF`), khóa/mở tài khoản, kiểm tra quyền duyệt đơn |
| 5 | Quản lý thông báo hệ thống | Xem toàn bộ thông báo nghiệp vụ (đơn mới, duyệt đơn, hoàn tất nhập kho, cảnh báo tồn kho thấp) |

#### 2. Client – Trang tác nghiệp thủ kho (Dành cho Thủ kho / Nhân viên tiếp nhận)
| STT | Nhóm chức năng | Chức năng dự kiến |
| :-: | :--- | :--- |
| 1 | Tra cứu sản phẩm & mã SKU | Tìm kiếm sản phẩm theo tên, danh mục hoặc quét mã SKU khi hàng về kho |
| 2 | Tiếp nhận đơn hàng chờ nhập | Xem danh sách đơn đặt hàng (PO) đã được quản lý kho phê duyệt chờ nhập |
| 3 | Lập phiếu nhập kho (GRN) | Tạo phiếu nhập kho (`receipts`) dựa trên đơn PO của nhà cung cấp |
| 4 | Kiểm đếm & phân loại hàng lỗi | Cập nhật số lượng thực nhận (`receivedQty`), số lượng lỗi/trả lại (`rejectedQty`), ghi chú kiểm hàng (`inspect`) |
| 5 | Hoàn tất nhập kho & lên kệ | Xác nhận hoàn tất phiếu nhập (`complete`), tự động cộng tồn kho và đồng bộ trạng thái đơn hàng sang `RECEIVED` |

---

### B./ Xác định các quy trình nghiệp vụ

| STT | Quy trình nghiệp vụ | Đối tượng thực hiện | Service liên quan |
| :-: | :--- | :--- | :--- |
| 1 | Quản lý danh mục, thương hiệu, sản phẩm | Admin | `product-service` |
| 2 | Tạo đơn đặt hàng nhập kho (lấy giá từ Product, gửi thông báo cho Quản lý kho) | Nhân viên thu mua | `order-service`, `product-service`, `notification-service` |
| 3 | Phê duyệt đơn đặt hàng nhập kho (xác thực quyền `WAREHOUSE_MANAGER` qua User Service và báo cho Thủ kho) | Quản lý kho (Admin) | `order-service`, `user-service`, `notification-service` |
| 4 | Tiếp nhận hàng & lập phiếu nhập kho GRN (xác thực thông tin thủ kho và đơn hàng) | Thủ kho (Client) | `receiving-service`, `order-service`, `user-service` |
| 5 | Hoàn tất phiếu nhập kho GRN (tự động cộng tồn kho, cập nhật đơn PO thành `RECEIVED` và bắn thông báo) | Thủ kho (Client) | `receiving-service`, `inventory-service`, `order-service`, `notification-service` |
| 6 | Kiểm tra tồn kho kèm thông tin chi tiết sản phẩm & cảnh báo tồn kho thấp | Admin / Thủ kho | `inventory-service`, `product-service`, `notification-service` |

---

### C./ Xác định các Business Microservices xây dựng

| STT | Tên Service | Port | Chức năng phụ trách | Các bảng Database (`ltw3_kiet`) | Service cần giao tiếp |
| :-: | :--- | :-: | :--- | :--- | :--- |
| 1 | **product-service** | `8081` | Quản lý danh mục, thương hiệu, sản phẩm, hình ảnh sản phẩm | `categories`, `brands`, `products`, `product_images` | Không phụ thuộc trực tiếp |
| 2 | **order-service** | `8082` | Quản lý đơn đặt hàng nhập kho (PO) và chi tiết đơn hàng | `orders`, `order_items` | `product-service`, `user-service`, `notification-service` |
| 3 | **inventory-service** | `8083` | Quản lý vị trí tồn kho, nhập kho, kiểm kê và lịch sử giao dịch kho | `inventories`, `inventory_transactions` | `product-service`, `notification-service` |
| 4 | **receiving-service** | `8084` | Quản lý phiếu nhập kho (GRN), kiểm đếm hàng hóa thực nhận/hàng lỗi | `receipts` | `order-service`, `inventory-service`, `user-service`, `notification-service` |
| 5 | **notification-service** | `8085` | Quản lý thông báo và cảnh báo tự động toàn hệ thống | `notifications` | Nhận yêu cầu từ `order-service`, `inventory-service`, `receiving-service` |
| 6 | **user-service** | `8086` | Quản lý nhân viên kho, đăng nhập và xác thực phân quyền | `users` | Cung cấp API xác thực cho `order-service`, `receiving-service` |

---

## Câu B & Câu C. Cấu trúc dự án & Kết nối Database (XAMPP MySQL)

Mỗi Microservice được tổ chức chuẩn theo kiến trúc 4 tầng (`entity`, `repository`, `service`, `controller`):

```text
lab-02/
├── product-service/        (Port 8081 - MySQL: categories, brands, products, product_images)
├── order-service/          (Port 8082 - MySQL: orders, order_items)
├── inventory-service/      (Port 8083 - MySQL: inventories, inventory_transactions)
├── receiving-service/      (Port 8084 - MySQL: receipts)
├── notification-service/   (Port 8085 - MySQL: notifications)
├── user-service/           (Port 8086 - MySQL: users)
├── .gitignore
└── README.md
```

### Cấu hình Database trên XAMPP (`application.properties`)
1. Mở **XAMPP Control Panel** -> Bấm **Start** ở dòng **MySQL** (Port `3306`).
2. Database sử dụng: **`ltw3_kiet`** (đã cấu hình `createDatabaseIfNotExist=true` và `spring.jpa.hibernate.ddl-auto=update`).
3. Khi khởi chạy các service, Hibernate tự động tạo đầy đủ **10 bảng** trong database `ltw3_kiet` và tự động nạp dữ liệu mẫu ban đầu.

---

## Câu D, E, F. Danh sách RESTful API CRUD & Nghiệp vụ (Kiểm thử Postman)

### 1. Product Service (`http://localhost:8081`)
- `GET /api/categories` | `GET /api/categories/{id}` | `POST /api/categories` | `PUT /api/categories/{id}` | `DELETE /api/categories/{id}`
- `GET /api/brands` | `GET /api/brands/{id}` | `POST /api/brands` | `PUT /api/brands/{id}` | `DELETE /api/brands/{id}`
- `GET /api/products` | `GET /api/products/{id}` | `POST /api/products` | `PUT /api/products/{id}` | `DELETE /api/products/{id}`
- `GET /api/products/sku/{sku}` | `GET /api/products/search?keyword=...&category=...`

### 2. Order Service (`http://localhost:8082`)
- `GET /api/orders` | `GET /api/orders/{id}` | `POST /api/orders` | `PUT /api/orders/{id}` | `DELETE /api/orders/{id}`
- `GET /api/order-items` | `GET /api/order-items/order/{orderId}`
- `GET /api/orders/status/{status}` | `GET /api/orders/product/{id}`
- `PUT /api/orders/{id}/approve` | `PUT /api/orders/{id}/receive` | `PUT /api/orders/{id}/cancel`

### 3. Inventory Service (`http://localhost:8083`)
- `GET /api/inventories` | `GET /api/inventories/{id}` | `POST /api/inventories` | `PUT /api/inventories/{id}` | `DELETE /api/inventories/{id}`
- `GET /api/inventories/product/{productId}` | `GET /api/inventories/low-stock` | `GET /api/inventories/transactions`
- `POST /api/inventories/inbound` | `POST /api/inventories/adjust`

### 4. Receiving Service (`http://localhost:8084`)
- `GET /api/receipts` | `GET /api/receipts/{id}` | `POST /api/receipts` | `PUT /api/receipts/{id}` | `DELETE /api/receipts/{id}`
- `GET /api/receipts/status/{status}` | `GET /api/receipts/orders`
- `PUT /api/receipts/{id}/inspect` | `PUT /api/receipts/{id}/complete` | `PUT /api/receipts/{id}/cancel`

### 5. Notification Service (`http://localhost:8085`)
- `GET /api/notifications` | `GET /api/notifications/{id}` | `POST /api/notifications` | `PUT /api/notifications/{id}` | `DELETE /api/notifications/{id}`
- `GET /api/notifications/unread` | `GET /api/notifications/recipient/{recipient}`
- `POST /api/notifications/send` | `PUT /api/notifications/{id}/read` | `PUT /api/notifications/read-all`

### 6. User Service (`http://localhost:8086`)
- `GET /api/users` | `GET /api/users/{id}` | `POST /api/users` | `PUT /api/users/{id}` | `DELETE /api/users/{id}`
- `GET /api/users/role/{role}` | `PUT /api/users/{id}/status`
- `POST /api/users/login` | `GET /api/users/{id}/validate-role?requiredRole=WAREHOUSE_MANAGER`
