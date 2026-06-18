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
-- IDs: 1=admin, 2-4=owners, 5-8=customers, 9-12=staff accounts
-- ============================================================================
INSERT INTO `user` (id, email, password, full_name, phone, address, active, email_verified, tier_id, total_spending) VALUES
(1,  'admin@peteye.vn',       '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Admin PetEye',       '0900000001', 'HCM', 1, 1, 1, 0),
(2,  'owner.spa@peteye.vn',   '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Nguyen Minh Tuan',   '0901111001', '123 Le Van Sy, Q.3, HCM',       1, 1, 1, 0),
(3,  'owner.clinic@peteye.vn','$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Tran Thi Lan',       '0901111002', '45 Dinh Tien Hoang, Q.BT, HCM', 1, 1, 1, 0),
(4,  'owner.hotel@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Le Hoang Nam',       '0901111003', '88 Nguyen Oanh, Q.GV, HCM',     1, 1, 1, 0),
(5,  'anhthu@gmail.com',      '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Pham Anh Thu',       '0912345001', '10 Tran Huy Lieu, Q.PN, HCM',   1, 1, 3, 3500000),
(6,  'binhminh@gmail.com',    '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Nguyen Binh Minh',   '0912345002', '5 Vo Thi Sau, Q.1, HCM',        1, 1, 2, 1500000),
(7,  'camly@gmail.com',       '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Vu Cam Ly',          '0912345003', '22 Ly Tu Trong, Q.1, HCM',      1, 1, 2, 800000),
(8,  'ducmanh@gmail.com',     '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Hoang Duc Manh',     '0912345004', '7 Nguyen Thi Minh Khai, Q.3, HCM', 1, 1, 1, 200000),
(9,  'staff1.spa@peteye.vn',  '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Dang Thi Hoa',       '0933001001', 'HCM', 1, 1, 1, 0),
(10, 'staff2.spa@peteye.vn',  '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Bui Van Hung',       '0933001002', 'HCM', 1, 1, 1, 0),
(11, 'staff.clinic@peteye.vn','$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Phan Thi Bich',      '0933002001', 'HCM', 1, 1, 1, 0),
(12, 'staff.hotel@peteye.vn', '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO', 'Dinh Quoc Tuan',     '0933003001', 'HCM', 1, 1, 1, 0);

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
(8,  3),
(9,  4),
(10, 4),
(11, 4),
(12, 4);

-- ============================================================================
-- SHOPS
-- ============================================================================
INSERT INTO shop (id, owner_id, shop_name, shop_type, email, phone, address, city, latitude, longitude,
                  description, license_number, logo_url, open_time, close_time, working_days,
                  rating_avg, is_verified, status, assignment_mode, late_grace_period) VALUES
(1, 2, 'Meo Kute Spa', 'SPA',
   'contact@meokutespa.vn', '02812345001',
   '123 Le Van Sy, Phuong 13, Quan 3, TP.HCM', 'TP.HCM',
   10.7769, 106.6938,
   'Spa & grooming cao cap cho meo va cho. Doi ngu chuyen nghiep, tan tam.',
   'GPKD-001-2024', NULL, '08:00', '19:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   4.6, 1, 'APPROVED', 'MANUAL', 15),

(2, 3, 'Thu Y An Tam', 'CLINIC',
   'contact@thuyantam.vn', '02812345002',
   '45 Dinh Tien Hoang, Phuong 3, Quan Binh Thanh, TP.HCM', 'TP.HCM',
   10.8038, 106.7162,
   'Phong kham thu y uy tin voi doi ngu bac si giau kinh nghiem.',
   'GPKD-002-2024', NULL, '07:30', '18:00', 'MON,TUE,WED,THU,FRI,SAT',
   4.8, 1, 'APPROVED', 'AUTO', 10),

(3, 4, 'Pet Hotel 5 Sao', 'BOARDING',
   'contact@pethotel5sao.vn', '02812345003',
   '88 Nguyen Oanh, Phuong 17, Quan Go Vap, TP.HCM', 'TP.HCM',
   10.8451, 106.6657,
   'Khach san thu cung 5 sao voi camera giam sat 24/7. Phong rieng va phong chung.',
   'GPKD-003-2024', NULL, '06:00', '22:00', 'MON,TUE,WED,THU,FRI,SAT,SUN',
   4.7, 1, 'APPROVED', 'OPEN_POOL', 30);

-- ============================================================================
-- SHOP WALLETS
-- ============================================================================
INSERT INTO shop_wallet (id, shop_id, frozen_balance, available_balance, total_earned, total_withdrawn) VALUES
(1, 1, 0.00,       3450000.00, 5450000.00, 2000000.00),
(2, 2, 0.00,       2950000.00, 3950000.00, 1000000.00),
(3, 3, 350000.00,  1850000.00, 2200000.00, 0.00);

-- ============================================================================
-- STAFF
-- ============================================================================
INSERT INTO staff (id, shop_id, user_id, full_name, role, phone, specialization, is_active) VALUES
(1, 1, 9,  'Dang Thi Hoa',  'Groomer',        '0933001001', 'Tam say, cat tia', 1),
(2, 1, 10, 'Bui Van Hung',  'Senior Groomer', '0933001002', 'Spa & nhuom long',  1),
(3, 2, 11, 'Phan Thi Bich', 'Bac si thu y',   '0933002001', 'Noi khoa, phau thuat', 1),
(4, 3, 12, 'Dinh Quoc Tuan','Pet Sitter',     '0933003001', 'Cham soc, huan luyen', 1);

-- ============================================================================
-- PETS
-- ============================================================================
INSERT INTO pet (id, owner_id, name, species, breed, gender, color, weight, dob, sterilized,
                 health_note, is_active) VALUES
(1, 5, 'Mochi', 'CAT', 'Munchkin',    'FEMALE', 'Trang vang', 3.2, '2022-03-15', 1, 'Khoe manh', 1),
(2, 5, 'Buddy', 'DOG', 'Poodle',      'MALE',   'Nau caramel', 4.5, '2021-07-20', 1, 'Di ung hai san', 1),
(3, 6, 'Luna',  'CAT', 'British SH',  'FEMALE', 'Xam xanh',   4.0, '2020-11-01', 1, 'Khoe manh', 1),
(4, 7, 'Max',   'DOG', 'Labrador',    'MALE',   'Vang',        22.0,'2019-05-10', 0, 'Khop can theo doi', 1),
(5, 8, 'Coco',  'RABBIT','Holland Lop','FEMALE','Trang den',   2.1, '2023-01-25', 0, 'Khoe manh', 1);

-- ============================================================================
-- PET SERVICES
-- ============================================================================
INSERT INTO pet_service (id, shop_id, service_name, category, price, duration_minutes, description,
                          active, camera_enabled, cage_size, room_type,
                          camera_tiers, camera_tier_prices, camera_tier_labels, camera_description) VALUES
-- Shop 1: SPA/GROOMING
(1, 1, 'Tam & Say Co Ban', 'GROOMING', 150000, 60,
   'Tam sach voi dau goi chuyen dung, say kho va chai long.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 1, 'Cat Tia Long', 'GROOMING', 200000, 90,
   'Cat tia theo yeu cau, tao kieu long dep cho thu cung.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 1, 'Spa Toan Than', 'SPA', 350000, 120,
   'Tam thao duoc, massage, cat mong, ve sinh tai va tao kieu.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 1, 'Nhuom Long Nghe Thuat', 'GROOMING', 500000, 180,
   'Nhuom long bang mau an toan, tao kieu doc dao theo yeu cau.',
   0, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 2: CLINIC
(5, 2, 'Kham Tong Quat', 'CLINIC', 200000, 30,
   'Kham suc khoe toan dien, tu van dinh duong va cham soc.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(6, 2, 'Tiem Phong', 'CLINIC', 150000, 20,
   'Tiem vaccine phong benh dai, carre, parvo va cac benh thuong gap.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(7, 2, 'Xet Nghiem Mau', 'CLINIC', 300000, 45,
   'Xet nghiem cong thuc mau toan phan, danh gia chuc nang gan than.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 2, 'Sieu Am O Bung', 'CLINIC', 400000, 60,
   'Sieu am chan doan cac benh ly noi tang, thai san.',
   1, 0, NULL, NULL, NULL, NULL, NULL, NULL),
-- Shop 3: BOARDING (with camera)
(9, 3, 'Luu Tru Phong Chung', 'BOARDING', 200000, 1440,
   'Phong chung thoai mai, an uong day du, vui choi cung cac be khac.',
   1, 1, '["SMALL","MEDIUM"]', '["SHARED"]',
   '["BASIC","HD"]',
   '{"BASIC":0,"HD":60000}',
   '{"BASIC":"Xem co ban (mien phi)","HD":"Camera HD (60k/ngay)"}',
   'Camera giam sat 24/7, xem qua ung dung'),
(10, 3, 'Luu Tru Phong Rieng', 'BOARDING', 350000, 1440,
    'Phong rieng yen tinh, cham soc ca nhan, 2 bua/ngay.',
    1, 1, '["MEDIUM","LARGE"]', '["PRIVATE"]',
    '["BASIC","HD","AI"]',
    '{"BASIC":0,"HD":60000,"AI":200000}',
    '{"BASIC":"Xem co ban (mien phi)","HD":"Camera HD (60k/ngay)","AI":"AI theo doi hanh vi (200k/ngay)"}',
    'Camera HD & AI nhan dien hanh vi bat thuong'),
(11, 3, 'Luu Tru VIP Suite', 'BOARDING', 550000, 1440,
    'Phong VIP rong rai, giuong em ai, do choi rieng, cham soc ca nhan toi da.',
    1, 1, '["LARGE"]', '["PRIVATE"]',
    '["BASIC","HD","AI"]',
    '{"BASIC":0,"HD":60000,"AI":200000}',
    '{"BASIC":"Xem co ban (mien phi)","HD":"Camera HD (60k/ngay)","AI":"AI theo doi hanh vi (200k/ngay)"}',
    'Camera AI cao cap theo doi suc khoe va hanh vi 24/7'),
(12, 3, 'Tam & Ve Sinh', 'GROOMING', 120000, 90,
    'Tam sach, ve sinh tai, cat mong cho thu cung luu tru.',
    1, 0, NULL, NULL, NULL, NULL, NULL, NULL);

-- ============================================================================
-- BOOKINGS
-- ============================================================================

-- 1. COMPLETED (5) -- booking IDs 1-5, thang 5/2026
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, check_in, check_out,
                     status, note, payos_order_code, created_at) VALUES
(1, 5, 1, 1, 1, '2026-05-05 09:00:00', '2026-05-05 09:05:00', '2026-05-05 10:10:00',
   'COMPLETED', NULL, 202405001, '2026-05-04 20:00:00'),
(2, 5, 1, 2, 2, '2026-05-10 14:00:00', '2026-05-10 14:02:00', '2026-05-10 15:35:00',
   'COMPLETED', NULL, 202405002, '2026-05-09 18:30:00'),
(3, 6, 2, 3, 3, '2026-05-12 08:30:00', '2026-05-12 08:35:00', '2026-05-12 09:10:00',
   'COMPLETED', 'Luna can kiem tra rang', 202405003, '2026-05-11 21:00:00'),
(4, 7, 2, 4, 3, '2026-05-20 10:00:00', '2026-05-20 10:00:00', '2026-05-20 11:05:00',
   'COMPLETED', 'Max dau khop truoc trai', 202405004, '2026-05-19 15:00:00'),
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
   'CANCELLED', NULL, 'Khach huy: meo bi om khong the den kham', 202405006, '2026-05-14 10:00:00'),
(7, 6, 1, 3, 1, '2026-05-22 15:00:00',
   'CANCELLED', NULL, 'Khach huy: ban dot xuat', 202405007, '2026-05-21 08:00:00'),
(8, 7, 3, 4, 4, '2026-06-01 12:00:00',
   'CANCELLED', 'Phong rieng cho Max', 'Shop huy: het phong loai LARGE', 202406001, '2026-05-30 19:00:00'),
(9, 8, 2, 5, 3, '2026-06-05 09:30:00',
   'CANCELLED', NULL, 'Khach huy: thay doi ke hoach', 202406002, '2026-06-04 09:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(6, 5),
(7, 2),
(8, 10),
(9, 6);

-- 3. WAITING_SHOP_APPROVAL (4) -- booking IDs 10-13, Shop1 MANUAL
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(10, 5, 1, 1, NULL, '2026-06-20 09:00:00', 'WAITING_SHOP_APPROVAL', 'Mochi can spa nhe nhang', 202406003, '2026-06-17 10:30:00'),
(11, 6, 1, 3, NULL, '2026-06-21 14:00:00', 'WAITING_SHOP_APPROVAL', NULL, 202406004, '2026-06-17 15:00:00'),
(12, 7, 1, 4, NULL, '2026-06-22 10:00:00', 'WAITING_SHOP_APPROVAL', 'Max can groomer co kinh nghiem voi cho lon', 202406005, '2026-06-18 07:00:00'),
(13, 8, 1, 5, NULL, '2026-06-23 11:00:00', 'WAITING_SHOP_APPROVAL', NULL, 202406006, '2026-06-18 08:30:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(10, 3),
(11, 1),
(12, 2),
(13, 1);

-- 4. CONFIRMED (5) -- booking IDs 14-18
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(14, 5, 2, 2, 3, '2026-06-20 08:30:00', 'CONFIRMED', 'Buddy can tiem nhac lai vaccine dai', 202406007, '2026-06-15 09:00:00'),
(15, 6, 2, 3, 3, '2026-06-21 09:00:00', 'CONFIRMED', NULL, 202406008, '2026-06-16 10:00:00'),
(16, 5, 3, 1, 4, '2026-06-25 14:00:00', 'CONFIRMED', 'Mochi luu tru 3 ngay, can phong yen tinh', 202406009, '2026-06-14 20:00:00'),
(17, 7, 2, 4, 3, '2026-06-24 10:00:00', 'CONFIRMED', 'Max sieu am theo lich tai kham', 202406010, '2026-06-16 14:00:00'),
(18, 8, 3, 5, 4, '2026-06-26 10:00:00', 'CONFIRMED', 'Coco luu tru 2 ngay cuoi tuan', 202406011, '2026-06-15 11:00:00');

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
    'IN_PROGRESS', 'Luna can xet nghiem mau dinh ky', 202406013, '2026-06-17 21:00:00'),
(21, 7, 1, 4, 2, '2026-06-18 10:00:00', '2026-06-18 10:03:00', '2026-06-18 10:15:00',
    'IN_PROGRESS', 'Max can spa toan than', 202406014, '2026-06-17 18:00:00'),
(22, 8, 3, 5, 4, '2026-06-17 12:00:00', '2026-06-17 12:05:00', '2026-06-17 12:10:00',
    'IN_PROGRESS', 'Coco luu tru 2 ngay', 202406015, '2026-06-16 09:00:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(19, 2),
(20, 7),
(21, 3),
(22, 9);

-- 6. PENDING_PAYMENT (4) -- booking IDs 23-26, vua tao
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(23, 5, 3, 1, NULL, '2026-06-27 14:00:00', 'PENDING_PAYMENT', 'Mochi luu tru phong VIP 2 ngay', 202406016, '2026-06-18 09:00:00'),
(24, 6, 1, 3, NULL, '2026-06-25 15:00:00', 'PENDING_PAYMENT', NULL, 202406017, '2026-06-18 09:30:00'),
(25, 7, 2, 4, NULL, '2026-06-26 09:00:00', 'PENDING_PAYMENT', 'Kham tong quat va xet nghiem mau', 202406018, '2026-06-18 10:00:00'),
(26, 8, 2, 5, NULL, '2026-06-28 10:00:00', 'PENDING_PAYMENT', NULL, 202406019, '2026-06-18 10:30:00');

INSERT INTO booking_services (booking_id, service_id) VALUES
(23, 11),
(24, 3),
(25, 5), (25, 7),
(26, 6);

-- ============================================================================
-- PAYMENTS
-- ============================================================================
INSERT INTO payment (id, booking_id, amount, method, status, payos_order_code,
                     gateway_transaction_id, payment_time, description) VALUES
-- COMPLETED → SUCCESS
(1,  1,  150000, 'PAYOS', 'SUCCESS', 202405001, 'TXN-SPA-001', '2026-05-04 20:05:00', 'Thanh toan Tam & Say'),
(2,  2,  350000, 'PAYOS', 'SUCCESS', 202405002, 'TXN-SPA-002', '2026-05-09 18:35:00', 'Thanh toan Spa Toan Than'),
(3,  3,  200000, 'PAYOS', 'SUCCESS', 202405003, 'TXN-CLI-001', '2026-05-11 21:05:00', 'Thanh toan Kham Tong Quat'),
(4,  4,  500000, 'PAYOS', 'SUCCESS', 202405004, 'TXN-CLI-002', '2026-05-19 15:05:00', 'Thanh toan Kham + Xet nghiem'),
(5,  5,  150000, 'PAYOS', 'SUCCESS', 202405005, 'TXN-SPA-003', '2026-05-27 10:05:00', 'Thanh toan Tam & Say'),
-- CANCELLED → REFUNDED
(6,  6,  200000, 'PAYOS', 'REFUNDED', 202405006, 'TXN-CLI-003', '2026-05-14 10:05:00', 'Hoan tien huy lich'),
(7,  7,  200000, 'PAYOS', 'REFUNDED', 202405007, 'TXN-SPA-004', '2026-05-21 08:05:00', 'Hoan tien huy lich'),
(8,  8,  350000, 'PAYOS', 'REFUNDED', 202406001, 'TXN-HOT-001', '2026-05-30 19:05:00', 'Hoan tien huy phong'),
(9,  9,  150000, 'PAYOS', 'REFUNDED', 202406002, 'TXN-CLI-004', '2026-06-04 09:05:00', 'Hoan tien huy lich'),
-- WAITING_SHOP_APPROVAL → SUCCESS (da thanh toan, cho shop duyet)
(10, 10, 350000, 'PAYOS', 'SUCCESS', 202406003, 'TXN-SPA-005', '2026-06-17 10:35:00', 'Thanh toan Spa Toan Than'),
(11, 11, 150000, 'PAYOS', 'SUCCESS', 202406004, 'TXN-SPA-006', '2026-06-17 15:05:00', 'Thanh toan Tam & Say'),
(12, 12, 200000, 'PAYOS', 'SUCCESS', 202406005, 'TXN-SPA-007', '2026-06-18 07:05:00', 'Thanh toan Cat Tia Long'),
(13, 13, 150000, 'PAYOS', 'SUCCESS', 202406006, 'TXN-SPA-008', '2026-06-18 08:35:00', 'Thanh toan Tam & Say'),
-- CONFIRMED → SUCCESS
(14, 14, 150000, 'PAYOS', 'SUCCESS', 202406007, 'TXN-CLI-005', '2026-06-15 09:05:00', 'Thanh toan Tiem Phong'),
(15, 15, 350000, 'PAYOS', 'SUCCESS', 202406008, 'TXN-CLI-006', '2026-06-16 10:05:00', 'Thanh toan Kham + Tiem'),
(16, 16, 200000, 'PAYOS', 'SUCCESS', 202406009, 'TXN-HOT-002', '2026-06-14 20:05:00', 'Thanh toan Luu Tru'),
(17, 17, 400000, 'PAYOS', 'SUCCESS', 202406010, 'TXN-CLI-007', '2026-06-16 14:05:00', 'Thanh toan Sieu Am'),
(18, 18, 200000, 'PAYOS', 'SUCCESS', 202406011, 'TXN-HOT-003', '2026-06-15 11:05:00', 'Thanh toan Luu Tru'),
-- IN_PROGRESS → SUCCESS
(19, 19, 200000, 'PAYOS', 'SUCCESS', 202406012, 'TXN-SPA-009', '2026-06-17 20:05:00', 'Thanh toan Cat Tia Long'),
(20, 20, 300000, 'PAYOS', 'SUCCESS', 202406013, 'TXN-CLI-008', '2026-06-17 21:05:00', 'Thanh toan Xet Nghiem Mau'),
(21, 21, 350000, 'PAYOS', 'SUCCESS', 202406014, 'TXN-SPA-010', '2026-06-17 18:05:00', 'Thanh toan Spa Toan Than'),
(22, 22, 200000, 'PAYOS', 'SUCCESS', 202406015, 'TXN-HOT-004', '2026-06-16 09:05:00', 'Thanh toan Luu Tru'),
-- PENDING_PAYMENT → PENDING
(23, 23, 550000, 'PAYOS', 'PENDING', 202406016, NULL, NULL, 'Cho thanh toan Luu Tru VIP'),
(24, 24, 350000, 'PAYOS', 'PENDING', 202406017, NULL, NULL, 'Cho thanh toan Spa Toan Than'),
(25, 25, 500000, 'PAYOS', 'PENDING', 202406018, NULL, NULL, 'Cho thanh toan Kham + Xet nghiem'),
(26, 26, 150000, 'PAYOS', 'PENDING', 202406019, NULL, NULL, 'Cho thanh toan Tiem Phong');

-- ============================================================================
-- REVIEWS (cho COMPLETED bookings)
-- ============================================================================
INSERT INTO review (id, shop_id, user_id, service_id, rating, comment, created_at, reply, replied_at) VALUES
(1, 1, 5, 1, 5,
   'Mochi duoc cham soc rat tot! Nhan vien nhe nhang, chuyen nghiep. Long be sang va mem hon han.',
   '2026-05-05 11:00:00',
   'Cam on chi da tin tuong Meo Kute Spa! Mochi that dang yeu, hen gap lai chi va be nhe ^^',
   '2026-05-05 14:00:00'),
(2, 1, 5, 3, 5,
   'Buddy duoc spa rat ky, mui thom ma khong hac. Be vui ve khi ve nha, khong bi stress.',
   '2026-05-10 16:00:00',
   'Cam on anh/chi! Buddy rat ngoan trong suot buoi spa. Hen gap lai!',
   '2026-05-11 09:00:00'),
(3, 2, 6, 5, 5,
   'Bac si rat tan tinh, giai thich ro rang tinh trang suc khoe cua Luna. Phong kham sach se, thoang mat.',
   '2026-05-12 10:00:00', NULL, NULL),
(4, 2, 7, 5, 4,
   'Kham nhanh va chuyen nghiep. Chi hoi dong nen cho lau mot chut. Bac si giai thich ve khop cua Max rat chi tiet.',
   '2026-05-20 12:00:00',
   'Cam on anh da phan hoi! Chung toi dang cai thien thoi gian cho. Hen gap lai Max!',
   '2026-05-20 15:00:00'),
(5, 1, 8, 1, 4,
   'Nhan vien than thien, Coco duoc tam sach. Hoi nho so voi gia nhung chat luong on.',
   '2026-05-28 13:00:00', NULL, NULL);

-- ============================================================================
-- NOTIFICATIONS
-- ============================================================================
INSERT INTO notification (id, user_id, title, content, notification_type, is_read, created_at) VALUES
-- BOOKING (giữ như cũ)
(1,  5, 'Dat lich thanh cong', 'Lich Spa Toan Than cho Buddy vao 14:00 ngay 09/05 da duoc xac nhan.', 'BOOKING', 1, '2026-05-09 18:36:00'),
(2,  5, 'Hoan thanh dich vu',  'Buddy da hoan thanh dich vu Spa Toan Than. Hay de lai danh gia cho chung toi!', 'BOOKING', 1, '2026-05-10 15:40:00'),
(3,  6, 'Dat lich thanh cong', 'Lich Kham Tong Quat cho Luna vao 08:30 ngay 12/05 da duoc xac nhan.', 'BOOKING', 1, '2026-05-11 21:06:00'),
(4,  7, 'Dat lich thanh cong', 'Lich Kham + Xet nghiem cho Max vao 10:00 ngay 20/05 da duoc xac nhan.', 'BOOKING', 1, '2026-05-19 15:06:00'),
(5,  5, 'Cho shop xac nhan',   'Lich Spa Toan Than cho Mochi vao 09:00 ngay 20/06 dang cho xac nhan tu Meo Kute Spa.', 'BOOKING', 0, '2026-06-17 10:36:00'),
(6,  6, 'Cho shop xac nhan',   'Lich Tam & Say cho Luna vao 14:00 ngay 21/06 dang cho xac nhan tu Meo Kute Spa.', 'BOOKING', 0, '2026-06-17 15:06:00'),
(7,  2, 'Dat lich moi',        'Pham Anh Thu dat Spa Toan Than cho Mochi vao 09:00 ngay 20/06. Vui long xac nhan.', 'BOOKING', 0, '2026-06-17 10:36:00'),
(8,  2, 'Dat lich moi',        'Nguyen Binh Minh dat Tam & Say cho Luna vao 14:00 ngay 21/06. Vui long xac nhan.', 'BOOKING', 0, '2026-06-17 15:06:00'),
(9,  5, 'Dich vu dang bat dau','Buddy dang duoc cat tia long tai Meo Kute Spa. Du kien hoan thanh luc 10:30.', 'BOOKING', 0, '2026-06-18 09:10:00'),
(10, 6, 'Dich vu dang bat dau','Luna dang duoc xet nghiem tai Thu Y An Tam.', 'BOOKING', 0, '2026-06-18 08:40:00'),

-- GENERAL
(11, 5, 'Chao mung ban den voi Pet Eye', 'Cam on ban da dang ky! Kham pha hang tram spa va benh vien thu y uy tin gan ban.', 'GENERAL', 1, '2026-05-01 09:00:00'),
(12, 6, 'Cap nhat phien ban moi', 'Pet Eye vua ra mat phien ban 2.0 voi tinh nang dat lich nhanh va theo doi lich su kham benh.', 'GENERAL', 1, '2026-05-15 10:00:00'),
(13, 7, 'Thong bao nghi le', 'Cac shop doi tac se nghi le 02/09. Vui long dat lich som de tranh giai doan cao diem.', 'GENERAL', 0, '2026-06-15 08:00:00'),

-- PROMOTION
(14, 5, 'Uu dai 20% dich vu Spa', 'Nhan ma giam gia SPA20 cho lan dat lich Spa Toan Than tiep theo, ap dung den 30/06.', 'PROMOTION', 0, '2026-06-10 09:00:00'),
(15, 6, 'Chuong trinh thanh vien moi', 'Tich diem moi lan su dung dich vu de doi qua tang hap dan tu Thu Y An Tam.', 'PROMOTION', 0, '2026-06-12 14:00:00'),
(16, 7, 'Khuyen mai mua he', 'Giam 15% goi Kham Tong Quat cho thu cung trong thang 6. Dat lich ngay de khong bo lo!', 'PROMOTION', 0, '2026-06-16 11:00:00'),

-- REMINDER
(17, 5, 'Nhac lich hen', 'Lich Spa Toan Than cho Mochi vao 09:00 ngay mai (20/06) tai Meo Kute Spa. Dung quen nhe!', 'REMINDER', 0, '2026-06-19 18:00:00'),
(18, 6, 'Nhac thanh toan', 'Don dat lich Spa Toan Than cho Luna ngay 25/06 dang cho thanh toan. Vui long hoan tat trong 30 phut.', 'REMINDER', 0, '2026-06-18 09:31:00'),
(19, 7, 'Nhac tiem phong dinh ky', 'Max da den han tiem phong nhac lai. Hay dat lich tai Thu Y An Tam de duoc kiem tra som.', 'REMINDER', 0, '2026-06-17 08:00:00'),

-- SYSTEM
(20, 5, 'Mat khau da duoc thay doi', 'Mat khau tai khoan cua ban vua duoc cap nhat thanh cong. Neu khong phai ban, hay lien he ho tro ngay.', 'SYSTEM', 1, '2026-06-05 20:15:00'),
(21, 2, 'Xac thuc tai khoan thanh cong', 'Ho so doi tac Meo Kute Spa da duoc xac thuc va kich hoat tren he thong.', 'SYSTEM', 1, '2026-04-20 10:00:00'),
(22, 6, 'Bao tri he thong', 'He thong se bao tri tu 23:00 - 01:00 ngay 22/06. Mot so chuc nang co the bi anh huong.', 'SYSTEM', 0, '2026-06-18 12:00:00');

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
(22, 'CONFIRMED',            'IN_PROGRESS',            '2026-06-17 12:10:00', 'staff.hotel@peteye.vn');

-- ============================================================================
-- CUSTOMER MANAGEMENT DEMO DATA
-- Bổ sung để demo tính năng "Quản lý Khách Hàng" (Shop Owner mobile app)
-- Shop 1 (Meo Kute Spa) sẽ có 5 khách hàng với đủ 4 hạng tier:
--   BRONZE  → Hoang Duc Manh  (user 8,  200K spent)
--   SILVER  → Vu Cam Ly       (user 7,  800K spent)
--             Nguyen Binh Minh (user 6, 1.5M spent)
--   GOLD    → Pham Anh Thu    (user 5, 3.5M spent)
--   PLATINUM→ Ly Thi Bich Van (user 13,  15M spent)
-- ============================================================================

-- 1. Cập nhật avatar cho 4 khách hàng hiện có (dùng placeholder ảnh thật)
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=47' WHERE id = 5;  -- Pham Anh Thu
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=33' WHERE id = 6;  -- Nguyen Binh Minh
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=45' WHERE id = 7;  -- Vu Cam Ly
UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=12' WHERE id = 8;  -- Hoang Duc Manh

-- 2. Thêm khách hàng PLATINUM (tier_id=4, 15M spent) để demo filter
INSERT INTO `user` (id, email, password, full_name, phone, address, active, email_verified, tier_id, total_spending) VALUES
(13, 'vip.customer@gmail.com',
 '$2a$10$9uurETzMx/LPgDYIodiRm.65/zfb7aJK5asJc.6wC1Nn26QfzNRcO',
 'Ly Thi Bich Van', '0912399001', '99 Nguyen Hue, Q.1, TP.HCM', 1, 1, 4, 15000000);

UPDATE `user` SET avatar = 'https://i.pravatar.cc/150?img=25' WHERE id = 13;

INSERT INTO user_roles (user_id, roles_id) VALUES (13, 3);

-- 3. Pets của khách hàng PLATINUM (2 con)
INSERT INTO pet (id, owner_id, name, species, breed, gender, color, weight, dob, sterilized, health_note, is_active) VALUES
(6, 13, 'Diamond', 'DOG', 'Husky Siberia', 'MALE',   'Trang xam', 18.5, '2020-08-15', 1, 'Khoe manh, can tap the duc deu dan', 1),
(7, 13, 'Ruby',    'CAT', 'Ragdoll',       'FEMALE', 'Trang kem',  4.2, '2021-12-01', 1, 'Khoe manh, khong di ung',          1);

-- 4. Bookings COMPLETED của khách hàng PLATINUM tại shop 1 (IDs 27-30)
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, check_in, check_out,
                     status, note, payos_order_code, created_at) VALUES
(27, 13, 1, 6, 1, '2026-04-10 10:00:00', '2026-04-10 10:05:00', '2026-04-10 13:05:00',
    'COMPLETED', 'Diamond spa toan than lan 1',      202604001, '2026-04-09 19:00:00'),
(28, 13, 1, 7, 1, '2026-04-25 09:00:00', '2026-04-25 09:02:00', '2026-04-25 10:30:00',
    'COMPLETED', 'Ruby tam & say co ban',             202604002, '2026-04-24 20:00:00'),
(29, 13, 1, 6, 2, '2026-05-08 11:00:00', '2026-05-08 11:05:00', '2026-05-08 14:05:00',
    'COMPLETED', 'Diamond spa toan than + cat tia',  202605010, '2026-05-07 18:00:00'),
(30, 13, 1, 7, 1, '2026-05-30 14:00:00', '2026-05-30 14:05:00', '2026-05-30 15:10:00',
    'COMPLETED', NULL,                               202605011, '2026-05-29 16:00:00');

-- 5. Booking CONFIRMED sắp tới của khách hàng PLATINUM (ID 31)
INSERT INTO booking (id, user_id, shop_id, pet_id, staff_id,
                     appointment_datetime, status, note, payos_order_code, created_at) VALUES
(31, 13, 1, 6, 2, '2026-06-28 10:00:00',
    'CONFIRMED', 'Diamond cat tia dinh ky thang 6', 202406020, '2026-06-16 11:00:00');

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
(27, 27, 350000, 'PAYOS', 'SUCCESS', 202604001, 'TXN-VIP-001', '2026-04-09 19:05:00', 'Thanh toan Spa Toan Than'),
(28, 28, 150000, 'PAYOS', 'SUCCESS', 202604002, 'TXN-VIP-002', '2026-04-24 20:05:00', 'Thanh toan Tam & Say'),
(29, 29, 350000, 'PAYOS', 'SUCCESS', 202605010, 'TXN-VIP-003', '2026-05-07 18:05:00', 'Thanh toan Spa Toan Than'),
(30, 30, 150000, 'PAYOS', 'SUCCESS', 202605011, 'TXN-VIP-004', '2026-05-29 16:05:00', 'Thanh toan Tam & Say'),
(31, 31, 200000, 'PAYOS', 'SUCCESS', 202406020, 'TXN-VIP-005', '2026-06-16 11:05:00', 'Thanh toan Cat Tia Long');

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

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- SUMMARY
-- Users    : 13 (1 admin, 3 owners, 5 customers, 4 staff)
-- Shops    : 3  (SPA/MANUAL, CLINIC/AUTO, BOARDING/OPEN_POOL)
-- Services : 12 (4 per shop)
-- Pets     : 7  (5 gốc + 2 của VIP customer)
-- Bookings : 31
--   COMPLETED            : 9  (IDs  1-5, 27-30)
--   CANCELLED            : 4  (IDs  6-9)
--   WAITING_SHOP_APPROVAL: 4  (IDs 10-13) - Shop1 MANUAL
--   CONFIRMED            : 6  (IDs 14-18, 31)
--   IN_PROGRESS          : 4  (IDs 19-22)
--   PENDING_PAYMENT      : 4  (IDs 23-26)
-- Payments : 31 (SUCCESS / REFUNDED / PENDING)
-- Reviews  : 5  (one per completed booking gốc)
-- Customer tiers tại Shop 1 (demo quản lý KH):
--   BRONZE  : Hoang Duc Manh  (user 8)
--   SILVER  : Vu Cam Ly (7), Nguyen Binh Minh (6)
--   GOLD    : Pham Anh Thu (5)
--   PLATINUM: Ly Thi Bich Van (13)  ← mới thêm
-- All passwords: 12345678
-- ============================================================================
