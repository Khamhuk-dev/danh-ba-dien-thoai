-- =====================================================================
--  Co so du lieu: danh_ba (Chuong 4.2 - Thiet ke co so du lieu)
--  MySQL 8.x, bo ky tu utf8mb4
-- =====================================================================

DROP DATABASE IF EXISTS danh_ba;
CREATE DATABASE danh_ba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE danh_ba;

-- ---------------------------------------------------------------------
-- Bang 4.1. Bang nguoi_dung
-- ---------------------------------------------------------------------
CREATE TABLE nguoi_dung (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    ten_dang_nhap  VARCHAR(50)  NOT NULL UNIQUE,
    mat_khau       VARCHAR(255) NOT NULL,     -- mat khau da bam (SHA-256)
    ho_ten         VARCHAR(100)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Bang 4.2. Bang nhom
-- ---------------------------------------------------------------------
CREATE TABLE nhom (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id  INT NOT NULL,
    ten_nhom       VARCHAR(100) NOT NULL,
    mo_ta          VARCHAR(255),
    CONSTRAINT fk_nhom_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    CONSTRAINT uq_nhom_ten UNIQUE (nguoi_dung_id, ten_nhom)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Bang 4.3. Bang lien_he
-- ---------------------------------------------------------------------
CREATE TABLE lien_he (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id  INT NOT NULL,
    nhom_id        INT NULL,
    ho_ten         VARCHAR(100) NOT NULL,
    email          VARCHAR(100),
    dia_chi        VARCHAR(255),
    ngay_sinh      DATE,
    ghi_chu        TEXT,
    yeu_thich      BOOLEAN DEFAULT 0,
    ngay_tao       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lienhe_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    CONSTRAINT fk_lienhe_nhom
        FOREIGN KEY (nhom_id) REFERENCES nhom(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Bang 4.4. Bang so_dien_thoai
-- ---------------------------------------------------------------------
CREATE TABLE so_dien_thoai (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    lien_he_id     INT NOT NULL,
    so             VARCHAR(20) NOT NULL,
    loai           VARCHAR(20) DEFAULT 'Di động',
    la_so_chinh    BOOLEAN DEFAULT 0,
    CONSTRAINT fk_sdt_lienhe
        FOREIGN KEY (lien_he_id) REFERENCES lien_he(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Du lieu mau de chay thu chuong trinh (Chuong 5 - xay dung chuong trinh
-- minh hoa). Tai khoan: admin / admin123
-- ---------------------------------------------------------------------
INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, ho_ten) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Quản trị viên');

INSERT INTO nhom (nguoi_dung_id, ten_nhom, mo_ta) VALUES
(1, 'Gia đình', 'Người thân trong gia đình'),
(1, 'Bạn bè', 'Bạn bè, đồng nghiệp');

INSERT INTO lien_he (nguoi_dung_id, nhom_id, ho_ten, email, dia_chi, ngay_sinh, yeu_thich) VALUES
(1, 1, 'Nguyễn Văn A', 'a.nguyen@example.com', 'Hà Nội', '1990-05-12', 1),
(1, 2, 'Trần Thị B', 'b.tran@example.com', 'TP. Hồ Chí Minh', '1995-09-01', 0);

INSERT INTO so_dien_thoai (lien_he_id, so, loai, la_so_chinh) VALUES
(1, '0901234567', 'Di động', 1),
(2, '0912345678', 'Di động', 1);
