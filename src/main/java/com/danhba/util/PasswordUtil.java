package com.danhba.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Bam mat khau truoc khi luu vao CSDL (yeu cau phi chuc nang - Chuong 2.4
 * va bien phap giam thieu rui ro lo mat khau - Bang 1.5).
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * Bam mat khau bang SHA-256, tra ve chuoi hex 64 ky tu.
     * (cot mat_khau VARCHAR(255) trong bang nguoi_dung du cho ca cac
     * thuat toan bam dai hon neu sau nay nang cap len bcrypt/argon2).
     */
    public static String bam(String matKhauGoc) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(matKhauGoc.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Khong the bam mat khau", e);
        }
    }

    public static boolean kiemTra(String matKhauGoc, String matKhauDaBam) {
        return bam(matKhauGoc).equals(matKhauDaBam);
    }
}
