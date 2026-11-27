-- =====================================================================
-- LAB 03 - POSTGRESQL DATABASE CHO ORDER SERVICE
-- Database: order_db (Port 5432, User: postgres)
-- =====================================================================

-- Chạy câu lệnh này trong psql / pgAdmin trước khi khởi động order-service:
-- CREATE DATABASE order_db;

-- Khi khởi động order-service, Hibernate (ddl-auto=update) sẽ tự động tạo
-- 2 bảng `orders` và `order_items` và nạp sẵn 2 đơn hàng mẫu.
