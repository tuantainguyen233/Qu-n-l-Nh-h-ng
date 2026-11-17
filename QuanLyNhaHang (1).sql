CREATE DATABASE QuanLyNhaHang;
GO
USE QuanLyNhaHang;
GO

/* ==========================================================================
   PHAN 1: NGUYEN BAN FILE
   ========================================================================== */

/* Xoa bang theo thu tu khoa ngoai de co the chay lai bat ky luc nao */
DROP TABLE IF EXISTS DATBAN, CHITIETHOADON, HOADON, PHANCA, NHANVIEN, BANAN, KHUVUC, MONAN, LOAIMON;
GO

/* 1.1 TAO CAC BANG CO BAN */
CREATE TABLE LOAIMON (
    MaLoai INT PRIMARY KEY,
    TenLoai NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE MONAN (
    MaMon INT PRIMARY KEY,
    TenMon NVARCHAR(100) NOT NULL UNIQUE,
    MaLoai INT NOT NULL,
    DonGia DECIMAL(12,0) NOT NULL CHECK (DonGia >= 0),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Còn món'
        CHECK (TrangThai IN (N'Còn món', N'Hết món', N'Ngừng bán')),
    FOREIGN KEY (MaLoai) REFERENCES LOAIMON(MaLoai)
);

CREATE TABLE KHUVUC (
    MaKhuVuc INT PRIMARY KEY,
    TenKhuVuc NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE BANAN (
    MaBan INT PRIMARY KEY,
    TenBan NVARCHAR(50) NOT NULL UNIQUE,
    MaKhuVuc INT NOT NULL,
    SoCho INT NOT NULL CHECK (SoCho > 0),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Trống'
        CHECK (TrangThai IN (N'Trống', N'Đang dùng', N'Đã đặt')),
    FOREIGN KEY (MaKhuVuc) REFERENCES KHUVUC(MaKhuVuc)
);

CREATE TABLE NHANVIEN (
    MaNV INT PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE CHECK (NgaySinh < GETDATE() AND YEAR(GETDATE()) - YEAR(NgaySinh) >= 16),
    GioiTinh NVARCHAR(10) NOT NULL CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    DienThoai VARCHAR(10) NOT NULL UNIQUE
        CHECK (DienThoai LIKE '0%' AND LEN(DienThoai) = 10 AND DienThoai NOT LIKE '%[^0-9]%'),
    Email VARCHAR(100) CHECK (Email LIKE '%@%'),
    ChucVu NVARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang làm'
        CHECK (TrangThai IN (N'Đang làm', N'Nghỉ việc'))
);

CREATE TABLE PHANCA (
    MaPhanCa INT IDENTITY(1,1) PRIMARY KEY,
    MaNV INT NOT NULL,
    CaLam NVARCHAR(100) NOT NULL,
    NgayLam DATE NOT NULL,
    MaKhuVuc INT NOT NULL,

    CONSTRAINT FK_PHANCA_NHANVIEN
        FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),

    CONSTRAINT FK_PHANCA_KHUVUC
        FOREIGN KEY (MaKhuVuc) REFERENCES KHUVUC(MaKhuVuc)
);

CREATE TABLE HOADON (
    MaHD INT IDENTITY(1,1) PRIMARY KEY,
    MaBan INT NOT NULL REFERENCES BANAN(MaBan),
    MaNV INT NOT NULL REFERENCES NHANVIEN(MaNV),
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    TongTien DECIMAL(12,0) NOT NULL DEFAULT 0 CHECK (TongTien >= 0),
    GiamGia DECIMAL(12,0) NOT NULL DEFAULT 0 CHECK (GiamGia >= 0),
    ThanhTien AS (TongTien - GiamGia),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chưa thanh toán'
        CHECK (TrangThai IN (N'Chưa thanh toán', N'Đã thanh toán', N'Đã hủy')),
    CHECK (GiamGia <= TongTien)
);

CREATE TABLE CHITIETHOADON (
    MaHD INT NOT NULL REFERENCES HOADON(MaHD),
    MaMon INT NOT NULL REFERENCES MONAN(MaMon),
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    DonGia DECIMAL(12,0) NOT NULL CHECK (DonGia >= 0),
    ThanhTien AS (SoLuong * DonGia),
    PRIMARY KEY (MaHD, MaMon)
);
GO

/* 1.2 INDEX */
CREATE UNIQUE INDEX UX_NHANVIEN_Email ON NHANVIEN(Email) WHERE Email IS NOT NULL;
CREATE UNIQUE INDEX UX_HOADON_ChuaTT ON HOADON(MaBan) WHERE TrangThai = N'Chưa thanh toán';
GO

/* 1.3 TRIGGER */
CREATE TRIGGER trg_TinhTongTien ON CHITIETHOADON
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    UPDATE HOADON
    SET TongTien = (SELECT ISNULL(SUM(ThanhTien), 0)
                    FROM CHITIETHOADON
                    WHERE CHITIETHOADON.MaHD = HOADON.MaHD)
    WHERE MaHD IN (SELECT MaHD FROM inserted)
       OR MaHD IN (SELECT MaHD FROM deleted);
END;
GO

CREATE TRIGGER trg_DoiTrangThaiBan ON HOADON
AFTER INSERT, UPDATE
AS
BEGIN
    IF UPDATE(TrangThai)
    BEGIN
        UPDATE BANAN SET TrangThai = N'Đang dùng'
        WHERE MaBan IN (SELECT MaBan FROM inserted WHERE TrangThai = N'Chưa thanh toán');

        UPDATE BANAN SET TrangThai = N'Trống'
        WHERE MaBan IN (SELECT MaBan FROM inserted WHERE TrangThai IN (N'Đã thanh toán', N'Đã hủy'));
    END
END;
GO

/* 1.4 DU LIEU MAU BAN DAU */
INSERT INTO LOAIMON VALUES
(1,N'Món chính'),
(2,N'Món khai vị'),
(3,N'Đồ uống'),
(4,N'Món ăn vặt'),
(5,N'Tráng miệng');

INSERT INTO MONAN VALUES
(101,N'Cơm chiên dương châu',1,45000,N'Còn món'),
(102,N'Phở bò',1,50000,N'Còn món'),
(103,N'Gỏi cuốn',2,30000,N'Còn món'),
(104,N'Trà đào',3,25000,N'Còn món'),
(105,N'Khoai tây chiên',4,35000,N'Còn món'),
(106,N'Rau câu',5,15000,N'Còn món');

INSERT INTO KHUVUC VALUES
(1,N'Tầng 1'),
(2,N'Tầng 2'),
(3,N'Ngoài trời'),
(4,N'Phòng VIP'),
(5,N'Sân vườn');
INSERT INTO KHUVUC (MaKhuVuc, TenKhuVuc) 
VALUES (6, N'Nhà bếp');

INSERT INTO BANAN VALUES
(1,N'Bàn 01',1,4,N'Trống'),
(2,N'Bàn 02',1,4,N'Trống'),
(3,N'Bàn 03',2,6,N'Trống'),
(4,N'Bàn VIP 01',4,8,N'Trống'),
(5,N'Bàn sân vườn 01',5,4,N'Trống'),
(18,N'Bàn 18',3,4,N'Đã đặt'),
(22,N'Bàn 22',4,6,N'Đã đặt');

INSERT INTO NHANVIEN VALUES
(1,N'Nguyễn Văn An','2000-03-15',N'Nam','0901234567','an@gmail.com',N'Quản lý',N'Đang làm'),
(2,N'Trần Thị Bình','2002-07-20',N'Nữ','0912345678','binh@gmail.com',N'Phục vụ',N'Đang làm'),
(3,N'Lê Văn Cường','2001-11-05',N'Nam','0923456789','cuong@gmail.com',N'Đầu bếp',N'Đang làm'),
(4,N'Phạm Thị Dung','2003-01-12',N'Nữ','0934567890','dung@gmail.com',N'Thu ngân',N'Đang làm'),
(5,N'Hoàng Minh Đức','1999-09-25',N'Nam','0945678901','duc@gmail.com',N'Phục vụ',N'Đang làm');

INSERT INTO PHANCA (MaNV, CaLam, NgayLam, MaKhuVuc)
VALUES
(1, N'Ca sáng (07:00 - 11:00)', CAST(GETDATE() AS DATE), 1),
(2, N'Ca chiều (11:00 - 16:00)', CAST(GETDATE() AS DATE), 2),
(3, N'Ca tối (16:00 - 22:00)',   CAST(GETDATE() AS DATE), 6),
(4, N'Ca tối (16:00 - 22:00)',   CAST(GETDATE() AS DATE), 3),
(5, N'Ca sáng (07:00 - 11:00)',   CAST(GETDATE() AS DATE), 4);
GO
/* 1.5 BO SUNG COT GIO VAO & GIO THANH TOAN VAO HOADON */
ALTER TABLE HOADON ADD GioVao DATETIME DEFAULT GETDATE();
ALTER TABLE HOADON ADD GioThanhToan DATETIME NULL;
GO

UPDATE HOADON SET GioVao = NgayTao WHERE GioVao IS NULL;
UPDATE HOADON SET GioThanhToan = DATEADD(MINUTE, 45, NgayTao) 
WHERE TrangThai = N'Đã thanh toán' AND GioThanhToan IS NULL;
GO

/* ==========================================================================
   PHAN 2: CAC PHAN BO SUNG TIEP THEO CUA NHOM
   ========================================================================== */

/* 2.1 BO SUNG COT CHO BANG KHUVUC */
ALTER TABLE KHUVUC ADD
    ViTri NVARCHAR(100) NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Hoạt động'
        CHECK (TrangThai IN (N'Hoạt động', N'Bảo trì')),
    GhiChu NVARCHAR(255) NULL;
GO

UPDATE KHUVUC SET ViTri = N'Tầng 1'   WHERE MaKhuVuc = 1;
UPDATE KHUVUC SET ViTri = N'Tầng 2'   WHERE MaKhuVuc = 2;
UPDATE KHUVUC SET ViTri = N'Sân trước' WHERE MaKhuVuc = 3;
UPDATE KHUVUC SET ViTri = N'Tầng 3'   WHERE MaKhuVuc = 4;
UPDATE KHUVUC SET ViTri = N'Sân sau', TrangThai = N'Bảo trì' WHERE MaKhuVuc = 5;
GO

/* 2.2 TAO BANG DATBAN VA DU LIEU MAU */
CREATE TABLE DATBAN (
    MaDat VARCHAR(10) PRIMARY KEY,
    TenKhach NVARCHAR(100) NOT NULL,
    SDT VARCHAR(10) NOT NULL
        CHECK (SDT LIKE '0%' AND LEN(SDT) = 10 AND SDT NOT LIKE '%[^0-9]%'),
    MaBan INT NOT NULL REFERENCES BANAN(MaBan),
    GioHen VARCHAR(5) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đã đặt'
        CHECK (TrangThai IN (N'Đã đặt', N'Đã nhận bàn', N'Đã hủy'))
);

CREATE UNIQUE INDEX UX_DATBAN_Ban_Gio ON DATBAN(MaBan, GioHen)
WHERE TrangThai <> N'Đã hủy';
GO

INSERT INTO DATBAN VALUES
('DB01', N'Trần Minh Hoàng', '0901234567', 18, '18:30', N'Đã đặt'),
('DB02', N'Lê Thu Hà',       '0912345678', 3,  '19:00', N'Đã nhận bàn'),
('DB03', N'Phạm Quốc Khánh', '0987654321', 22, '20:00', N'Đã đặt');
GO

/* 2.3 BO SUNG THONG TIN LOAIMON */
ALTER TABLE LOAIMON
ADD
    DonViTinh NVARCHAR(30) NULL,
    TrangThai NVARCHAR(20) NOT NULL
        CONSTRAINT DF_LOAIMON_TrangThai
        DEFAULT N'Đang kinh doanh',
    CONSTRAINT CK_LOAIMON_TrangThai
        CHECK (TrangThai IN (N'Đang kinh doanh', N'Ngừng kinh doanh'));
GO

UPDATE LOAIMON
SET DonViTinh = CASE MaLoai
    WHEN 1 THEN N'Phần / Tô'
    WHEN 2 THEN N'Đĩa'
    WHEN 3 THEN N'Ly / Lon'
    WHEN 4 THEN N'Đĩa'
    WHEN 5 THEN N'Phần'
END;
GO

/* 2.4 BO SUNG SO LUONG TON DU KIEN CHO MONAN */
ALTER TABLE MONAN
ADD SoLuongTonDuKien INT NULL
    CHECK (SoLuongTonDuKien >= 0);
GO

UPDATE MONAN
SET SoLuongTonDuKien = CASE MaMon
        WHEN 101 THEN 20
        WHEN 102 THEN 15
        WHEN 103 THEN 12
        WHEN 104 THEN 10
        WHEN 105 THEN 0
        WHEN 106 THEN 8
    END,
    TrangThai = CASE MaMon
        WHEN 105 THEN N'Hết món'
        ELSE N'Còn món'
    END
WHERE MaMon IN (101, 102, 103, 104, 105, 106);
GO

/* ==========================================================================
PHAN 3: KIEM TRA DU LIEU TOAN BO HE THONG
   ========================================================================== */
SELECT * FROM LOAIMON;
SELECT * FROM MONAN;
SELECT * FROM KHUVUC;
SELECT * FROM BANAN;
SELECT * FROM NHANVIEN;
SELECT * FROM PHANCA;
SELECT * FROM DATBAN;
SELECT * FROM HOADON;
SELECT * FROM CHITIETHOADON;
GO