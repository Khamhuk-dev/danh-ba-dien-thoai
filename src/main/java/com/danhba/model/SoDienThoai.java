package com.danhba.model;

/**
 * Lop thuc the SoDienThoai - tuong ung bang so_dien_thoai trong CSDL.
 * Mot LienHe co nhieu SoDienThoai.
 */
public class SoDienThoai {

    public static final String DI_DONG = "Di động";
    public static final String NHA_RIENG = "Nhà riêng";
    public static final String CONG_TY = "Công ty";
    public static final String KHAC = "Khác";

    private int id;
    private int lienHeId;
    private String so;
    private String loai = DI_DONG;
    private boolean laSoChinh;

    public SoDienThoai() {
    }

    public SoDienThoai(String so, String loai, boolean laSoChinh) {
        this.so = so;
        this.loai = loai;
        this.laSoChinh = laSoChinh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLienHeId() {
        return lienHeId;
    }

    public void setLienHeId(int lienHeId) {
        this.lienHeId = lienHeId;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public boolean isLaSoChinh() {
        return laSoChinh;
    }

    public void setLaSoChinh(boolean laSoChinh) {
        this.laSoChinh = laSoChinh;
    }

    @Override
    public String toString() {
        return so + " (" + loai + ")";
    }
}
