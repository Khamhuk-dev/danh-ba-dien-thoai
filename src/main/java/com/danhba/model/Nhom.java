package com.danhba.model;

/**
 * Lop thuc the Nhom - tuong ung bang nhom trong CSDL.
 */
public class Nhom {

    private int id;
    private int nguoiDungId;
    private String tenNhom;
    private String moTa;
    private int soLuongLienHe; // chi dung de hien thi (khong luu CSDL)

    public Nhom() {
    }

    public Nhom(int id, int nguoiDungId, String tenNhom, String moTa) {
        this.id = id;
        this.nguoiDungId = nguoiDungId;
        this.tenNhom = tenNhom;
        this.moTa = moTa;
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

    public String getTenNhom() {
        return tenNhom;
    }

    public void setTenNhom(String tenNhom) {
        this.tenNhom = tenNhom;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSoLuongLienHe() {
        return soLuongLienHe;
    }

    public void setSoLuongLienHe(int soLuongLienHe) {
        this.soLuongLienHe = soLuongLienHe;
    }

    @Override
    public String toString() {
        // Dung de hien thi truc tiep trong ComboBox / ListView
        return tenNhom;
    }
}
