package com.danhba.controller;

import com.danhba.model.Nhom;
import com.danhba.model.NguoiDung;
import com.danhba.service.NhomService;
import com.danhba.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Controller man hinh quan ly nhom (UC08 - Xem danh sach nhom,
 * UC09 - Them nhom, UC10 - Sua nhom, UC11 - Xoa nhom).
 */
public class NhomController {

    @FXML private TableView<Nhom> tvNhom;
    @FXML private TableColumn<Nhom, String> colTenNhom;
    @FXML private TableColumn<Nhom, String> colMoTa;
    @FXML private TableColumn<Nhom, Integer> colSoLuong;
    @FXML private TextField txtTenNhom;
    @FXML private TextField txtMoTa;
    @FXML private Label lblLoi;

    private final NhomService nhomService = new NhomService();
    private final ObservableList<Nhom> dsNhom = FXCollections.observableArrayList();
    private NguoiDung nguoiDungHienTai;

    @FXML
    private void initialize() {
        colTenNhom.setCellValueFactory(new PropertyValueFactory<>("tenNhom"));
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuongLienHe"));
        tvNhom.setItems(dsNhom);
        tvNhom.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                txtTenNhom.setText(moi.getTenNhom());
                txtMoTa.setText(moi.getMoTa());
            }
        });
    }

    public void khoiTao(NguoiDung nguoiDung) {
        this.nguoiDungHienTai = nguoiDung;
        taiLai();
    }

    private void taiLai() {
        dsNhom.setAll(nhomService.layTatCa(nguoiDungHienTai.getId()));
    }

    // ---------- UC09: Them nhom ----------
    @FXML
    private void xuLyThem() {
        String ten = layTenNhomNhap();
        if (ten == null) {
            return;
        }
        Nhom n = new Nhom();
        n.setNguoiDungId(nguoiDungHienTai.getId());
        n.setTenNhom(ten);
        n.setMoTa(txtMoTa.getText());
        nhomService.them(n);
        taiLai();
        xuLyLamMoi();
        lblLoi.setText("");
    }

    // ---------- UC10: Sua nhom ----------
    @FXML
    private void xuLySua() {
        Nhom chon = tvNhom.getSelectionModel().getSelectedItem();
        if (chon == null) {
            lblLoi.setText("Vui lòng chọn một nhóm cần sửa.");
            return;
        }
        String ten = layTenNhomNhapBoQua(chon.getId());
        if (ten == null) {
            return;
        }
        chon.setTenNhom(ten);
        chon.setMoTa(txtMoTa.getText());
        nhomService.sua(chon);
        taiLai();
        lblLoi.setText("");
    }

    // ---------- UC11: Xoa nhom ----------
    @FXML
    private void xuLyXoa() {
        Nhom chon = tvNhom.getSelectionModel().getSelectedItem();
        if (chon == null) {
            lblLoi.setText("Vui lòng chọn một nhóm cần xóa.");
            return;
        }
        Alert xacNhan = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa nhóm \"" + chon.getTenNhom() + "\"? Các liên hệ trong nhóm sẽ chuyển sang \"chưa phân nhóm\".",
                ButtonType.YES, ButtonType.NO);
        xacNhan.setTitle("Xác nhận xóa nhóm");
        xacNhan.setHeaderText(null);
        Optional<ButtonType> ketQua = xacNhan.showAndWait();
        if (ketQua.isPresent() && ketQua.get() == ButtonType.YES) {
            nhomService.xoa(chon.getId());
            taiLai();
            xuLyLamMoi();
        }
    }

    @FXML
    private void xuLyLamMoi() {
        txtTenNhom.clear();
        txtMoTa.clear();
        tvNhom.getSelectionModel().clearSelection();
    }

    @FXML
    private void xuLyDong() {
        ((Stage) tvNhom.getScene().getWindow()).close();
    }

    /** Kiem tra ten nhom khong rong va khong trung (UC09 buoc 4). */
    private String layTenNhomNhap() {
        return layTenNhomNhapBoQua(0);
    }

    /** boQuaId: id nhom dang sua, de khong tu so sanh trung voi chinh no (UC10 buoc 3). */
    private String layTenNhomNhapBoQua(int boQuaId) {
        String ten = txtTenNhom.getText() == null ? "" : txtTenNhom.getText().trim();
        if (!Validator.isTenNhomHopLe(ten)) {
            lblLoi.setText("Tên nhóm không được để trống.");
            return null;
        }
        if (nhomService.ganTrungTen(nguoiDungHienTai.getId(), ten, boQuaId)) {
            lblLoi.setText("Tên nhóm đã tồn tại.");
            return null;
        }
        return ten;
    }
}
