package com.danhba.controller;

import com.danhba.model.Nhom;
import com.danhba.model.NguoiDung;
import com.danhba.service.LienHeService;
import com.danhba.service.NhomService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller man hinh thong ke (UC14 - Thong ke danh ba).
 * Tinh tong so lien he, so lien he yeu thich va so lien he theo tung nhom.
 */
public class ThongKeController {

    @FXML private Label lblTongSo;
    @FXML private Label lblYeuThich;
    @FXML private BarChart<String, Number> bieuDoNhom;

    private final LienHeService lienHeService = new LienHeService();
    private final NhomService nhomService = new NhomService();

    public void khoiTao(NguoiDung nguoiDung) {
        // 2a. Danh ba trong: cac so lieu hien thi bang 0 (demTongSo/demYeuThich tra ve 0)
        int tongSo = lienHeService.demTongSo(nguoiDung.getId());
        int yeuThich = lienHeService.demYeuThich(nguoiDung.getId());
        lblTongSo.setText("Tổng số liên hệ: " + tongSo);
        lblYeuThich.setText("Số liên hệ yêu thích: " + yeuThich);

        List<Nhom> dsNhom = nhomService.layTatCa(nguoiDung.getId());
        XYChart.Series<String, Number> seri = new XYChart.Series<>();
        seri.setName("Số liên hệ theo nhóm");
        for (Nhom n : dsNhom) {
            seri.getData().add(new XYChart.Data<>(n.getTenNhom(), n.getSoLuongLienHe()));
        }
        bieuDoNhom.getData().add(seri);
    }

    @FXML
    private void xuLyDong() {
        ((Stage) lblTongSo.getScene().getWindow()).close();
    }
}
