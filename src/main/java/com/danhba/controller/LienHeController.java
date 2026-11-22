package com.danhba.controller;

import com.danhba.model.LienHe;
import com.danhba.model.Nhom;
import com.danhba.model.NguoiDung;
import com.danhba.service.CsvService;
import com.danhba.service.LienHeService;
import com.danhba.service.NhomService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller cho man hinh chinh (Bang 4.9): thanh ben nhom, tim kiem,
 * them/sua/xoa lien he, yeu thich, nhap/xuat CSV, mo thong ke.
 */
public class LienHeController {

    @FXML private ListView<Nhom> lvNhom;
    @FXML private TextField txtTimKiem;
    @FXML private TableView<LienHe> tvLienHe;
    @FXML private TableColumn<LienHe, Boolean> colYeuThich;
    @FXML private TableColumn<LienHe, String> colHoTen;
    @FXML private TableColumn<LienHe, String> colSoDienThoai;
    @FXML private TableColumn<LienHe, String> colEmail;
    @FXML private TableColumn<LienHe, String> colNhom;
    @FXML private Label lblTrangThai;

    private final LienHeService lienHeService = new LienHeService();
    private final NhomService nhomService = new NhomService();
    private final CsvService csvService = new CsvService();

    private NguoiDung nguoiDungHienTai;
    private final ObservableList<LienHe> dsLienHe = FXCollections.observableArrayList();
    private static final Nhom NHOM_TAT_CA = new Nhom(0, 0, "Tất cả", "");

    /** Goi tu DangNhapController sau khi dang nhap thanh cong. */
    public void khoiTao(NguoiDung nguoiDung) {
        this.nguoiDungHienTai = nguoiDung;
        caiDatBang();
        taiDanhSachNhom();
        taiDanhSachLienHe();
    }

    private void caiDatBang() {
        colYeuThich.setCellValueFactory(new PropertyValueFactory<>("yeuThich"));
        colYeuThich.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean yeuThich, boolean rong) {
                super.updateItem(yeuThich, rong);
                setText(rong || yeuThich == null ? null : (yeuThich ? "★" : ""));
            }
        });
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSoDienThoai.setCellValueFactory(du -> new javafx.beans.property.SimpleStringProperty(
                du.getValue().getSoDienThoaiChinh()));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNhom.setCellValueFactory(du -> new javafx.beans.property.SimpleStringProperty(
                du.getValue().getTenNhom() == null ? "" : du.getValue().getTenNhom()));
        tvLienHe.setItems(dsLienHe);
    }

    private void taiDanhSachNhom() {
        ObservableList<Nhom> ds = FXCollections.observableArrayList();
        ds.add(NHOM_TAT_CA);
        ds.addAll(nhomService.layTatCa(nguoiDungHienTai.getId()));
        lvNhom.setItems(ds);
        lvNhom.getSelectionModel().select(NHOM_TAT_CA);
    }

    private void taiDanhSachLienHe() {
        List<LienHe> ds = lienHeService.layTatCa(nguoiDungHienTai.getId());
        dsLienHe.setAll(ds);
        capNhatTrangThai(ds.isEmpty() ? "Danh bạ trống." : "Tổng cộng " + ds.size() + " liên hệ.");
    }

    private void capNhatTrangThai(String s) {
        lblTrangThai.setText(s);
    }

    private int nhomDangChon() {
        Nhom n = lvNhom.getSelectionModel().getSelectedItem();
        return (n == null || n.getId() == 0) ? 0 : n.getId();
    }

    // ---------- UC08: Xem danh sach nhom (chon de loc) ----------
    @FXML
    private void xuLyChonNhom() {
        xuLyTimKiem();
    }

    // ---------- UC06: Tim kiem lien he ----------
    @FXML
    private void xuLyTimKiem() {
        String tuKhoa = txtTimKiem.getText();
        int nhomId = nhomDangChon();
        if ((tuKhoa == null || tuKhoa.trim().isEmpty()) && nhomId == 0) {
            taiDanhSachLienHe();
            return;
        }
        List<LienHe> ketQua = lienHeService.timKiem(nguoiDungHienTai.getId(), tuKhoa, nhomId);
        dsLienHe.setAll(ketQua);
        capNhatTrangThai(ketQua.isEmpty() ? "Không tìm thấy liên hệ." : "Tìm thấy " + ketQua.size() + " liên hệ.");
    }

    @FXML
    private void xuLyLamMoi() {
        txtTimKiem.clear();
        lvNhom.getSelectionModel().select(NHOM_TAT_CA);
        taiDanhSachLienHe();
    }

    // ---------- UC03: Them lien he ----------
    @FXML
    private void xuLyThem() {
        moFormLienHe(null);
    }

    // ---------- UC04: Sua lien he ----------
    @FXML
    private void xuLySua() {
        LienHe chon = tvLienHe.getSelectionModel().getSelectedItem();
        if (chon == null) {
            hienCanhBao("Vui lòng chọn một liên hệ cần sửa."); // 1a
            return;
        }
        moFormLienHe(chon);
    }

    private void moFormLienHe(LienHe lienHeDeSua) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/form_lien_he.fxml"));
            Parent root = loader.load();
            FormLienHeController controller = loader.getController();
            controller.khoiTao(nguoiDungHienTai, lienHeDeSua);

            Stage dialog = new Stage();
            dialog.setTitle(lienHeDeSua == null ? "Thêm liên hệ" : "Sửa liên hệ");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            if (controller.daLuu()) {
                taiDanhSachLienHe();
            }
        } catch (IOException e) {
            hienCanhBao("Không mở được biểu mẫu liên hệ: " + e.getMessage());
        }
    }

    // ---------- UC05: Xoa lien he ----------
    @FXML
    private void xuLyXoa() {
        LienHe chon = tvLienHe.getSelectionModel().getSelectedItem();
        if (chon == null) {
            hienCanhBao("Vui lòng chọn một liên hệ cần xóa."); // 1a
            return;
        }
        Alert xacNhan = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa liên hệ \"" + chon.getHoTen() + "\" không?",
                ButtonType.YES, ButtonType.NO);
        xacNhan.setTitle("Xác nhận xóa");
        xacNhan.setHeaderText(null);
        Optional<ButtonType> ketQua = xacNhan.showAndWait();
        if (ketQua.isPresent() && ketQua.get() == ButtonType.YES) {
            lienHeService.xoa(chon.getId());
            taiDanhSachLienHe();
            capNhatTrangThai("Đã xóa liên hệ \"" + chon.getHoTen() + "\".");
        }
    }

    // ---------- UC07: Danh dau yeu thich ----------
    @FXML
    private void xuLyDanhDauYeuThich() {
        LienHe chon = tvLienHe.getSelectionModel().getSelectedItem();
        if (chon == null) {
            hienCanhBao("Vui lòng chọn một liên hệ."); // 1a
            return;
        }
        boolean moi = !chon.isYeuThich();
        lienHeService.datYeuThich(chon.getId(), moi);
        chon.setYeuThich(moi);
        tvLienHe.refresh();
    }

    // ---------- UC12: Nhap danh ba tu CSV ----------
    @FXML
    private void xuLyNhapCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn file CSV để nhập");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tệp CSV", "*.csv"));
        File file = fc.showOpenDialog(tvLienHe.getScene().getWindow());
        if (file == null) {
            return;
        }
        CsvService.KetQuaNhap ketQua = csvService.nhap(file, nguoiDungHienTai.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("Nhập thành công: ").append(ketQua.soDongThanhCong).append(" dòng.\n");
        sb.append("Số dòng lỗi: ").append(ketQua.danhSachLoi.size());
        if (!ketQua.danhSachLoi.isEmpty()) {
            sb.append("\n\nChi tiết lỗi:\n");
            int gioiHan = Math.min(ketQua.danhSachLoi.size(), 20);
            for (int i = 0; i < gioiHan; i++) {
                sb.append("- ").append(ketQua.danhSachLoi.get(i)).append('\n');
            }
        }
        Alert thongBao = new Alert(Alert.AlertType.INFORMATION, sb.toString());
        thongBao.setTitle("Kết quả nhập CSV");
        thongBao.setHeaderText(null);
        thongBao.showAndWait();
        taiDanhSachLienHe();
    }

    // ---------- UC13: Xuat danh ba ra CSV ----------
    @FXML
    private void xuLyXuatCsv() {
        if (dsLienHe.isEmpty()) {
            hienCanhBao("Danh bạ trống, không có dữ liệu để xuất."); // 1a
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn nơi lưu file CSV");
        fc.setInitialFileName("danh_ba.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tệp CSV", "*.csv"));
        File file = fc.showSaveDialog(tvLienHe.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            csvService.xuat(file, dsLienHe);
            capNhatTrangThai("Xuất CSV thành công: " + file.getName());
        } catch (IOException e) {
            hienCanhBao("Không ghi được file (không có quyền hoặc file đang mở)."); // 3a
        }
    }

    // ---------- Mo man hinh quan ly nhom (UC08-UC11) ----------
    @FXML
    private void xuLyMoQuanLyNhom() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nhom.fxml"));
            Parent root = loader.load();
            NhomController controller = loader.getController();
            controller.khoiTao(nguoiDungHienTai);

            Stage dialog = new Stage();
            dialog.setTitle("Quản lý nhóm");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            taiDanhSachNhom();
            taiDanhSachLienHe();
        } catch (IOException e) {
            hienCanhBao("Không mở được màn hình quản lý nhóm: " + e.getMessage());
        }
    }

    // ---------- Mo man hinh thong ke (UC14) ----------
    @FXML
    private void xuLyMoThongKe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/thong_ke.fxml"));
            Parent root = loader.load();
            ThongKeController controller = loader.getController();
            controller.khoiTao(nguoiDungHienTai);

            Stage dialog = new Stage();
            dialog.setTitle("Thống kê");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            hienCanhBao("Không mở được màn hình thống kê: " + e.getMessage());
        }
    }

    private void hienCanhBao(String noiDung) {
        Alert canhBao = new Alert(Alert.AlertType.WARNING, noiDung, ButtonType.OK);
        canhBao.setTitle("Thông báo");
        canhBao.setHeaderText(null);
        canhBao.showAndWait();
    }
}
