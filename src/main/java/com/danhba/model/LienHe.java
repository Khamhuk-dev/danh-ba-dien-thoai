package com.danhba.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lop thuc the LienHe - tuong ung bang lien_he trong CSDL.
 * Thuoc tinh theo dung Bang 4.6 trong bao cao (Chuong 4).
 */
public class LienHe {

    private int id;
    private int nguoiDungId;
    private int nhomId; // 0 neu chua phan nhom
    private String hoTen;
    private String email;
    private String diaChi;
    private LocalDate ngaySinh;
    private String ghiChu;
    private boolean yeuThich;
    private List<SoDienThoai> dsSoDienThoai = new ArrayList<>();

    // Khong luu CSDL - chi de hien thi tren bang danh sach (JOIN voi nhom)
    private String tenNhom;

    public LienHe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(int nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public int getNhomId() {
        return nhomId;
    }

    public void setNhomId(int nhomId) {
        this.nhomId = nhomId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public boolean isYeuThich() {
        return yeuThich;
    }

    public void setYeuThich(boolean yeuThich) {
        this.yeuThich = yeuThich;
    }

    public List<SoDienThoai> getDsSoDienThoai() {
        return dsSoDienThoai;
    }

    public void setDsSoDienThoai(List<SoDienThoai> dsSoDienThoai) {
        this.dsSoDienThoai = dsSoDienThoai;
    }

    public String getTenNhom() {
        return tenNhom;
    }

    public void setTenNhom(String tenNhom) {
        this.tenNhom = tenNhom;
    }

    /**
     * So dien thoai chinh de hien thi tren bang danh sach (UC02).
     */
    public String getSoDienThoaiChinh() {
        for (SoDienThoai sdt : dsSoDienThoai) {
            if (sdt.isLaSoChinh()) {
                return sdt.getSo();
            }
        }
        return dsSoDienThoai.isEmpty() ? "" : dsSoDienThoai.get(0).getSo();
    }
}
