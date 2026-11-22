package com.danhba.controller;

import com.danhba.model.NguoiDung;
import com.danhba.service.NguoiDungService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller cho man hinh dang nhap (UC01 - Dang nhap).
 */
public class DangNhapController {

    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private Label lblThongBao;

    private final NguoiDungService nguoiDungService = new NguoiDungService();

    @FXML
    private void xuLyDangNhap(ActionEvent event) {
        String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText() == null ? "" : txtMatKhau.getText();

        // 3a. Bo trong ten dang nhap hoac mat khau
        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            lblThongBao.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        NguoiDung nguoiDung;
        try {
            nguoiDung = nguoiDungService.dangNhap(tenDangNhap, matKhau);
        } catch (RuntimeException e) {
            lblThongBao.setText("Không kết nối được cơ sở dữ liệu. Vui lòng kiểm tra lại MySQL.");
            return;
        }

        // 4a. Sai ten dang nhap hoac mat khau
        if (nguoiDung == null) {
            lblThongBao.setText("Sai tên đăng nhập hoặc mật khẩu.");
            return;
        }

        moManHinhChinh(nguoiDung, event);
    }

    private void moManHinhChinh(NguoiDung nguoiDung, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chinh.fxml"));
            Parent root = loader.load();

            LienHeController controller = loader.getController();
            controller.khoiTao(nguoiDung);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Quản lý danh bạ điện thoại - " +
                    (nguoiDung.getHoTen() == null ? nguoiDung.getTenDangNhap() : nguoiDung.getHoTen()));
            stage.setResizable(true);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            lblThongBao.setText("Không mở được màn hình chính: " + e.getMessage());
        }
    }
}
