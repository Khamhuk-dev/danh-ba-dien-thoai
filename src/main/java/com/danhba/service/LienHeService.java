package com.danhba.service;

import com.danhba.model.LienHe;
import com.danhba.model.SoDienThoai;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Nghiep vu quan ly lien he (UC02-UC07) - phuong thuc theo dung Bang 4.7.
 */
public class LienHeService {

    private static final String SELECT_CO_BAN =
            "SELECT lh.id, lh.nguoi_dung_id, lh.nhom_id, lh.ho_ten, lh.email, lh.dia_chi, " +
            "lh.ngay_sinh, lh.ghi_chu, lh.yeu_thich, n.ten_nhom " +
            "FROM lien_he lh LEFT JOIN nhom n ON lh.nhom_id = n.id ";

    /** Lay toan bo lien he cua nguoi dung, sap xep theo ho ten (UC02). */
    public List<LienHe> layTatCa(int nguoiDungId) {
        String sql = SELECT_CO_BAN + "WHERE lh.nguoi_dung_id = ? ORDER BY lh.ho_ten ASC";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nguoiDungId);
            try (ResultSet rs = ps.executeQuery()) {
                List<LienHe> ds = docDanhSach(cn, rs);
                return ds;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach lien he", e);
        }
    }

    /** Tim theo ho ten, so dien thoai, email va nhom (UC06). nhomId = 0 nghia la khong loc theo nhom. */
    public List<LienHe> timKiem(int nguoiDungId, String tuKhoa, int nhomId) {
        StringBuilder sql = new StringBuilder(SELECT_CO_BAN);
        sql.append("WHERE lh.nguoi_dung_id = ? AND (LOWER(lh.ho_ten) LIKE ? OR LOWER(lh.email) LIKE ? ")
           .append("OR lh.id IN (SELECT lien_he_id FROM so_dien_thoai WHERE so LIKE ?)) ");
        if (nhomId > 0) {
            sql.append("AND lh.nhom_id = ? ");
        }
        sql.append("ORDER BY lh.ho_ten ASC");

        String tk = "%" + (tuKhoa == null ? "" : tuKhoa.trim().toLowerCase()) + "%";
        String tkSdt = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";

        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, nguoiDungId);
            ps.setString(idx++, tk);
            ps.setString(idx++, tk);
            ps.setString(idx++, tkSdt);
            if (nhomId > 0) {
                ps.setInt(idx++, nhomId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return docDanhSach(cn, rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem lien he", e);
        }
    }

    /** Kiem tra so dien thoai da ton tai o lien he khac chua (UC03 5b). */
    public boolean soDaTonTai(String so) {
        String sql = "SELECT COUNT(*) FROM so_dien_thoai WHERE so = ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, so);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung so dien thoai", e);
        }
    }

    /** Them lien he va cac so dien thoai kem theo (UC03). */
    public boolean them(LienHe lh) {
        String sqlLh = "INSERT INTO lien_he (nguoi_dung_id, nhom_id, ho_ten, email, dia_chi, ngay_sinh, ghi_chu, yeu_thich) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = MysqlConnector.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sqlLh, Statement.RETURN_GENERATED_KEYS)) {
                ganThamSoLienHe(ps, lh);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        lh.setId(keys.getInt(1));
                    }
                }
            }
            themSoDienThoai(cn, lh.getId(), lh.getDsSoDienThoai());
            cn.commit();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them lien he", e);
        }
    }

    /** Cap nhat lien he va lam moi lai toan bo so dien thoai (UC04). */
    public boolean sua(LienHe lh) {
        String sqlLh = "UPDATE lien_he SET nhom_id = ?, ho_ten = ?, email = ?, dia_chi = ?, " +
                "ngay_sinh = ?, ghi_chu = ?, yeu_thich = ? WHERE id = ?";
        try (Connection cn = MysqlConnector.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sqlLh)) {
                if (lh.getNhomId() > 0) {
                    ps.setInt(1, lh.getNhomId());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setString(2, lh.getHoTen());
                ps.setString(3, lh.getEmail());
                ps.setString(4, lh.getDiaChi());
                ps.setObject(5, lh.getNgaySinh());
                ps.setString(6, lh.getGhiChu());
                ps.setBoolean(7, lh.isYeuThich());
                ps.setInt(8, lh.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM so_dien_thoai WHERE lien_he_id = ?")) {
                del.setInt(1, lh.getId());
                del.executeUpdate();
            }
            themSoDienThoai(cn, lh.getId(), lh.getDsSoDienThoai());
            cn.commit();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Loi sua lien he", e);
        }
    }

    /** Xoa lien he (so dien thoai bi xoa theo nho ON DELETE CASCADE - Bang 4.4) (UC05). */
    public boolean xoa(int id) {
        String sql = "DELETE FROM lien_he WHERE id = ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa lien he", e);
        }
    }

    /** Doi trang thai yeu thich (UC07). */
    public boolean datYeuThich(int id, boolean yeuThich) {
        String sql = "UPDATE lien_he SET yeu_thich = ? WHERE id = ?";
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, yeuThich);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat yeu thich", e);
        }
    }

    // ---------- Phuong thuc ho tro (Chuong 5.2 - thong ke) ----------

    public int demTongSo(int nguoiDungId) {
        return demTheoDieuKien("nguoi_dung_id = ?", nguoiDungId);
    }

    public int demYeuThich(int nguoiDungId) {
        String sql = "SELECT COUNT(*) FROM lien_he WHERE nguoi_dung_id = ? AND yeu_thich = 1";
        return demBangSql(sql, nguoiDungId);
    }

    private int demTheoDieuKien(String dieuKien, int nguoiDungId) {
        return demBangSql("SELECT COUNT(*) FROM lien_he WHERE " + dieuKien, nguoiDungId);
    }

    private int demBangSql(String sql, int nguoiDungId) {
        try (Connection cn = MysqlConnector.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nguoiDungId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi dem lien he", e);
        }
    }

    // ---------- Phuong thuc noi bo ----------

    private void ganThamSoLienHe(PreparedStatement ps, LienHe lh) throws SQLException {
        ps.setInt(1, lh.getNguoiDungId());
        if (lh.getNhomId() > 0) {
            ps.setInt(2, lh.getNhomId());
        } else {
            ps.setNull(2, Types.INTEGER);
        }
        ps.setString(3, lh.getHoTen());
        ps.setString(4, lh.getEmail());
        ps.setString(5, lh.getDiaChi());
        ps.setObject(6, lh.getNgaySinh());
        ps.setString(7, lh.getGhiChu());
        ps.setBoolean(8, lh.isYeuThich());
    }

    private void themSoDienThoai(Connection cn, int lienHeId, List<SoDienThoai> dsSdt) throws SQLException {
        String sql = "INSERT INTO so_dien_thoai (lien_he_id, so, loai, la_so_chinh) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (SoDienThoai sdt : dsSdt) {
                ps.setInt(1, lienHeId);
                ps.setString(2, sdt.getSo());
                ps.setString(3, sdt.getLoai());
                ps.setBoolean(4, sdt.isLaSoChinh());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<LienHe> docDanhSach(Connection cn, ResultSet rs) throws SQLException {
        List<LienHe> ds = new ArrayList<>();
        while (rs.next()) {
            LienHe lh = new LienHe();
            lh.setId(rs.getInt("id"));
            lh.setNguoiDungId(rs.getInt("nguoi_dung_id"));
            lh.setNhomId(rs.getInt("nhom_id")); // tra ve 0 neu NULL
            lh.setHoTen(rs.getString("ho_ten"));
            lh.setEmail(rs.getString("email"));
            lh.setDiaChi(rs.getString("dia_chi"));
            Date ngaySinh = rs.getDate("ngay_sinh");
            lh.setNgaySinh(ngaySinh == null ? null : ngaySinh.toLocalDate());
            lh.setGhiChu(rs.getString("ghi_chu"));
            lh.setYeuThich(rs.getBoolean("yeu_thich"));
            lh.setTenNhom(rs.getString("ten_nhom"));
            lh.setDsSoDienThoai(laySoDienThoai(cn, lh.getId()));
            ds.add(lh);
        }
        return ds;
    }

    private List<SoDienThoai> laySoDienThoai(Connection cn, int lienHeId) throws SQLException {
        String sql = "SELECT id, lien_he_id, so, loai, la_so_chinh FROM so_dien_thoai " +
                "WHERE lien_he_id = ? ORDER BY la_so_chinh DESC, id ASC";
        List<SoDienThoai> ds = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, lienHeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SoDienThoai sdt = new SoDienThoai();
                    sdt.setId(rs.getInt("id"));
                    sdt.setLienHeId(rs.getInt("lien_he_id"));
                    sdt.setSo(rs.getString("so"));
                    sdt.setLoai(rs.getString("loai"));
                    sdt.setLaSoChinh(rs.getBoolean("la_so_chinh"));
                    ds.add(sdt);
                }
            }
        }
        return ds;
    }
}
