-- ============================================================================
-- FRESH SEED DATA - PET EYE
-- 6 booking statuses x 4-5 records each, full demo data
-- Run AFTER COMPLETE_RESET_WITH_DATA.sql (schema already created)
-- Or run standalone after dropping and recreating PET_EYE db.
-- ============================================================================
USE PET_EYE;
SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;

-- ============================================================================
-- TRUNCATE ALL DATA TABLES
-- ============================================================================
TRUNCATE TABLE withdrawal_request;
TRUNCATE TABLE shop_wallet;
TRUNCATE TABLE staff_change_request;
TRUNCATE TABLE staff_certificate;
TRUNCATE TABLE pet_vaccination;
TRUNCATE TABLE pet_reminder;
TRUNCATE TABLE pet_meal;
TRUNCATE TABLE pet_image;
TRUNCATE TABLE pet_document;
TRUNCATE TABLE boarding_detail;
TRUNCATE TABLE booking_history;
TRUNCATE TABLE care_log;
TRUNCATE TABLE pet_medical_record;
TRUNCATE TABLE review;
TRUNCATE TABLE notification;
TRUNCATE TABLE payment;
TRUNCATE TABLE booking_services;
TRUNCATE TABLE booking;
TRUNCATE TABLE cage;
TRUNCATE TABLE pet;
TRUNCATE TABLE staff;
TRUNCATE TABLE pet_service;
TRUNCATE TABLE shop;
TRUNCATE TABLE user_voucher;
TRUNCATE TABLE voucher;
TRUNCATE TABLE invalidated_token;
TRUNCATE TABLE user_token;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE `user`;
TRUNCATE TABLE `role`;
TRUNCATE TABLE membership_tier;

-- ============================================================================
-- ROLES
-- ============================================================================
INSERT INTO `role` (id, name, description) VALUES
(1, 'ADMIN',      'Quản trị viên hệ thống'),
(2, 'SHOP_OWNER', 'Chủ cửa hàng thú cưng'),
(3, 'USER',       'Khách hàng'),
(4, 'STAFF',      'Nhân viên cửa hàng');

-- ============================================================================
-- MEMBERSHIP TIERS
-- ============================================================================
INSERT INTO membership_tier (id, name, required_spending, benefits) VALUES
(1, 'BRONZE',   0,        'Thành viên cơ bản'),
(2, 'SILVER',   2000000,  'Giảm 5% tất cả dịch vụ'),
(3, 'GOLD',     5000000,  'Giảm 10% + ưu tiên đặt lịch'),
(4, 'PLATINUM', 10000000, 'Giảm 15% + tư vấn riêng');

-- ============================================================================
-- USERS  (password = "12345678" for all)
-- IDs: 1=admin, 2-4=owners, 5-8=customers
-- ============================================================================
INSERT INTO `user` (id, email, password, full_name, phone, address, active, email_verified, tier_id, total_spending, failed_login_attempts) VALUES
                                                                                                                                                (1,  'admin@peteye.vn',       '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Admin PetEye',         '0900000001', 'TP. Hồ Chí Minh', 1, 1, 1, 0, 0),
                                                                                                                                                (2,  'owner1@peteye.vn',   '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Nguyễn Minh Tuấn',     '0901111001', '123 Lê Văn Sỹ, Quận 3, TP. Hồ Chí Minh',       1, 1, 1, 0, 0),
                                                                                                                                                (3,  'owner2@peteye.vn','$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Trần Thị Lan',         '0901111002', '45 Đinh Tiên Hoàng, Quận Bình Thạnh, TP. Hồ Chí Minh', 1, 1, 1, 0, 0),
                                                                                                                                                (4,  'owner3@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Lê Hoàng Nam',         '0901111003', '88 Nguyễn Oanh, Quận Gò Vấp, TP. Hồ Chí Minh',     1, 1, 1, 0, 0),
                                                                                                                                                (5,  'anhthu@gmail.com',      '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Phạm Anh Thư',         '0912345001', '10 Trần Huy Liệu, Quận Phú Nhuận, TP. Hồ Chí Minh',   1, 1, 3, 3500000, 0),
                                                                                                                                                (6,  'binhminh@gmail.com',    '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Nguyễn Bình Minh',     '0912345002', '5 Võ Thị Sáu, Quận 1, TP. Hồ Chí Minh',        1, 1, 2, 1500000, 0),
                                                                                                                                                (7,  'camly@gmail.com',       '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Vũ Cẩm Ly',            '0912345003', '22 Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh',      1, 1, 2, 800000, 0),
                                                                                                                                                (8,  'ducmanh@gmail.com',     '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Hoàng Đức Mạnh',       '0912345004', '7 Nguyễn Thị Minh Khai, Quận 3, TP. Hồ Chí Minh', 1, 1, 1, 200000, 0);
-- ============================================================================
-- USER ROLES
-- ============================================================================
INSERT INTO user_roles (user_id, roles_id) VALUES
(1,  1),
(2,  2),
(3,  2),
(4,  2),
(5,  3),
(6,  3),
(7,  3),
(8,  3);

-- ============================================================================
-- SHOPS
-- Shop 3 (Pet Hotel 5 Sao) cung cấp cả LƯU TRÚ (BOARDING) lẫn SPA_GROOMING
-- để test luồng đặt lịch/thanh toán gộp nhiều loại dịch vụ trong 1 booking.
-- ============================================================================
INSERT INTO shop (id, owner_id, shop_name, shop_type, email, phone, address, city, latitude, longitude,
                  description, license_number, logo_url, open_time, close_time, working_days,
                  rating_avg, is_verified, status, assignment_mode, late_grace_period,
                  off_days, created_at) VALUES
(1, 2, 'Mèo Kute Spa', 'SPA_GROOMING',
   'contact@meokutespa.vn', '02812345001',
   '123 Lê Văn Sỹ, Phường 13, Quận 3, TP. Hồ Chí Minh', 'TP. Hồ Chí Minh',
   10.7769, 106.6938,
   'Spa & chăm sóc lông cao cấp cho mèo và chó. Đội ngũ chuyên nghiệp, tận tâm.',
   'GPKD-001-2024', NULL, '08:00', '19:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   4.6, 1, 'APPROVED', 'MANUAL', 15, NULL, NOW()),

(2, 3, 'Thú Y An Tâm', 'CLINIC',
   'contact@thuyantam.vn', '02812345002',
   '45 Đinh Tiên Hoàng, Phường 3, Quận Bình Thạnh, TP. Hồ Chí Minh', 'TP. Hồ Chí Minh',
   10.8038, 106.7162,
   'Phòng khám thú y uy tín với đội ngũ bác sĩ giàu kinh nghiệm.',
   'GPKD-002-2024', NULL, '07:30', '18:00', 'MON,TUE,WED,THU,FRI,SAT',
   4.8, 1, 'APPROVED', 'AUTO', 10, NULL, NOW()),

(3, 4, 'Pet Hotel 5 Sao', 'BOARDING',
   'contact@pethotel5sao.vn', '02812345003',
   '88 Nguyễn Oanh, Phường 17, Quận Gò Vấp, TP. Hồ Chí Minh', 'TP. Hồ Chí Minh',
   10.8451, 106.6657,
   'Khách sạn thú cưng 5 sao với camera giám sát 24/7. Phòng riêng và phòng chung, kèm dịch vụ spa tại chỗ cho thú cưng lưu trú.',
   'GPKD-003-2024', NULL, '06:00', '22:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   4.7, 1, 'APPROVED', 'OPEN_POOL', 30, NULL, NOW());

-- ============================================================================
-- SHOP WALLETS
-- ============================================================================
INSERT INTO shop_wallet (id, shop_id, frozen_balance, available_balance, total_earned, total_withdrawn) VALUES
(1, 1, 0.00,       3450000.00, 5450000.00, 2000000.00),
(2, 2, 0.00,       2950000.00, 3950000.00, 1000000.00),
(3, 3, 350000.00,  2550000.00, 2900000.00, 0.00);

-- ============================================================================
-- PETS
-- ============================================================================
INSERT INTO pet (id, owner_id, name, species, breed, gender, color, weight, dob, sterilized,
                 health_note, is_active) VALUES
(1, 5, 'Mochi', 'CAT', 'Munchkin',    'FEMALE', 'Trắng vàng',  3.2, '2022-03-15', 1, 'Khỏe mạnh', 1),
(2, 5, 'Buddy', 'DOG', 'Poodle',      'MALE',   'Nâu caramel', 4.5, '2021-07-20', 1, 'Dị ứng hải sản', 1),
(3, 6, 'Luna',  'CAT', 'British SH',  'FEMALE', 'Xám xanh',    4.0, '2020-11-01', 1, 'Khỏe mạnh', 1),
(4, 7, 'Max',   'DOG', 'Labrador',    'MALE',   'Vàng',        22.0,'2019-05-10', 0, 'Khớp cần theo dõi', 1),
(5, 8, 'Coco',  'RABBIT','Holland Lop','FEMALE','Trắng đen',   2.1, '2023-01-25', 0, 'Khỏe mạnh', 1);

-- ============================================================================
-- PET SERVICES
-- ============================================================================
INSERT INTO pet_service (id, shop_id, service_name, category, price, duration_minutes, description,
                          active, camera_enabled, cage_size, room_type,
                          camera_tiers, camera_tier_prices, camera_tier_labels, camera_description) VALUES
-- Shop 1: SPA_GROOMING
(1, 1, 'Tắm & Sấy Cơ Bản', 'SPA_GROOMING', 150000, 60,
   'Tắm sạch với dầu gội chuyên dụng, sấy khô và chải lông.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 1, 'Cắt Tỉa Lông', 'SPA_GROOMING', 200000, 90,
   'Cắt tỉa theo yêu cầu, tạo kiểu lông đẹp cho thú cưng.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 1, 'Spa Toàn Thân', 'SPA_GROOMING', 350000, 120,
   'Tắm thảo dược, massage, cắt móng, vệ sinh tai và tạo kiểu.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 1, 'Nhuộm Lông Nghệ Thuật', 'SPA_GROOMING', 500000, 180,
   'Nhuộm lông bằng màu an toàn, tạo kiểu độc đáo theo yêu cầu.',
   0, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 2: CLINIC
(5, 2, 'Khám Tổng Quát', 'CLINIC', 200000, 30,
   'Khám sức khỏe toàn diện, tư vấn dinh dưỡng và chăm sóc.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(6, 2, 'Tiêm Phòng', 'CLINIC', 150000, 20,
   'Tiêm vaccine phòng bệnh dại, carre, parvo và các bệnh thường gặp.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(7, 2, 'Xét Nghiệm Máu', 'CLINIC', 300000, 45,
   'Xét nghiệm công thức máu toàn phần, đánh giá chức năng gan thận.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 2, 'Siêu Âm Ổ Bụng', 'CLINIC', 400000, 60,
   'Siêu âm chẩn đoán các bệnh lý nội tạng, thai sản.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 3: BOARDING (có camera) + SPA_GROOMING tại chỗ
(9, 3, 'Lưu Trú Phòng Chung', 'BOARDING', 200000, 1440,
   'Phòng chung thoải mái, ăn uống đầy đủ, vui chơi cùng các bé khác.',
   1, 1, '["SMALL","MEDIUM"]', '["SHARED"]',
   '["BASIC","HD"]',
   '{"BASIC":0,"HD":60000}',
   '{"BASIC":"Xem cơ bản (miễn phí)","HD":"Camera HD (60.000đ/ngày)"}',
   'Camera giám sát 24/7, xem qua ứng dụng'),
(10, 3, 'Lưu Trú Phòng Riêng', 'BOARDING', 350000, 1440,
    'Phòng riêng yên tĩnh, chăm sóc cá nhân, 2 bữa/ngày.',
    1, 1, '["MEDIUM","LARGE"]', '["PRIVATE"]',
    '["BASIC","HD","AI"]',
    '{"BASIC":0,"HD":60000,"AI":200000}',
    '{"BASIC":"Xem cơ bản (miễn phí)","HD":"Camera HD (60.000đ/ngày)","AI":"AI theo dõi hành vi (200.000đ/ngày)"}',
    'Camera HD & AI nhận diện hành vi bất thường'),
(11, 3, 'Lưu Trú VIP Suite', 'BOARDING', 550000, 1440,
    'Phòng VIP rộng rãi, giường êm ái, đồ chơi riêng, chăm sóc cá nhân tối đa.',
    1, 1, '["LARGE"]', '["PRIVATE"]',
    '["BASIC","HD","AI"]',
    '{"BASIC":0,"HD":60000,"AI":200000}',
    '{"BASIC":"Xem cơ bản (miễn phí)","HD":"Camera HD (60.000đ/ngày)","AI":"AI theo dõi hành vi (200.000đ/ngày)"}',
    'Camera AI cao cấp theo dõi sức khỏe và hành vi 24/7'),
(12, 3, 'Tắm & Vệ Sinh', 'SPA_GROOMING', 120000, 90,
    'Tắm sạch, vệ sinh tai, cắt móng cho thú cưng lưu trú.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(13, 3, 'Spa Toàn Thân', 'SPA_GROOMING', 350000, 120,
    'Tắm thảo dược, massage, cắt móng, vệ sinh tai và tạo kiểu cho thú cưng đang lưu trú.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 1: dịch vụ giá cao, dùng để test nhanh việc lên hạng (vượt ngưỡng Bạc/Vàng/Bạch Kim
-- chỉ với 1 booking, không cần cộng dồn nhiều đơn nhỏ)
(14, 1, 'Gói Chăm Sóc Cao Cấp Trọn Gói', 'SPA_GROOMING', 4000000, 180,
    'Gói cao cấp: tắm dưỡng chuyên sâu, trị liệu spa toàn thân, khám tổng quát và tư vấn dinh dưỡng riêng theo thể trạng thú cưng.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL);

-- ============================================================================
-- BOOKINGS
-- ============================================================================

-- ─── Staff records (linked to shop + user accounts) ───────────────────────
INSERT INTO staff (id, shop_id, user_id, full_name, role, phone, is_active) VALUES
(1, 1, 9,  'Trần Văn Hùng',  'GROOMER',      '0911001001', 1),
(2, 1, 10, 'Lê Thị Mai',     'GROOMER',      '0911001002', 1),
(3, 2, 11, 'Nguyễn Thị Hoa', 'VETERINARIAN', '0911001003', 1),
(4, 3, 12, 'Phạm Văn Dũng',  'CARETAKER',    '0911001004', 1);

-- 1. COMPLETED (5) -- booking IDs 1-5, thang 5/2026
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, check_in, check_out,
                     status, note, payos_order_code, created_at) VALUES
(1, 5, 1, 1, 1, '2026-05-05 09:00:00', '2026-05-05 09:05:00', '2026-05-05 10:10:00',
   'COMPLETED', NULL, 202405001, '2026-05-04 20:00:00'),
(2, 5, 1, 2, 2, '2026-05-10 14:00:00', '2026-05-10 14:02:00', '2026-05-10 15:35:00',
   'COMPLETED', NULL, 202405002, '2026-05-09 18:30:00'),
(3, 6, 2, 3, 3, '2026-05-12 08:30:00', '2026-05-12 08:35:00', '2026-05-12 09:10:00',
   'COMPLETED', 'Luna cần kiểm tra răng', 202405003, '2026-05-11 21:00:00'),
(4, 7, 2, 4, 3, '2026-05-20 10:00:00', '2026-05-20 10:00:00', '2026-05-20 11:05:00',
   'COMPLETED', 'Max đau khớp trước trái', 202405004, '2026-05-19 15:00:00'),
(5, 8, 1, 5, 1, '2026-05-28 11:00:00', '2026-05-28 11:10:00', '2026-05-28 12:05:00',
   'COMPLETED', NULL, 202405005, '2026-05-27 10:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(1, 1),
(2, 3),
(3, 5),
(4, 5), (4, 7),
(5, 1);

-- 2. CANCELLED (4) -- booking IDs 6-9
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, cancellation_reason,
                     payos_order_code, created_at) VALUES
(6, 5, 2, 1, 3, '2026-05-15 09:00:00',
   'CANCELLED', NULL, 'Khách hủy: mèo bị ốm không thể đến khám', 202405006, '2026-05-14 10:00:00'),
(7, 6, 1, 3, 1, '2026-05-22 15:00:00',
   'CANCELLED', NULL, 'Khách hủy: bận đột xuất', 202405007, '2026-05-21 08:00:00'),
(8, 7, 3, 4, 4, '2026-06-01 12:00:00',
   'CANCELLED', 'Phòng riêng cho Max', 'Shop hủy: hết phòng loại LARGE', 202406001, '2026-05-30 19:00:00'),
(9, 8, 2, 5, 3, '2026-06-05 09:30:00',
   'CANCELLED', NULL, 'Khách hủy: thay đổi kế hoạch', 202406002, '2026-06-04 09:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(6, 5),
(7, 2),
(8, 10),
(9, 6);

-- 3. WAITING_SHOP_APPROVAL (4) -- booking IDs 10-13, Shop1 MANUAL
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(10, 5, 1, 1, NULL, '2026-06-20 09:00:00', 'WAITING_SHOP_APPROVAL', 'Mochi cần spa nhẹ nhàng', 202406003, '2026-06-17 10:30:00'),
(11, 6, 1, 3, NULL, '2026-06-21 14:00:00', 'WAITING_SHOP_APPROVAL', NULL, 202406004, '2026-06-17 15:00:00'),
(12, 7, 1, 4, NULL, '2026-06-22 10:00:00', 'WAITING_SHOP_APPROVAL', 'Max cần nhân viên có kinh nghiệm với chó lớn', 202406005, '2026-06-18 07:00:00'),
(13, 8, 1, 5, NULL, '2026-06-23 11:00:00', 'WAITING_SHOP_APPROVAL', NULL, 202406006, '2026-06-18 08:30:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(10, 3),
(11, 1),
(12, 2),
(13, 1);

-- 4. CONFIRMED (5) -- booking IDs 14-18
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(14, 5, 2, 2, 3, '2026-06-20 08:30:00', 'CONFIRMED', 'Buddy cần tiêm nhắc lại vaccine dại', 202406007, '2026-06-15 09:00:00'),
(15, 6, 2, 3, 3, '2026-06-21 09:00:00', 'CONFIRMED', NULL, 202406008, '2026-06-16 10:00:00'),
(16, 5, 3, 1, 4, '2026-06-25 14:00:00', 'CONFIRMED', 'Mochi lưu trú 3 ngày, cần phòng yên tĩnh', 202406009, '2026-06-14 20:00:00'),
(17, 7, 2, 4, 3, '2026-06-24 10:00:00', 'CONFIRMED', 'Max siêu âm theo lịch tái khám', 202406010, '2026-06-16 14:00:00'),
(18, 8, 3, 5, 4, '2026-06-26 10:00:00', 'CONFIRMED', 'Coco lưu trú 2 ngày cuối tuần', 202406011, '2026-06-15 11:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(14, 6),
(15, 5), (15, 6),
(16, 9),
(17, 8),
(18, 9);

-- 5. IN_PROGRESS (4) -- booking IDs 19-22, dang dien ra hom nay
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, check_in, service_start_datetime,
                     status, note, payos_order_code, created_at) VALUES
(19, 5, 1, 2, 1, '2026-06-18 09:00:00', '2026-06-18 09:05:00', '2026-06-18 09:10:00',
    'IN_PROGRESS', NULL, 202406012, '2026-06-17 20:00:00'),
(20, 6, 2, 3, 3, '2026-06-18 08:30:00', '2026-06-18 08:32:00', '2026-06-18 08:40:00',
    'IN_PROGRESS', 'Luna cần xét nghiệm máu định kỳ', 202406013, '2026-06-17 21:00:00'),
(21, 7, 1, 4, 2, '2026-06-18 10:00:00', '2026-06-18 10:03:00', '2026-06-18 10:15:00',
    'IN_PROGRESS', 'Max cần spa toàn thân', 202406014, '2026-06-17 18:00:00'),
(22, 8, 3, 5, 4, '2026-06-17 12:00:00', '2026-06-17 12:05:00', '2026-06-17 12:10:00',
    'IN_PROGRESS', 'Coco lưu trú 2 ngày', 202406015, '2026-06-16 09:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(19, 2),
(20, 7),
(21, 3),
(22, 9);

-- 6. PENDING_PAYMENT (4) -- booking IDs 23-26, vua tao
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(23, 5, 3, 1, NULL, '2026-06-27 14:00:00', 'PENDING_PAYMENT', 'Mochi lưu trú phòng VIP 2 ngày', 202406016, '2026-06-18 09:00:00'),
(24, 6, 1, 3, NULL, '2026-06-25 15:00:00', 'PENDING_PAYMENT', NULL, 202406017, '2026-06-18 09:30:00'),
(25, 7, 2, 4, NULL, '2026-06-26 09:00:00', 'PENDING_PAYMENT', 'Khám tổng quát và xét nghiệm máu', 202406018, '2026-06-18 10:00:00'),
(26, 8, 2, 5, NULL, '2026-06-28 10:00:00', 'PENDING_PAYMENT', NULL, 202406019, '2026-06-18 10:30:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(23, 11),
(24, 3),
(25, 5), (25, 7),
(26, 6);

-- 7. CONFIRMED, gộp LƯU TRÚ + SPA cùng 1 booking (test thanh toán 2 dịch vụ khác loại) -- booking ID 32
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(32, 6, 3, 3, 4, '2026-06-29 09:00:00', 'CONFIRMED', 'Luna lưu trú phòng riêng kèm gói spa toàn thân', 202406021, '2026-06-18 11:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(32, 10), (32, 13);

-- ============================================================================
-- PAYMENTS
-- ============================================================================
INSERT INTO payment (id, booking_id, amount, method, status, payos_order_code,
                     gateway_transaction_id, payment_time, description) VALUES
-- COMPLETED → SUCCESS
(1,  1,  150000, 'PAYOS', 'SUCCESS', 202405001, 'TXN-SPA-001', '2026-05-04 20:05:00', 'Thanh toán Tắm & Sấy'),
(2,  2,  350000, 'PAYOS', 'SUCCESS', 202405002, 'TXN-SPA-002', '2026-05-09 18:35:00', 'Thanh toán Spa Toàn Thân'),
(3,  3,  200000, 'PAYOS', 'SUCCESS', 202405003, 'TXN-CLI-001', '2026-05-11 21:05:00', 'Thanh toán Khám Tổng Quát'),
(4,  4,  500000, 'PAYOS', 'SUCCESS', 202405004, 'TXN-CLI-002', '2026-05-19 15:05:00', 'Thanh toán Khám + Xét nghiệm'),
(5,  5,  150000, 'PAYOS', 'SUCCESS', 202405005, 'TXN-SPA-003', '2026-05-27 10:05:00', 'Thanh toán Tắm & Sấy'),
-- CANCELLED → REFUNDED
(6,  6,  200000, 'PAYOS', 'REFUNDED', 202405006, 'TXN-CLI-003', '2026-05-14 10:05:00', 'Hoàn tiền hủy lịch'),
(7,  7,  200000, 'PAYOS', 'REFUNDED', 202405007, 'TXN-SPA-004', '2026-05-21 08:05:00', 'Hoàn tiền hủy lịch'),
(8,  8,  350000, 'PAYOS', 'REFUNDED', 202406001, 'TXN-HOT-001', '2026-05-30 19:05:00', 'Hoàn tiền hủy phòng'),
(9,  9,  150000, 'PAYOS', 'REFUNDED', 202406002, 'TXN-CLI-004', '2026-06-04 09:05:00', 'Hoàn tiền hủy lịch'),
-- WAITING_SHOP_APPROVAL → SUCCESS (da thanh toan, cho shop duyet)
(10, 10, 350000, 'PAYOS', 'SUCCESS', 202406003, 'TXN-SPA-005', '2026-06-17 10:35:00', 'Thanh toán Spa Toàn Thân'),
(11, 11, 150000, 'PAYOS', 'SUCCESS', 202406004, 'TXN-SPA-006', '2026-06-17 15:05:00', 'Thanh toán Tắm & Sấy'),
(12, 12, 200000, 'PAYOS', 'SUCCESS', 202406005, 'TXN-SPA-007', '2026-06-18 07:05:00', 'Thanh toán Cắt Tỉa Lông'),
(13, 13, 150000, 'PAYOS', 'SUCCESS', 202406006, 'TXN-SPA-008', '2026-06-18 08:35:00', 'Thanh toán Tắm & Sấy'),
-- CONFIRMED → SUCCESS
(14, 14, 150000, 'PAYOS', 'SUCCESS', 202406007, 'TXN-CLI-005', '2026-06-15 09:05:00', 'Thanh toán Tiêm Phòng'),
(15, 15, 350000, 'PAYOS', 'SUCCESS', 202406008, 'TXN-CLI-006', '2026-06-16 10:05:00', 'Thanh toán Khám + Tiêm'),
(16, 16, 200000, 'PAYOS', 'SUCCESS', 202406009, 'TXN-HOT-002', '2026-06-14 20:05:00', 'Thanh toán Lưu Trú'),
(17, 17, 400000, 'PAYOS', 'SUCCESS', 202406010, 'TXN-CLI-007', '2026-06-16 14:05:00', 'Thanh toán Siêu Âm'),
(18, 18, 200000, 'PAYOS', 'SUCCESS', 202406011, 'TXN-HOT-003', '2026-06-15 11:05:00', 'Thanh toán Lưu Trú'),
-- IN_PROGRESS → SUCCESS
(19, 19, 200000, 'PAYOS', 'SUCCESS', 202406012, 'TXN-SPA-009', '2026-06-17 20:05:00', 'Thanh toán Cắt Tỉa Lông'),
(20, 20, 300000, 'PAYOS', 'SUCCESS', 202406013, 'TXN-CLI-008', '2026-06-17 21:05:00', 'Thanh toán Xét Nghiệm Máu'),
(21, 21, 350000, 'PAYOS', 'SUCCESS', 202406014, 'TXN-SPA-010', '2026-06-17 18:05:00', 'Thanh toán Spa Toàn Thân'),
(22, 22, 200000, 'PAYOS', 'SUCCESS', 202406015, 'TXN-HOT-004', '2026-06-16 09:05:00', 'Thanh toán Lưu Trú'),
-- PENDING_PAYMENT → PENDING
(23, 23, 550000, 'PAYOS', 'PENDING', 202406016, NULL, NULL, 'Chờ thanh toán Lưu Trú VIP'),
(24, 24, 350000, 'PAYOS', 'PENDING', 202406017, NULL, NULL, 'Chờ thanh toán Spa Toàn Thân'),
(25, 25, 500000, 'PAYOS', 'PENDING', 202406018, NULL, NULL, 'Chờ thanh toán Khám + Xét nghiệm'),
(26, 26, 150000, 'PAYOS', 'PENDING', 202406019, NULL, NULL, 'Chờ thanh toán Tiêm Phòng'),
-- CONFIRMED, gop Luu Tru + Spa → SUCCESS
(32, 32, 700000, 'PAYOS', 'SUCCESS', 202406021, 'TXN-HOT-005', '2026-06-18 11:05:00', 'Thanh toán Lưu Trú Phòng Riêng + Spa Toàn Thân');

-- ============================================================================
-- REVIEWS (cho COMPLETED bookings)
-- ============================================================================
INSERT INTO review (id, shop_id, user_id, service_id, rating, comment, created_at, reply, replied_at) VALUES
(1, 1, 5, 1, 5,
   'Mochi được chăm sóc rất tốt! Nhân viên nhẹ nhàng, chuyên nghiệp. Lông bé sáng và mềm hơn hẳn.',
   '2026-05-05 11:00:00',
   'Cảm ơn chị đã tin tưởng Mèo Kute Spa! Mochi thật đáng yêu, hẹn gặp lại chị và bé nhé ^^',
   '2026-05-05 14:00:00'),
(2, 1, 5, 3, 5,
   'Buddy được spa rất kỹ, mùi thơm mà không hắc. Bé vui vẻ khi về nhà, không bị stress.',
   '2026-05-10 16:00:00',
   'Cảm ơn anh/chị! Buddy rất ngoan trong suốt buổi spa. Hẹn gặp lại!',
   '2026-05-11 09:00:00'),
(3, 2, 6, 5, 5,
   'Bác sĩ rất tận tình, giải thích rõ ràng tình trạng sức khỏe của Luna. Phòng khám sạch sẽ, thoáng mát.',
   '2026-05-12 10:00:00', NULL, NULL),
(4, 2, 7, 5, 4,
   'Khám nhanh và chuyên nghiệp. Chỉ hơi đông nên chờ lâu một chút. Bác sĩ giải thích về khớp của Max rất chi tiết.',
   '2026-05-20 12:00:00',
   'Cảm ơn anh đã phản hồi! Chúng tôi đang cải thiện thời gian chờ. Hẹn gặp lại Max!',
   '2026-05-20 15:00:00'),
(5, 1, 8, 1, 4,
   'Nhân viên thân thiện, Coco được tắm sạch. Hơi nhỏ so với giá nhưng chất lượng ổn.',
   '2026-05-28 13:00:00', NULL, NULL);

-- ============================================================================
-- NOTIFICATIONS
-- ============================================================================
INSERT INTO notification (id, user_id, title, content, notification_type, is_read, created_at) VALUES
-- BOOKING
(1,  5, 'Đặt lịch thành công', 'Lịch Spa Toàn Thân cho Buddy vào 14:00 ngày 09/05 đã được xác nhận.', 'BOOKING', 1, '2026-05-09 18:36:00'),
(2,  5, 'Hoàn thành dịch vụ',  'Buddy đã hoàn thành dịch vụ Spa Toàn Thân. Hãy để lại đánh giá cho chúng tôi!', 'BOOKING', 1, '2026-05-10 15:40:00'),
(3,  6, 'Đặt lịch thành công', 'Lịch Khám Tổng Quát cho Luna vào 08:30 ngày 12/05 đã được xác nhận.', 'BOOKING', 1, '2026-05-11 21:06:00'),
(4,  7, 'Đặt lịch thành công', 'Lịch Khám + Xét nghiệm cho Max vào 10:00 ngày 20/05 đã được xác nhận.', 'BOOKING', 1, '2026-05-19 15:06:00'),
(5,  5, 'Chờ shop xác nhận',   'Lịch Spa Toàn Thân cho Mochi vào 09:00 ngày 20/06 đang chờ xác nhận từ Mèo Kute Spa.', 'BOOKING', 0, '2026-06-17 10:36:00'),
(6,  6, 'Chờ shop xác nhận',   'Lịch Tắm & Sấy cho Luna vào 14:00 ngày 21/06 đang chờ xác nhận từ Mèo Kute Spa.', 'BOOKING', 0, '2026-06-17 15:06:00'),
(7,  2, 'Đặt lịch mới',        'Phạm Anh Thư đặt Spa Toàn Thân cho Mochi vào 09:00 ngày 20/06. Vui lòng xác nhận.', 'BOOKING', 0, '2026-06-17 10:36:00'),
(8,  2, 'Đặt lịch mới',        'Nguyễn Bình Minh đặt Tắm & Sấy cho Luna vào 14:00 ngày 21/06. Vui lòng xác nhận.', 'BOOKING', 0, '2026-06-17 15:06:00'),
(9,  5, 'Dịch vụ đang bắt đầu','Buddy đang được cắt tỉa lông tại Mèo Kute Spa. Dự kiến hoàn thành lúc 10:30.', 'BOOKING', 0, '2026-06-18 09:10:00'),
(10, 6, 'Dịch vụ đang bắt đầu','Luna đang được xét nghiệm tại Thú Y An Tâm.', 'BOOKING', 0, '2026-06-18 08:40:00'),

-- GENERAL
(11, 5, 'Chào mừng bạn đến với Pet Eye', 'Cảm ơn bạn đã đăng ký! Khám phá hàng trăm spa và bệnh viện thú y uy tín gần bạn.', 'GENERAL', 1, '2026-05-01 09:00:00'),
(12, 6, 'Cập nhật phiên bản mới', 'Pet Eye vừa ra mắt phiên bản 2.0 với tính năng đặt lịch nhanh và theo dõi lịch sử khám bệnh.', 'GENERAL', 1, '2026-05-15 10:00:00'),
(13, 7, 'Thông báo nghỉ lễ', 'Các shop đối tác sẽ nghỉ lễ 02/09. Vui lòng đặt lịch sớm để tránh giai đoạn cao điểm.', 'GENERAL', 0, '2026-06-15 08:00:00'),

-- PROMOTION
(14, 5, 'Ưu đãi 20% dịch vụ Spa', 'Nhận mã giảm giá SPA20 cho lần đặt lịch Spa Toàn Thân tiếp theo, áp dụng đến 30/06.', 'PROMOTION', 0, '2026-06-10 09:00:00'),
(15, 6, 'Chương trình thành viên mới', 'Tích điểm mỗi lần sử dụng dịch vụ để đổi quà tặng hấp dẫn từ Thú Y An Tâm.', 'PROMOTION', 0, '2026-06-12 14:00:00'),
(16, 7, 'Khuyến mãi mùa hè', 'Giảm 15% gói Khám Tổng Quát cho thú cưng trong tháng 6. Đặt lịch ngay để không bỏ lỡ!', 'PROMOTION', 0, '2026-06-16 11:00:00'),

-- REMINDER
(17, 5, 'Nhắc lịch hẹn', 'Lịch Spa Toàn Thân cho Mochi vào 09:00 ngày mai (20/06) tại Mèo Kute Spa. Đừng quên nhé!', 'REMINDER', 0, '2026-06-19 18:00:00'),
(18, 6, 'Nhắc thanh toán', 'Đơn đặt lịch Spa Toàn Thân cho Luna ngày 25/06 đang chờ thanh toán. Vui lòng hoàn tất trong 30 phút.', 'REMINDER', 0, '2026-06-18 09:31:00'),
(19, 7, 'Nhắc tiêm phòng định kỳ', 'Max đã đến hạn tiêm phòng nhắc lại. Hãy đặt lịch tại Thú Y An Tâm để được kiểm tra sớm.', 'REMINDER', 0, '2026-06-17 08:00:00'),

-- SYSTEM
(20, 5, 'Mật khẩu đã được thay đổi', 'Mật khẩu tài khoản của bạn vừa được cập nhật thành công. Nếu không phải bạn, hãy liên hệ hỗ trợ ngay.', 'SYSTEM', 1, '2026-06-05 20:15:00'),
(21, 2, 'Xác thực tài khoản thành công', 'Hồ sơ đối tác Mèo Kute Spa đã được xác thực và kích hoạt trên hệ thống.', 'SYSTEM', 1, '2026-04-20 10:00:00'),
(22, 6, 'Bảo trì hệ thống', 'Hệ thống sẽ bảo trì từ 23:00 - 01:00 ngày 22/06. Một số chức năng có thể bị ảnh hưởng.', 'SYSTEM', 0, '2026-06-18 12:00:00');

-- ============================================================================
-- BOOKING HISTORY
-- ============================================================================
INSERT INTO booking_history (booking_id, old_status, new_status, changed_at, changed_by) VALUES
(1, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-04 20:05:00', 'SYSTEM'),
(1, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-05-04 21:00:00', 'owner.spa@peteye.vn'),
(1, 'CONFIRMED',             'IN_PROGRESS',            '2026-05-05 09:10:00', 'staff1.spa@peteye.vn'),
(1, 'IN_PROGRESS',           'COMPLETED',              '2026-05-05 10:10:00', 'staff1.spa@peteye.vn'),
(2, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-09 18:35:00', 'SYSTEM'),
(2, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-05-09 19:00:00', 'owner.spa@peteye.vn'),
(2, 'CONFIRMED',             'IN_PROGRESS',            '2026-05-10 14:05:00', 'staff2.spa@peteye.vn'),
(2, 'IN_PROGRESS',           'COMPLETED',              '2026-05-10 15:35:00', 'staff2.spa@peteye.vn'),
(3, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-11 21:05:00', 'SYSTEM'),
(3, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-05-11 21:30:00', 'SYSTEM'),
(3, 'CONFIRMED',             'IN_PROGRESS',            '2026-05-12 08:40:00', 'staff.clinic@peteye.vn'),
(3, 'IN_PROGRESS',           'COMPLETED',              '2026-05-12 09:10:00', 'staff.clinic@peteye.vn'),
(4, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-19 15:05:00', 'SYSTEM'),
(4, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-05-19 15:30:00', 'SYSTEM'),
(4, 'CONFIRMED',             'IN_PROGRESS',            '2026-05-20 10:05:00', 'staff.clinic@peteye.vn'),
(4, 'IN_PROGRESS',           'COMPLETED',              '2026-05-20 11:05:00', 'staff.clinic@peteye.vn'),
(5, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-27 10:05:00', 'SYSTEM'),
(5, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-05-27 10:30:00', 'owner.spa@peteye.vn'),
(5, 'CONFIRMED',             'IN_PROGRESS',            '2026-05-28 11:15:00', 'staff1.spa@peteye.vn'),
(5, 'IN_PROGRESS',           'COMPLETED',              '2026-05-28 12:05:00', 'staff1.spa@peteye.vn'),
(6, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-14 10:05:00', 'SYSTEM'),
(6, 'WAITING_SHOP_APPROVAL', 'CANCELLED',              '2026-05-14 10:09:00', 'anhthu@gmail.com'),
(7, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-21 08:05:00', 'SYSTEM'),
(7, 'WAITING_SHOP_APPROVAL', 'CANCELLED',              '2026-05-21 08:10:00', 'binhminh@gmail.com'),
(8, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-05-30 19:05:00', 'SYSTEM'),
(8, 'WAITING_SHOP_APPROVAL', 'CANCELLED',              '2026-05-30 19:29:00', 'owner.hotel@peteye.vn'),
(9, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-06-04 09:05:00', 'SYSTEM'),
(9, 'WAITING_SHOP_APPROVAL', 'CANCELLED',              '2026-06-04 09:08:00', 'ducmanh@gmail.com'),
(10, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-17 10:35:00', 'SYSTEM'),
(11, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-17 15:05:00', 'SYSTEM'),
(12, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-18 07:05:00', 'SYSTEM'),
(13, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-18 08:35:00', 'SYSTEM'),
(14, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-15 09:05:00', 'SYSTEM'),
(14, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-15 10:00:00', 'SYSTEM'),
(15, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-16 10:05:00', 'SYSTEM'),
(15, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-16 10:30:00', 'SYSTEM'),
(16, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-14 20:05:00', 'SYSTEM'),
(16, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-14 20:30:00', 'owner.hotel@peteye.vn'),
(17, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-16 14:05:00', 'SYSTEM'),
(17, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-16 14:30:00', 'SYSTEM'),
(18, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-15 11:05:00', 'SYSTEM'),
(18, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-15 11:30:00', 'owner.hotel@peteye.vn'),
(19, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-17 20:05:00', 'SYSTEM'),
(19, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-17 20:30:00', 'owner.spa@peteye.vn'),
(19, 'CONFIRMED',            'IN_PROGRESS',            '2026-06-18 09:10:00', 'staff1.spa@peteye.vn'),
(20, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-17 21:05:00', 'SYSTEM'),
(20, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-17 21:20:00', 'SYSTEM'),
(20, 'CONFIRMED',            'IN_PROGRESS',            '2026-06-18 08:40:00', 'staff.clinic@peteye.vn'),
(21, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-17 18:05:00', 'SYSTEM'),
(21, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-17 18:30:00', 'owner.spa@peteye.vn'),
(21, 'CONFIRMED',            'IN_PROGRESS',            '2026-06-18 10:15:00', 'staff2.spa@peteye.vn'),
(22, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-16 09:05:00', 'SYSTEM'),
(22, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-16 09:30:00', 'owner.hotel@peteye.vn'),
(22, 'CONFIRMED',            'IN_PROGRESS',            '2026-06-17 12:10:00', 'staff.hotel@peteye.vn'),
(32, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-18 11:05:00', 'SYSTEM'),
(32, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-18 11:30:00', 'owner.hotel@peteye.vn');

-- ============================================================================
-- CUSTOMER MANAGEMENT DEMO DATA
-- Bổ sung để demo tính năng "Quản lý Khách Hàng" (Shop Owner mobile app)
-- Shop 1 (Mèo Kute Spa) sẽ có 5 khách hàng với đủ 4 hạng tier:
--   BRONZE  → Hoàng Đức Mạnh   (user 8,  200K spent)
--   SILVER  → Vũ Cẩm Ly        (user 7,  800K spent)
--             Nguyễn Bình Minh (user 6, 1.5M spent)
--   GOLD    → Phạm Anh Thư     (user 5, 3.5M spent)
--   PLATINUM→ Lý Thị Bích Vân  (user 13,  15M spent)
-- ============================================================================

-- 1. Cập nhật avatar cho 4 khách hàng hiện có (dùng placeholder ảnh thật)
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=47' WHERE id = 5;  -- Pham Anh Thu
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=33' WHERE id = 6;  -- Nguyen Binh Minh
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=45' WHERE id = 7;  -- Vu Cam Ly
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=12' WHERE id = 8;  -- Hoang Duc Manh

-- 2. Thêm khách hàng PLATINUM (tier_id=4, 15M spent) để demo filter
INSERT INTO `user` (id, email, password, full_name, phone, address, active, email_verified, tier_id, total_spending, failed_login_attempts) VALUES
(13, 'vip.customer@gmail.com',
 '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO',
 'Lý Thị Bích Vân', '0912399001', '99 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', 1, 1, 4, 15000000, 0);

UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=25' WHERE id = 13;

INSERT INTO user_roles (user_id, roles_id) VALUES (13, 3);

-- 3. Pets của khách hàng PLATINUM (2 con)
INSERT INTO pet (id, owner_id, name, species, breed, gender, color, weight, dob, sterilized, health_note, is_active) VALUES
(6, 13, 'Diamond', 'DOG', 'Husky Siberia', 'MALE',   'Trắng xám', 18.5, '2020-08-15', 1, 'Khỏe mạnh, cần tập thể dục đều đặn', 1),
(7, 13, 'Ruby',    'CAT', 'Ragdoll',       'FEMALE', 'Trắng kem',  4.2, '2021-12-01', 1, 'Khỏe mạnh, không dị ứng',            1);

-- 4. Bookings COMPLETED của khách hàng PLATINUM tại shop 1 (IDs 27-30)
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, check_in, check_out,
                     status, note, payos_order_code, created_at) VALUES
(27, 13, 1, 6, 1, '2026-04-10 10:00:00', '2026-04-10 10:05:00', '2026-04-10 13:05:00',
    'COMPLETED', 'Diamond spa toàn thân lần 1',       202604001, '2026-04-09 19:00:00'),
(28, 13, 1, 7, 1, '2026-04-25 09:00:00', '2026-04-25 09:02:00', '2026-04-25 10:30:00',
    'COMPLETED', 'Ruby tắm & sấy cơ bản',              202604002, '2026-04-24 20:00:00'),
(29, 13, 1, 6, 2, '2026-05-08 11:00:00', '2026-05-08 11:05:00', '2026-05-08 14:05:00',
    'COMPLETED', 'Diamond spa toàn thân + cắt tỉa',   202605010, '2026-05-07 18:00:00'),
(30, 13, 1, 7, 1, '2026-05-30 14:00:00', '2026-05-30 14:05:00', '2026-05-30 15:10:00',
    'COMPLETED', NULL,                                202605011, '2026-05-29 16:00:00');

-- 5. Booking CONFIRMED sắp tới của khách hàng PLATINUM (ID 31)
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(31, 13, 1, 6, 2, '2026-06-28 10:00:00',
    'CONFIRMED', 'Diamond cắt tỉa định kỳ tháng 6', 202406020, '2026-06-16 11:00:00');

-- 6. Booking services cho bookings 27-31
INSERT INTO booking_services (booking_id, service_id) VALUES
(27, 3),   -- Spa Toan Than (350k)
(28, 1),   -- Tam & Say Co Ban (150k)
(29, 3),   -- Spa Toan Than (350k)
(30, 1),   -- Tam & Say Co Ban (150k)
(31, 2);   -- Cat Tia Long (200k)

-- 7. Payments cho bookings 27-31
INSERT INTO payment (id, booking_id, amount, method, status, payos_order_code,
                     gateway_transaction_id, payment_time, description) VALUES
(27, 27, 350000, 'PAYOS', 'SUCCESS', 202604001, 'TXN-VIP-001', '2026-04-09 19:05:00', 'Thanh toán Spa Toàn Thân'),
(28, 28, 150000, 'PAYOS', 'SUCCESS', 202604002, 'TXN-VIP-002', '2026-04-24 20:05:00', 'Thanh toán Tắm & Sấy'),
(29, 29, 350000, 'PAYOS', 'SUCCESS', 202605010, 'TXN-VIP-003', '2026-05-07 18:05:00', 'Thanh toán Spa Toàn Thân'),
(30, 30, 150000, 'PAYOS', 'SUCCESS', 202605011, 'TXN-VIP-004', '2026-05-29 16:05:00', 'Thanh toán Tắm & Sấy'),
(31, 31, 200000, 'PAYOS', 'SUCCESS', 202406020, 'TXN-VIP-005', '2026-06-16 11:05:00', 'Thanh toán Cắt Tỉa Lông');

-- 8. Booking history cho bookings 27-31
INSERT INTO booking_history (booking_id, old_status, new_status, changed_at, changed_by) VALUES
(27, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-04-09 19:05:00', 'SYSTEM'),
(27, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-04-09 20:00:00', 'owner.spa@peteye.vn'),
(27, 'CONFIRMED',            'IN_PROGRESS',            '2026-04-10 10:10:00', 'staff1.spa@peteye.vn'),
(27, 'IN_PROGRESS',          'COMPLETED',              '2026-04-10 13:05:00', 'staff1.spa@peteye.vn'),
(28, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-04-24 20:05:00', 'SYSTEM'),
(28, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-04-24 21:00:00', 'owner.spa@peteye.vn'),
(28, 'CONFIRMED',            'IN_PROGRESS',            '2026-04-25 09:05:00', 'staff1.spa@peteye.vn'),
(28, 'IN_PROGRESS',          'COMPLETED',              '2026-04-25 10:30:00', 'staff1.spa@peteye.vn'),
(29, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-05-07 18:05:00', 'SYSTEM'),
(29, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-05-07 19:00:00', 'owner.spa@peteye.vn'),
(29, 'CONFIRMED',            'IN_PROGRESS',            '2026-05-08 11:10:00', 'staff2.spa@peteye.vn'),
(29, 'IN_PROGRESS',          'COMPLETED',              '2026-05-08 14:05:00', 'staff2.spa@peteye.vn'),
(30, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-05-29 16:05:00', 'SYSTEM'),
(30, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-05-29 17:00:00', 'owner.spa@peteye.vn'),
(30, 'CONFIRMED',            'IN_PROGRESS',            '2026-05-30 14:10:00', 'staff1.spa@peteye.vn'),
(30, 'IN_PROGRESS',          'COMPLETED',              '2026-05-30 15:10:00', 'staff1.spa@peteye.vn'),
(31, 'PENDING_PAYMENT',      'WAITING_SHOP_APPROVAL', '2026-06-16 11:05:00', 'SYSTEM'),
(31, 'WAITING_SHOP_APPROVAL','CONFIRMED',              '2026-06-16 12:00:00', 'owner.spa@peteye.vn');

-- ============================================================================
-- 9. BỔ SUNG BOOKING COMPLETED để total_spending (hạng thành viên) KHỚP với
-- tổng tiền thực tế cộng dồn từ booking_services/pet_service — trước đây total_spending
-- của 5 khách hàng demo (user 5,6,7,8,13) được set tay, cao hơn hẳn tổng các booking
-- COMPLETED thực có trong seed (vd user13: total_spending=15.000.000đ nhưng chỉ có
-- booking COMPLETED tổng 1.000.000đ) — khiến trang "Quản lý khách hàng" (tính lại từ
-- booking thật) hiện số nhỏ hơn nhiều so với hồ sơ/hạng thành viên. Mỗi user dưới đây
-- được thêm đúng phần chênh lệch còn thiếu, tại Shop 1, để: total_spending (cột user)
-- == tổng giá dịch vụ của MỌI booking COMPLETED của user đó cộng lại trên toàn hệ thống.
-- ============================================================================
INSERT INTO pet_service (id, shop_id, service_name, category, price, duration_minutes, description,
                          active, camera_enabled, cage_size, room_type,
                          camera_tiers, camera_tier_prices, camera_tier_labels, camera_description) VALUES
(15, 1, 'Gói Trị Liệu Chuyên Sâu Cao Cấp', 'SPA_GROOMING', 3000000, 200,
    'Liệu trình chăm sóc cao cấp dài ngày, phục hồi da lông và thể trạng toàn diện.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(16, 1, 'Gói Chăm Sóc Định Kỳ Nâng Cao', 'SPA_GROOMING', 1300000, 150,
    'Gói chăm sóc định kỳ hàng tháng: tắm dưỡng, cắt tỉa, kiểm tra sức khỏe cơ bản.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(17, 1, 'Gói Vệ Sinh & Chăm Sóc Cơ Bản Nâng Cao', 'SPA_GROOMING', 300000, 100,
    'Vệ sinh tai, cắt móng, tắm sấy và chải lông kỹ hơn gói cơ bản.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(18, 1, 'Cắt Móng & Vệ Sinh Tai', 'SPA_GROOMING', 50000, 15,
    'Dịch vụ nhanh: cắt móng và vệ sinh tai cho thú cưng.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(19, 1, 'Gói Chăm Sóc VIP Toàn Diện Dài Hạn', 'SPA_GROOMING', 14000000, 300,
    'Gói chăm sóc VIP trọn gói dài hạn: trị liệu, spa, tư vấn dinh dưỡng và ưu tiên lịch hẹn riêng.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, check_in, check_out,
                     status, note, payos_order_code, created_at) VALUES
-- user5 (anhthu, GOLD 3.5M): đã có 500K (booking 1,2) → cộng thêm 3.000.000đ
(33, 5, 1, 1, 1, '2026-04-15 10:00:00', '2026-04-15 10:05:00', '2026-04-15 13:25:00',
    'COMPLETED', 'Mochi trị liệu chuyên sâu định kỳ', 202604010, '2026-04-14 18:00:00'),
-- user6 (binhminh, SILVER 1.5M): đã có 200K (booking 3, tại shop2) → cộng thêm 1.300.000đ tại shop1
(34, 6, 1, 3, 1, '2026-04-18 09:00:00', '2026-04-18 09:05:00', '2026-04-18 11:35:00',
    'COMPLETED', 'Luna chăm sóc định kỳ nâng cao', 202604011, '2026-04-17 19:00:00'),
-- user7 (camly, SILVER 800K): đã có 500K (booking 4, tại shop2) → cộng thêm 300.000đ tại shop1
(35, 7, 1, 4, 2, '2026-04-20 14:00:00', '2026-04-20 14:05:00', '2026-04-20 15:45:00',
    'COMPLETED', 'Max vệ sinh & chăm sóc cơ bản nâng cao', 202604012, '2026-04-19 20:00:00'),
-- user8 (ducmanh, BRONZE 200K): đã có 150K (booking 5) → cộng thêm 50.000đ
(36, 8, 1, 5, 1, '2026-04-22 11:00:00', '2026-04-22 11:05:00', '2026-04-22 11:20:00',
    'COMPLETED', 'Coco cắt móng & vệ sinh tai', 202604013, '2026-04-21 09:00:00'),
-- user13 (vip.customer, PLATINUM 15M): đã có 1.000.000đ (booking 27-30) → cộng thêm 14.000.000đ
(37, 13, 1, 6, 2, '2026-04-25 09:00:00', '2026-04-25 09:05:00', '2026-04-25 14:05:00',
    'COMPLETED', 'Diamond gói chăm sóc VIP toàn diện dài hạn', 202604014, '2026-04-24 17:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(33, 15),
(34, 16),
(35, 17),
(36, 18),
(37, 19);

INSERT INTO payment (id, booking_id, amount, method, status, payos_order_code,
                     gateway_transaction_id, payment_time, description) VALUES
(33, 33, 3000000,  'PAYOS', 'SUCCESS', 202604010, 'TXN-SPA-011', '2026-04-14 18:05:00', 'Thanh toán Gói Trị Liệu Chuyên Sâu Cao Cấp'),
(34, 34, 1300000,  'PAYOS', 'SUCCESS', 202604011, 'TXN-SPA-012', '2026-04-17 19:05:00', 'Thanh toán Gói Chăm Sóc Định Kỳ Nâng Cao'),
(35, 35, 300000,   'PAYOS', 'SUCCESS', 202604012, 'TXN-SPA-013', '2026-04-19 20:05:00', 'Thanh toán Gói Vệ Sinh & Chăm Sóc Cơ Bản Nâng Cao'),
(36, 36, 50000,    'PAYOS', 'SUCCESS', 202604013, 'TXN-SPA-014', '2026-04-21 09:05:00', 'Thanh toán Cắt Móng & Vệ Sinh Tai'),
(37, 37, 14000000, 'PAYOS', 'SUCCESS', 202604014, 'TXN-VIP-006', '2026-04-24 17:05:00', 'Thanh toán Gói Chăm Sóc VIP Toàn Diện Dài Hạn');

INSERT INTO booking_history (booking_id, old_status, new_status, changed_at, changed_by) VALUES
(33, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-04-14 18:05:00', 'SYSTEM'),
(33, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-04-14 19:00:00', 'owner.spa@peteye.vn'),
(33, 'CONFIRMED',             'IN_PROGRESS',            '2026-04-15 10:10:00', 'staff1.spa@peteye.vn'),
(33, 'IN_PROGRESS',           'COMPLETED',              '2026-04-15 13:25:00', 'staff1.spa@peteye.vn'),
(34, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-04-17 19:05:00', 'SYSTEM'),
(34, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-04-17 20:00:00', 'owner.spa@peteye.vn'),
(34, 'CONFIRMED',             'IN_PROGRESS',            '2026-04-18 09:10:00', 'staff1.spa@peteye.vn'),
(34, 'IN_PROGRESS',           'COMPLETED',              '2026-04-18 11:35:00', 'staff1.spa@peteye.vn'),
(35, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-04-19 20:05:00', 'SYSTEM'),
(35, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-04-19 21:00:00', 'owner.spa@peteye.vn'),
(35, 'CONFIRMED',             'IN_PROGRESS',            '2026-04-20 14:10:00', 'staff2.spa@peteye.vn'),
(35, 'IN_PROGRESS',           'COMPLETED',              '2026-04-20 15:45:00', 'staff2.spa@peteye.vn'),
(36, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-04-21 09:05:00', 'SYSTEM'),
(36, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-04-21 10:00:00', 'owner.spa@peteye.vn'),
(36, 'CONFIRMED',             'IN_PROGRESS',            '2026-04-22 11:10:00', 'staff1.spa@peteye.vn'),
(36, 'IN_PROGRESS',           'COMPLETED',              '2026-04-22 11:20:00', 'staff1.spa@peteye.vn'),
(37, 'PENDING_PAYMENT',       'WAITING_SHOP_APPROVAL', '2026-04-24 17:05:00', 'SYSTEM'),
(37, 'WAITING_SHOP_APPROVAL', 'CONFIRMED',              '2026-04-24 18:00:00', 'owner.spa@peteye.vn'),
(37, 'CONFIRMED',             'IN_PROGRESS',            '2026-04-25 09:10:00', 'staff2.spa@peteye.vn'),
(37, 'IN_PROGRESS',           'COMPLETED',              '2026-04-25 14:05:00', 'staff2.spa@peteye.vn');

-- SET FOREIGN_KEY_CHECKS = 1;  -- disabled: seed data is consistent by design

-- ============================================================================
-- VOUCHER & MEMBERSHIP TIER DEMO DATA
-- Dùng để test toàn bộ luồng voucher trên app
-- ============================================================================

-- ============================================================================
-- VOUCHERS (4 loại để cover đủ test cases)
-- ============================================================================
INSERT INTO voucher (id, code, discount_type, discount_value, min_order_value,
                     max_discount_amount, valid_days, issue_quantity, target_tier_id, is_active) VALUES
-- V1: GOLD tier, PERCENTAGE 10%, cap 35.000đ, min 200.000đ — dùng cho booking 350K → giảm 35K
(1, 'GOLD_WELCOME',    'PERCENTAGE',   10,     200000, 35000,  30, 2, 3, 1),
-- V2: SILVER tier, FIXED 30.000đ, min 150.000đ
(2, 'SILVER_FIXED30',  'FIXED_AMOUNT', 30000,  150000, NULL,   14, 1, 2, 1),
-- V3: PLATINUM tier, PERCENTAGE 15%, cap 50.000đ, min 300.000đ
(3, 'PLATINUM_15PCT',  'PERCENTAGE',   15,     300000, 50000,  60, 3, 4, 1),
-- V4: GOLD tier, PERCENTAGE 50%, cap 30.000đ — để test cap thực sự chặn
(4, 'GOLD_BIG50',      'PERCENTAGE',   50,     100000, 30000,  30, 1, 3, 1),
-- V5: BRONZE tier (hạng thấp nhất) — trước đây bộ seed không có voucher nào nhắm BRONZE
(5, 'BRONZE_HELLO',    'FIXED_AMOUNT', 20000,  100000, NULL,   60, 1, 1, 1),
-- V6: SILVER tier, đã bị admin VÔ HIỆU HÓA sẵn (is_active=0) — test hiển thị "đã ngừng áp dụng"
(6, 'SILVER_REVOKED',  'FIXED_AMOUNT', 25000,  100000, NULL,   30, 1, 2, 0);

-- ============================================================================
-- USER_VOUCHER (pre-seed để test ngay, không cần trigger upgrade)
-- ============================================================================
INSERT INTO user_voucher (id, user_id, voucher_id, is_used, expires_at, used_at) VALUES
-- user5 (Phạm Anh Thư, GOLD): có 2x GOLD_WELCOME (chưa dùng)
(1,  5, 1, 0, DATE_ADD(NOW(), INTERVAL 25 DAY), NULL),
(2,  5, 1, 0, DATE_ADD(NOW(), INTERVAL 25 DAY), NULL),
-- user5: có 1x GOLD_BIG50 (chưa dùng) — test cap 30K trên booking 350K → giảm đúng 30K
(3,  5, 4, 0, DATE_ADD(NOW(), INTERVAL 20 DAY), NULL),
-- user5: có 1x GOLD_WELCOME đã DÙNG RỒI — test reject reuse
(4,  5, 1, 1, DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
-- user5: có 1x GOLD_WELCOME HẾT HẠN — test reject expired
(5,  5, 1, 0, '2026-01-01 00:00:00',            NULL),
-- user6 (Nguyễn Bình Minh, SILVER): có 1x SILVER_FIXED30
(6,  6, 2, 0, DATE_ADD(NOW(), INTERVAL 10 DAY), NULL),
-- user7 (Vũ Cẩm Ly, SILVER): có 1x SILVER_FIXED30 đã dùng — empty valid list
(7,  7, 2, 1, DATE_ADD(NOW(), INTERVAL 10 DAY), NOW()),
-- user8 (Hoàng Đức Mạnh, BRONZE): không có voucher gì — test empty state
-- user13 (Lý Thị Bích Vân, PLATINUM): 3x PLATINUM_15PCT
(8,  13, 3, 0, DATE_ADD(NOW(), INTERVAL 55 DAY), NULL),
(9,  13, 3, 0, DATE_ADD(NOW(), INTERVAL 55 DAY), NULL),
(10, 13, 3, 0, DATE_ADD(NOW(), INTERVAL 55 DAY), NULL),
-- user6: có thêm voucher sắp hết hạn (< 7 ngày) — test warning color trên app
(11, 6, 1, 0, DATE_ADD(NOW(), INTERVAL 3 DAY),  NULL),
-- user13: có thêm 1x SILVER_REVOKED (voucher template đã bị vô hiệu hóa từ trước) — vẫn giữ trong ví
-- nhưng phải hiện rõ trạng thái "đã ngừng áp dụng", không được chọn khi đặt lịch
(12, 13, 6, 0, DATE_ADD(NOW(), INTERVAL 55 DAY), NULL),
-- user6: 1x SILVER_FIXED30 ĐÃ DÙNG, gắn với booking 11 (WAITING_SHOP_APPROVAL) bên dưới —
-- dùng để test luồng "shop từ chối → voucher được hoàn lại"
(13, 6, 2, 1, DATE_ADD(NOW(), INTERVAL 10 DAY), NOW());

-- ============================================================================
-- Gắn voucher đã dùng vào booking 11 (WAITING_SHOP_APPROVAL, Tắm & Sấy 150.000đ, user6)
-- để có thể test ngay "shop từ chối → user_voucher.is_used quay lại 0" mà không cần
-- tự đặt lịch mới từ đầu. min_order_value của SILVER_FIXED30 là 150.000đ nên vừa đủ áp dụng.
-- ============================================================================
UPDATE booking SET voucher_id = 2, applied_user_voucher_id = 13, discount_amount = 30000
WHERE id = 11;
UPDATE payment SET amount = 120000, description = 'Thanh toán Tắm & Sấy (đã áp SILVER_FIXED30 -30.000đ)'
WHERE id = 11;

-- ============================================================================
-- SUMMARY
-- Users    : 17 (1 admin, 7 owners, 6 customers, 4 staff)
-- Shops    : 7  (SPA_GROOMING/MANUAL, CLINIC/AUTO, BOARDING+SPA_GROOMING/OPEN_POOL, SPA_GROOMING/MANUAL x2, CLINIC/AUTO, SPA_GROOMING/MANUAL)
--   Shop 3 (Pet Hotel 5 Sao) có cả dịch vụ BOARDING (9,10,11) và SPA_GROOMING
--   (12,13) để test đặt lịch/thanh toán gộp 2 loại dịch vụ khác nhau (xem booking 32).
--   Shop 4 (rating thấp 3.5), Shop 5 (giá cao), Shop 6 (Hà Nội), Shop 7 (La Gi) để test filter.
-- Services : 30
-- Pets     : 7  (5 gốc + 2 của VIP customer)
-- Bookings : 32
--   COMPLETED            : 9  (IDs  1-5, 27-30)
--   CANCELLED            : 4  (IDs  6-9)
--   WAITING_SHOP_APPROVAL: 4  (IDs 10-13) - Shop1 MANUAL
--   CONFIRMED            : 7  (IDs 14-18, 31, 32) - booking 32 gộp Lưu Trú + Spa
--   IN_PROGRESS          : 4  (IDs 19-22)
--   PENDING_PAYMENT      : 4  (IDs 23-26)
-- Payments : 32 (SUCCESS / REFUNDED / PENDING)
-- Reviews  : 5  (one per completed booking gốc)
-- Customer tiers tại Shop 1 (demo quản lý KH):
--   BRONZE  : Hoàng Đức Mạnh (user 8)
--   SILVER  : Vũ Cẩm Ly (7), Nguyễn Bình Minh (6)
--   GOLD    : Phạm Anh Thư (5)
--   PLATINUM: Lý Thị Bích Vân (13)
-- Vouchers : 6 (V1-V4 gốc + V5 BRONZE_HELLO + V6 SILVER_REVOKED [is_active=0, giữ bởi user13])
--   Booking 11 (WAITING_SHOP_APPROVAL) đã gắn sẵn voucher SILVER_FIXED30 đã dùng (user6)
--   để test "shop từ chối → voucher hoàn lại" ngay không cần thao tác tay từ đầu.
-- All passwords: 12345678
-- LƯU Ý: cần khởi động lại backend một lần (Hibernate ddl-auto=update) để tạo cột
-- booking.applied_user_voucher_id TRƯỚC KHI chạy script này, nếu không UPDATE cuối sẽ lỗi.
-- ============================================================================

-- ============================================================================
-- BỘ LỌC KHÁM PHÁ: khoảng cách GPS / mức giá / số sao tối thiểu
-- 3 shop gốc (1-3) đều rating 4.6-4.8 và ở cùng khu vực gần nhau (TP.HCM, cách
-- nhau vài km) nên không đủ để kiểm thử rõ ràng việc LOẠI TRỪ theo rating/giá/
-- khoảng cách. Thêm 3 shop mới (owner 14-16) để mỗi bộ lọc đều có ít nhất 1 shop
-- "trượt" rõ ràng khi áp dụng, dùng cho checklist test bên dưới.
-- ============================================================================
INSERT INTO `user` (id, email, password, full_name, phone, address, active, email_verified, tier_id, total_spending, failed_login_attempts) VALUES
(14, 'owner4@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Đỗ Thanh Hà',        '0901111004', '15 Nguyễn Văn Đậu, Quận Bình Thạnh, TP. Hồ Chí Minh', 1, 1, 1, 0, 0),
(15, 'owner5@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Ngô Quốc Bảo',       '0901111005', '200 Hai Bà Trưng, Quận 1, TP. Hồ Chí Minh',           1, 1, 1, 0, 0),
(16, 'owner6@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Bùi Thị Thanh Trúc', '0901111006', '10 Hàng Bài, Hoàn Kiếm, Hà Nội',                       1, 1, 1, 0, 0);

INSERT INTO user_roles (user_id, roles_id) VALUES
(14, 2), (15, 2), (16, 2);

-- Owner 17 + Shop 7: gần "24 Ỷ Lan, Tân An, La Gi, Lâm Đồng" — thêm riêng để bạn test
-- bằng vị trí GPS THẬT của mình (không cần giả lập tọa độ TP.HCM) khi không ở HCM.
-- Tọa độ dưới đây là ước lượng trung tâm phường Tân An/La Gi (không phải geocode chính
-- xác tới số nhà), đủ để test bán kính vài km quanh khu vực đó.
INSERT INTO `user` (id, email, password, full_name, phone, address, active, email_verified, tier_id, total_spending, failed_login_attempts) VALUES
(17, 'owner7@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Trần Văn Lộc', '0901111007', '24 Ỷ Lan, Tân An, La Gi, Lâm Đồng', 1, 1, 1, 0, 0),
(18, 'owner8@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Nguyễn Thị Tám', '0901111008', '123 Cầu Giấy, Hà Nội', 1, 1, 1, 0, 0),
(19, 'owner9@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Lê Văn Chín', '0901111009', '456 Đống Đa, Hà Nội', 1, 1, 1, 0, 0),
(20, 'owner10@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Phạm Mười', '0901111010', '789 Ba Đình, Hà Nội', 1, 1, 1, 0, 0);

INSERT INTO user_roles (user_id, roles_id) VALUES 
(17, 2), (18, 2), (19, 2), (20, 2);

-- Shop 4: rating THẤP (3.5) + giá RẺ toàn bộ + cách Shop 1 khoảng 1.3km
--   → dùng để test minRating loại shop này, và test radius hẹp vẫn thấy (gần).
-- Shop 5: rating CAO (4.9) + giá ĐẮT toàn bộ (900k-1.5M) + cách Shop 1 khoảng 4.7km
--   → dùng để test maxPrice loại shop này dù rating/khoảng cách đạt.
-- Shop 6: ở Hà Nội (cách TP.HCM ~1150km), rating/giá đều đạt điều kiện thông thường
--   → dùng để test bộ lọc khoảng cách loại shop này dù các filter khác đều pass.
INSERT INTO shop (id, owner_id, shop_name, shop_type, email, phone, address, city, latitude, longitude,
                  description, license_number, logo_url, open_time, close_time, working_days,
                  rating_avg, is_verified, status, assignment_mode, late_grace_period,
                  off_days, created_at) VALUES
(4, 14, 'Pet Care Bình Dân', 'SPA_GROOMING',
   'contact@petcarebinhdan.vn', '02812345004',
   '15 Nguyễn Văn Đậu, Phường 5, Quận Bình Thạnh, TP. Hồ Chí Minh', 'TP. Hồ Chí Minh',
   10.7860, 106.7010,
   'Tiệm chăm sóc thú cưng giá bình dân, phù hợp túi tiền sinh viên.',
   'GPKD-004-2024', NULL, '08:00', '20:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   3.5, 1, 'APPROVED', 'MANUAL', 15, NULL, NOW()),

(5, 15, 'Luxury Paws Spa', 'SPA_GROOMING',
   'contact@luxurypaws.vn', '02812345005',
   '200 Hai Bà Trưng, Phường Đa Kao, Quận 1, TP. Hồ Chí Minh', 'TP. Hồ Chí Minh',
   10.7480, 106.7250,
   'Spa cao cấp 5 sao cho thú cưng, dịch vụ trọn gói sang trọng.',
   'GPKD-005-2024', NULL, '09:00', '21:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   4.9, 1, 'APPROVED', 'MANUAL', 15, NULL, NOW()),

(6, 16, 'Phòng Khám Thú Y Hà Nội', 'CLINIC',
   'contact@thuyhanoi.vn', '02412345006',
   '10 Hàng Bài, Hoàn Kiếm, Hà Nội', 'Hà Nội',
   21.0285, 105.8542,
   'Phòng khám thú y uy tín tại Hà Nội, hơn 10 năm kinh nghiệm.',
   'GPKD-006-2024', NULL, '07:00', '19:00', 'MON,TUE,WED,THU,FRI,SAT',
   4.7, 1, 'PENDING', 'AUTO', 10, NULL, NOW()),

(7, 17, 'Thú Cưng La Gi', 'SPA_GROOMING',
   'contact@thucunglagi.vn', '02523456007',
   '24 Ỷ Lan, Tân An, La Gi, Lâm Đồng', 'Lâm Đồng',
   10.6907, 107.7802,
   'Tiệm chăm sóc thú cưng địa phương tại La Gi, phục vụ tận tâm, giá hợp lý.',
   'GPKD-007-2024', NULL, '08:00', '19:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   4.6, 1, 'PENDING', 'MANUAL', 15, NULL, NOW()),

(8, 18, 'Cầu Giấy Pet Spa', 'SPA',
   'contact@caugiaypetspa.vn', '02412345008',
   '123 Cầu Giấy, Hà Nội', 'Hà Nội',
   21.0333, 105.7958,
   'Dịch vụ spa làm đẹp toàn diện cho thú cưng tại khu vực Cầu Giấy.',
   'GPKD-008-2024', NULL, '08:00', '20:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   0.0, 0, 'PENDING', 'MANUAL', 15, NULL, NOW()),

(9, 19, 'Phòng Khám Thú Y Đống Đa', 'CLINIC',
   'contact@thuyidongda.vn', '02412345009',
   '456 Đống Đa, Hà Nội', 'Hà Nội',
   21.0125, 105.8239,
   'Chuyên khám chữa bệnh, tiêm phòng cho chó mèo uy tín.',
   'GPKD-009-2024', NULL, '08:00', '20:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   0.0, 0, 'REJECTED', 'MANUAL', 15, NULL, NOW()),

(10, 20, 'Khách Sạn Thú Cưng Ba Đình', 'BOARDING',
   'contact@badinhpethotel.vn', '02412345010',
   '789 Ba Đình, Hà Nội', 'Hà Nội',
   21.0357, 105.8361,
   'Dịch vụ lưu chuồng an toàn, sạch sẽ, có sân chơi rộng rãi cho các bé.',
   'GPKD-010-2024', NULL, '08:00', '20:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   0.0, 0, 'REJECTED', 'MANUAL', 15, NULL, NOW());

INSERT INTO shop_wallet (id, shop_id, frozen_balance, available_balance, total_earned, total_withdrawn) VALUES
(4, 4, 0.00, 0.00, 0.00, 0.00),
(5, 5, 0.00, 0.00, 0.00, 0.00),
(6, 6, 0.00, 0.00, 0.00, 0.00),
(7, 7, 0.00, 0.00, 0.00, 0.00),
(8, 8, 0.00, 0.00, 0.00, 0.00),
(9, 9, 0.00, 0.00, 0.00, 0.00),
(10, 10, 0.00, 0.00, 0.00, 0.00);

INSERT INTO pet_service (id, shop_id, service_name, category, price, duration_minutes, description,
                          active, camera_enabled, cage_size, room_type,
                          camera_tiers, camera_tier_prices, camera_tier_labels, camera_description) VALUES
-- Shop 4: toàn bộ dịch vụ giá rẻ (30k - 100k)
(20, 4, 'Cắt Móng', 'SPA_GROOMING', 30000, 15,
   'Cắt móng nhanh gọn cho thú cưng.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(21, 4, 'Tắm Cơ Bản', 'SPA_GROOMING', 70000, 45,
   'Tắm nhanh giá rẻ, phù hợp túi tiền sinh viên.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(22, 4, 'Tắm & Vệ Sinh Tai', 'SPA_GROOMING', 100000, 50,
   'Gói tắm kèm vệ sinh tai giá rẻ.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 5: toàn bộ dịch vụ giá cao (900k - 1.5tr)
(23, 5, 'Gói Spa Luxury Toàn Thân', 'SPA_GROOMING', 900000, 150,
   'Spa cao cấp trọn gói, sản phẩm nhập khẩu.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(24, 5, 'Gói Trị Liệu Massage 5 Sao', 'SPA_GROOMING', 1200000, 120,
   'Massage trị liệu chuyên sâu 5 sao.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(25, 5, 'Gói Chăm Sóc VIP Trọn Gói', 'SPA_GROOMING', 1500000, 180,
   'Chăm sóc toàn diện cao cấp nhất.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 6: giá vừa phải, ở Hà Nội
(26, 6, 'Khám Tổng Quát', 'CLINIC', 180000, 30,
   'Khám sức khỏe tổng quát cho thú cưng.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(27, 6, 'Tiêm Phòng', 'CLINIC', 150000, 20,
   'Tiêm vaccine phòng bệnh dại và các bệnh thường gặp.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 7 (La Gi): giá vừa phải, dùng để test bằng vị trí GPS thật
(28, 7, 'Tắm & Sấy', 'SPA_GROOMING', 100000, 60,
   'Tắm sạch, sấy khô và chải lông cho thú cưng.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(29, 7, 'Cắt Tỉa Lông', 'SPA_GROOMING', 150000, 75,
   'Cắt tỉa lông theo yêu cầu.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(30, 7, 'Spa Toàn Thân', 'SPA_GROOMING', 280000, 100,
   'Tắm thảo dược, massage, cắt móng và vệ sinh tai.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL);
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 1;
