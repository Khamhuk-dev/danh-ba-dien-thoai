package com.danhba.util;

import java.util.regex.Pattern;

/**
 * Kiem tra du lieu nhap - theo dung Bang 4.8 trong bao cao (Chuong 4.4.3).
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    // Chi gom chu so, cho phep dau + o dau, do dai 9-12 ky tu (khong tinh dau +)
    private static final Pattern SDT_PATTERN =
            Pattern.compile("^\\+?\\d{9,12}$");

    private Validator() {
    }

    /** Ho ten khong rong, toi da 100 ky tu. */
    public static boolean isHoTenHopLe(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().length() <= 100;
    }

    /** Chi gom chu so (cho phep dau + o dau), do dai 9-12 ky tu. */
    public static boolean isSoDienThoaiHopLe(String s) {
        return s != null && SDT_PATTERN.matcher(s.trim()).matches();
    }

    /** Rong hoac dung dinh dang email. */
    public static boolean isEmailHopLe(String s) {
        if (s == null || s.trim().isEmpty()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(s.trim()).matches();
    }

    /** Ten nhom khong rong, toi da 100 ky tu (Bang nhom, cot ten_nhom). */
    public static boolean isTenNhomHopLe(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().length() <= 100;
    }
}
