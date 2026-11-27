-- =====================================================================
-- LAB 03 - DỮ LIỆU MẪU PHỤC VỤ NGHIỆP VỤ (XAMPP MySQL)
-- Database: product_db
-- Các bảng: categories, brands, products, product_images
-- =====================================================================

CREATE DATABASE IF NOT EXISTS product_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE product_db;

-- 1. Bảng categories (5 bản ghi)
INSERT INTO categories (id, name, description) VALUES
    (1, 'Accessories', 'Phụ kiện máy tính (Chuột, Bàn phím, Lót chuột)'),
    (2, 'Display', 'Màn hình máy tính văn phòng & đồ họa'),
    (3, 'Laptop', 'Máy tính xách tay doanh nghiệp & lập trình'),
    (4, 'Audio', 'Tai nghe, loa và thiết bị âm thanh'),
    (5, 'Storage', 'Ổ cứng SSD, HDD và thiết bị lưu trữ')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 2. Bảng brands (6 bản ghi)
INSERT INTO brands (id, name, description) VALUES
    (1, 'Logitech', 'Thương hiệu thiết bị ngoại vi Logitech'),
    (2, 'Keychron', 'Thương hiệu bàn phím cơ Keychron'),
    (3, 'Dell', 'Thương hiệu màn hình và máy tính Dell'),
    (4, 'Sony', 'Thương hiệu thiết bị âm thanh Sony'),
    (5, 'Samsung', 'Thương hiệu ổ cứng và màn hình Samsung'),
    (6, 'LG', 'Thương hiệu màn hình LG UltraGear & UltraWide')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 3. Bảng products (10 bản ghi)
INSERT INTO products (id, sku, name, category, unit, price, status, category_id, brand_id) VALUES
    (1,  'SKU-MOU-001', 'Mouse Logitech G102 Lightsync',        'Accessories', 'Cái',   390000,   'ACTIVE', 1, 1),
    (2,  'SKU-KEY-002', 'Keyboard Mechanical Keychron K2 Pro',  'Accessories', 'Cái',   2150000,  'ACTIVE', 1, 2),
    (3,  'SKU-MON-003', 'Monitor Dell UltraSharp 24 U2424H',    'Display',     'Chiếc', 6490000,  'ACTIVE', 2, 3),
    (4,  'SKU-LAP-004', 'Laptop Dell Inspiron 15 3530',         'Laptop',      'Chiếc', 15990000, 'ACTIVE', 3, 3),
    (5,  'SKU-AUD-005', 'Tai nghe Sony WH-1000XM5',             'Audio',       'Cái',   7990000,  'ACTIVE', 4, 4),
    (6,  'SKU-MOU-006', 'Mouse Logitech MX Master 3S',          'Accessories', 'Cái',   2390000,  'ACTIVE', 1, 1),
    (7,  'SKU-STO-007', 'SSD Samsung 990 Pro 1TB NVMe',         'Storage',     'Cái',   2890000,  'ACTIVE', 5, 5),
    (8,  'SKU-MON-008', 'Monitor LG UltraGear 27GN800 2K',      'Display',     'Chiếc', 7290000,  'ACTIVE', 2, 6),
    (9,  'SKU-KEY-009', 'Keyboard Keychron Q1 QMK Custom',      'Accessories', 'Cái',   3850000,  'ACTIVE', 1, 2),
    (10, 'SKU-STO-010', 'SSD Portable Samsung T7 Shield 1TB',   'Storage',     'Cái',   2490000,  'ACTIVE', 5, 5)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    price = VALUES(price),
    status = VALUES(status);

-- 4. Bảng product_images (10 bản ghi)
INSERT INTO product_images (id, image_url, product_id) VALUES
    (1,  'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400', 1),
    (2,  'https://images.unsplash.com/photo-1595225476474-87563907a212?w=400', 2),
    (3,  'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400', 3),
    (4,  'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400', 4),
    (5,  'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', 5),
    (6,  'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=400', 6),
    (7,  'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400', 7),
    (8,  'https://images.unsplash.com/photo-1586210579191-33b45e38fa2c?w=400', 8),
    (9,  'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400', 9),
    (10, 'https://images.unsplash.com/photo-1531492746076-161ca9bcad58?w=400', 10)
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);
