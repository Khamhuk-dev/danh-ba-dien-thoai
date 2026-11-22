package com.danhba.service;

import com.danhba.model.Nhom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Nghiep vu quan ly nhom (UC08-UC11).
 */
public class NhomService {

    /** Lay danh sach nhom cua nguoi dung, kem so luong lien he moi nhom (UC08). */
    public List<Nhom> layTatCa(int nguoiDungId) {
        String sql = "SELECT n.id, n.nguoi_dung_id, n.ten_nhom, n.mo_ta, " +
                "(SELECT COUNT(*) FROM lien_he lh WHERE lh.nhom_id = n.id) AS so_luong " +
                "FROM nhom n WHERE n.nguoi_dung_id = ? ORDER BY n.ten_nhom ASC";
        List<Nhom> ds = new ArrayList<>();
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nguoiDungId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Nhom n = new Nhom(rs.getInt("id"), rs.getInt("nguoi_dung_id"),
                            rs.getString("ten_nhom"), rs.getString("mo_ta"));
                    n.setSoLuongLienHe(rs.getInt("so_luong"));
                    ds.add(n);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach nhom", e);
        }
        return ds;
    }

    /** Kiem tra ten nhom da ton tai trong cung nguoi dung chua (UC09 buoc 4, UC10 buoc 3). */
    public boolean ganTrungTen(int nguoiDungId, String tenNhom, int boQuaId) {
        String sql = "SELECT COUNT(*) FROM nhom WHERE nguoi_dung_id = ? AND ten_nhom = ? AND id <> ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nguoiDungId);
            ps.setString(2, tenNhom);
            ps.setInt(3, boQuaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung ten nhom", e);
        }
    }

    /** Them nhom moi (UC09). */
    public boolean them(Nhom nhom) {
        String sql = "INSERT INTO nhom (nguoi_dung_id, ten_nhom, mo_ta) VALUES (?, ?, ?)";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nhom.getNguoiDungId());
            ps.setString(2, nhom.getTenNhom());
            ps.setString(3, nhom.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them nhom", e);
        }
    }

    /** Sua nhom (UC10). */
    public boolean sua(Nhom nhom) {
        String sql = "UPDATE nhom SET ten_nhom = ?, mo_ta = ? WHERE id = ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nhom.getTenNhom());
            ps.setString(2, nhom.getMoTa());
            ps.setInt(3, nhom.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi sua nhom", e);
        }
    }

    /**
     * Xoa nhom (UC11). Nho khoa ngoai lien_he.nhom_id co ON DELETE SET NULL
     * (Bang 4.3), cac lien he thuoc nhom se tu chuyen sang "chua phan nhom".
     */
    public boolean xoa(int id) {
        String sql = "DELETE FROM nhom WHERE id = ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa nhom", e);
        }
    }
}
