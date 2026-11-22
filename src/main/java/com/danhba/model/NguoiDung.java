package com.danhba.model;

/**
 * Lop thuc the NguoiDung - tuong ung bang nguoi_dung trong CSDL.
 */
public class NguoiDung {

    private int id;
    private String tenDangNhap;
    private String matKhau; // mat khau da bam (SHA-256)
    private String hoTen;

    public NguoiDung() {
    }

    public NguoiDung(int id, String tenDangNhap, String matKhau, String hoTen) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
}
