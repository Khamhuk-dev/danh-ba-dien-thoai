package com.danhba.service;

import com.danhba.model.LienHe;
import com.danhba.model.Nhom;
import com.danhba.model.SoDienThoai;
import com.danhba.util.Validator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Doc / ghi file CSV (goi service - Bang 4.5). UC12 - Nhap danh ba tu CSV,
 * UC13 - Xuat danh ba ra CSV.
 *
 * Dinh dang cot CSV: ho_ten,so_dien_thoai,email,dia_chi,ten_nhom
 */
public class CsvService {

    private static final String[] TIEU_DE = {"ho_ten", "so_dien_thoai", "email", "dia_chi", "ten_nhom"};
    private final LienHeService lienHeService = new LienHeService();
    private final NhomService nhomService = new NhomService();

    /** Ket qua nhap CSV: dung de hien thi bao cao (UC12 buoc 4). */
    public static class KetQuaNhap {
        public int soDongThanhCong = 0;
        public final List<String> danhSachLoi = new ArrayList<>();
    }

    /**
     * Nhap danh ba tu file CSV (UTF-8). Kiem tra tung dong, dong khong hop le
     * se bi bo qua va liet ke trong bao cao (2b).
     */
    public KetQuaNhap nhap(File file, int nguoiDungId) {
        KetQuaNhap ketQua = new KetQuaNhap();
        // nhom hien co cua nguoi dung, dung de doi chieu ten_nhom -> id
        Map<String, Nhom> nhomTheoTen = nhomService.layTatCa(nguoiDungId).stream()
                .collect(Collectors.toMap(n -> n.getTenNhom().toLowerCase(), n -> n, (a, b) -> a));

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dongDauTien = br.readLine();
            if (dongDauTien == null) {
                ketQua.danhSachLoi.add("File rỗng.");
                return ketQua;
            }
            String[] cotTieuDe = tachDong(dongDauTien);
            if (cotTieuDe.length < 2) {
                ketQua.danhSachLoi.add("File không đúng cấu trúc cột (cần: " + String.join(",", TIEU_DE) + ").");
                return ketQua; // 2a. Sai cau truc cot
            }

            String dong;
            int soDong = 1;
            while ((dong = br.readLine()) != null) {
                soDong++;
                if (dong.trim().isEmpty()) {
                    continue;
                }
                String[] cot = tachDong(dong);
                String hoTen = layCot(cot, 0);
                String soDienThoai = layCot(cot, 1);
                String email = layCot(cot, 2);
                String diaChi = layCot(cot, 3);
                String tenNhom = layCot(cot, 4);

                if (!Validator.isHoTenHopLe(hoTen)) {
                    ketQua.danhSachLoi.add("Dòng " + soDong + ": thiếu hoặc sai họ tên.");
                    continue;
                }
                if (!Validator.isSoDienThoaiHopLe(soDienThoai)) {
                    ketQua.danhSachLoi.add("Dòng " + soDong + ": số điện thoại không hợp lệ.");
                    continue;
                }
                if (!Validator.isEmailHopLe(email)) {
                    ketQua.danhSachLoi.add("Dòng " + soDong + ": email không hợp lệ.");
                    continue;
                }

                LienHe lh = new LienHe();
                lh.setNguoiDungId(nguoiDungId);
                lh.setHoTen(hoTen);
                lh.setEmail(email);
                lh.setDiaChi(diaChi);
                if (tenNhom != null && !tenNhom.isEmpty()) {
                    Nhom n = nhomTheoTen.get(tenNhom.toLowerCase());
                    if (n != null) {
                        lh.setNhomId(n.getId());
                    }
                }
                SoDienThoai sdt = new SoDienThoai(soDienThoai, SoDienThoai.DI_DONG, true);
                lh.getDsSoDienThoai().add(sdt);

                try {
                    lienHeService.them(lh);
                    ketQua.soDongThanhCong++;
                } catch (RuntimeException e) {
                    ketQua.danhSachLoi.add("Dòng " + soDong + ": lỗi khi lưu vào cơ sở dữ liệu.");
                }
            }
        } catch (IOException e) {
            ketQua.danhSachLoi.add("Không đọc được file: " + e.getMessage());
        }
        return ketQua;
    }

    /** Xuat danh sach lien he ra file CSV (UTF-8) (UC13). */
    public void xuat(File file, List<LienHe> danhSach) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(String.join(",", TIEU_DE));
            bw.newLine();
            for (LienHe lh : danhSach) {
                bw.write(dongCsv(
                        lh.getHoTen(),
                        lh.getSoDienThoaiChinh(),
                        lh.getEmail(),
                        lh.getDiaChi(),
                        lh.getTenNhom() == null ? "" : lh.getTenNhom()
                ));
                bw.newLine();
            }
        }
    }

    private String dongCsv(String... cacCot) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cacCot.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(thoat(cacCot[i] == null ? "" : cacCot[i]));
        }
        return sb.toString();
    }

    private String thoat(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /** Tach mot dong CSV don gian, co ho tro dau nhay kep bao quanh gia tri co dau phay. */
    private String[] tachDong(String dong) {
        List<String> cot = new ArrayList<>();
        StringBuilder hienTai = new StringBuilder();
        boolean trongChuoi = false;
        for (int i = 0; i < dong.length(); i++) {
            char c = dong.charAt(i);
            if (c == '"') {
                trongChuoi = !trongChuoi;
            } else if (c == ',' && !trongChuoi) {
                cot.add(hienTai.toString());
                hienTai.setLength(0);
            } else {
                hienTai.append(c);
            }
        }
        cot.add(hienTai.toString());
        return cot.toArray(new String[0]);
    }

    private String layCot(String[] cot, int idx) {
        return idx < cot.length ? cot[idx].trim() : "";
    }
}
