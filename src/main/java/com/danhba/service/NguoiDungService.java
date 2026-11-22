package com.danhba.service;

import com.danhba.model.NguoiDung;
import com.danhba.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Xu ly nghiep vu dang nhap (UC01 - Dang nhap).
 */
public class NguoiDungService {

    /**
     * Doi chieu ten dang nhap / mat khau voi CSDL (Luong su kien chinh
     * buoc 3-4 cua UC01). Tra ve null neu sai ten dang nhap hoac mat khau.
     */
    public NguoiDung dangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT id, ten_dang_nhap, mat_khau, ho_ten FROM nguoi_dung WHERE ten_dang_nhap = ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null; // 4a. Sai ten dang nhap
                }
                String matKhauDaBam = rs.getString("mat_khau");
                if (!PasswordUtil.kiemTra(matKhau, matKhauDaBam)) {
                    return null; // 4a. Sai mat khau
                }
                return new NguoiDung(rs.getInt("id"), rs.getString("ten_dang_nhap"),
                        matKhauDaBam, rs.getString("ho_ten"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi ket noi/truy van CSDL khi dang nhap", e);
        }
    }

    /** Tao nguoi dung moi (dung khi khoi tao du lieu / dang ky). */
    public boolean dangKy(String tenDangNhap, String matKhau, String hoTen) {
        String sql = "INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, ho_ten) VALUES (?, ?, ?)";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, PasswordUtil.bam(matKhau));
            ps.setString(3, hoTen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false; // vi du: ten_dang_nhap da ton tai (UNIQUE)
        }
    }
}
