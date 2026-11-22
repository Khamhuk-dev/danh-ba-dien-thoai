package com.danhba.controller;

import com.danhba.model.LienHe;
import com.danhba.model.Nhom;
import com.danhba.model.NguoiDung;
import com.danhba.model.SoDienThoai;
import com.danhba.service.LienHeService;
import com.danhba.service.NhomService;
import com.danhba.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller cho bieu mau them / sua lien he (UC03 - Them lien he,
 * UC04 - Sua lien he). Dung chung mot FXML cho ca hai truong hop.
 */
public class FormLienHeController {

    @FXML private Label lblTieuDe;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDiaChi;
    @FXML private DatePicker dpNgaySinh;
    @FXML private ComboBox<Nhom> cbNhom;
    @FXML private CheckBox chkYeuThich;
    @FXML private ListView<SoDienThoai> lvSoDienThoai;
    @FXML private TextField txtSoMoi;
    @FXML private ComboBox<String> cbLoaiSo;
    @FXML private TextArea txtGhiChu;
    @FXML private Label lblLoi;
    @FXML private Button btnLuu;

    private final LienHeService lienHeService = new LienHeService();
    private final NhomService nhomService = new NhomService();
    private final ObservableList<SoDienThoai> dsSoDienThoai = FXCollections.observableArrayList();
    private static final Nhom KHONG_NHOM = new Nhom(0, 0, "(Chưa phân nhóm)", "");

    private NguoiDung nguoiDungHienTai;
    private LienHe lienHeDangSua; // null neu la them moi
    private boolean daLuuThanhCong = false;

    @FXML
    private void initialize() {
        cbLoaiSo.setItems(FXCollections.observableArrayList(
                SoDienThoai.DI_DONG, SoDienThoai.NHA_RIENG, SoDienThoai.CONG_TY, SoDienThoai.KHAC));
        cbLoaiSo.getSelectionModel().select(SoDienThoai.DI_DONG);
        lvSoDienThoai.setItems(dsSoDienThoai);
    }

    /** @param lienHe null khi them moi (UC03), khac null khi sua (UC04). */
    public void khoiTao(NguoiDung nguoiDung, LienHe lienHe) {
        this.nguoiDungHienTai = nguoiDung;
        this.lienHeDangSua = lienHe;

        ObservableList<Nhom> dsNhom = FXCollections.observableArrayList();
        dsNhom.add(KHONG_NHOM);
        dsNhom.addAll(nhomService.layTatCa(nguoiDung.getId()));
        cbNhom.setItems(dsNhom);

        if (lienHe == null) {
            lblTieuDe.setText("Thêm liên hệ");
            cbNhom.getSelectionModel().select(KHONG_NHOM);
        } else {
            lblTieuDe.setText("Sửa liên hệ");
            txtHoTen.setText(lienHe.getHoTen());
            txtEmail.setText(lienHe.getEmail());
            txtDiaChi.setText(lienHe.getDiaChi());
            dpNgaySinh.setValue(lienHe.getNgaySinh());
            chkYeuThich.setSelected(lienHe.isYeuThich());
            txtGhiChu.setText(lienHe.getGhiChu());
            dsSoDienThoai.setAll(lienHe.getDsSoDienThoai());
            dsNhom.stream()
                    .filter(n -> n.getId() == lienHe.getNhomId())
                    .findFirst()
                    .ifPresentOrElse(n -> cbNhom.getSelectionModel().select(n),
                            () -> cbNhom.getSelectionModel().select(KHONG_NHOM));
        }
    }

    @FXML
    private void xuLyThemSo() {
        String so = txtSoMoi.getText() == null ? "" : txtSoMoi.getText().trim();
        if (!Validator.isSoDienThoaiHopLe(so)) {
            lblLoi.setText("Số điện thoại không hợp lệ (9-12 chữ số, có thể bắt đầu bằng +).");
            return;
        }
        lblLoi.setText("");
        boolean laSoChinh = dsSoDienThoai.isEmpty(); // so dau tien la so chinh
        dsSoDienThoai.add(new SoDienThoai(so, cbLoaiSo.getValue(), laSoChinh));
        txtSoMoi.clear();
    }

    @FXML
    private void xuLyXoaSo() {
        SoDienThoai chon = lvSoDienThoai.getSelectionModel().getSelectedItem();
        if (chon != null) {
            boolean laSoChinh = chon.isLaSoChinh();
            dsSoDienThoai.remove(chon);
            if (laSoChinh && !dsSoDienThoai.isEmpty()) {
                dsSoDienThoai.get(0).setLaSoChinh(true);
            }
        }
    }

    // ---------- Luu (them hoac sua) ----------
    @FXML
    private void xuLyLuu() {
        String hoTen = txtHoTen.getText() == null ? "" : txtHoTen.getText().trim();
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();

        // 5a / 3a: thieu ho ten hoac so dien thoai, hoac du lieu sai dinh dang
        if (!Validator.isHoTenHopLe(hoTen)) {
            lblLoi.setText("Họ tên không được để trống (tối đa 100 ký tự).");
            return;
        }
        if (dsSoDienThoai.isEmpty()) {
            lblLoi.setText("Vui lòng nhập ít nhất một số điện thoại.");
            return;
        }
        if (!Validator.isEmailHopLe(email)) {
            lblLoi.setText("Email không đúng định dạng.");
            return;
        }

        // 5b: canh bao neu so dien thoai da ton tai o lien he khac
        for (SoDienThoai sdt : dsSoDienThoai) {
            boolean trungOLienHeKhac = lienHeService.soDaTonTai(sdt.getSo())
                    && (lienHeDangSua == null || khongThuocLienHeDangSua(sdt));
            if (trungOLienHeKhac) {
                Alert xacNhan = new Alert(Alert.AlertType.CONFIRMATION,
                        "Số điện thoại \"" + sdt.getSo() + "\" đã tồn tại ở liên hệ khác.\n" +
                                "Bạn có muốn tiếp tục lưu không?",
                        ButtonType.YES, ButtonType.NO);
                xacNhan.setTitle("Cảnh báo trùng số điện thoại");
                xacNhan.setHeaderText(null);
                var ketQua = xacNhan.showAndWait();
                if (ketQua.isEmpty() || ketQua.get() != ButtonType.YES) {
                    return;
                }
                break;
            }
        }

        LienHe lh = (lienHeDangSua == null) ? new LienHe() : lienHeDangSua;
        lh.setNguoiDungId(nguoiDungHienTai.getId());
        lh.setHoTen(hoTen);
        lh.setEmail(email);
        lh.setDiaChi(txtDiaChi.getText());
        lh.setNgaySinh(dpNgaySinh.getValue());
        lh.setGhiChu(txtGhiChu.getText());
        lh.setYeuThich(chkYeuThich.isSelected());
        Nhom nhomChon = cbNhom.getSelectionModel().getSelectedItem();
        lh.setNhomId(nhomChon == null ? 0 : nhomChon.getId());
        lh.setDsSoDienThoai(new java.util.ArrayList<>(dsSoDienThoai));

        boolean ok = (lienHeDangSua == null) ? lienHeService.them(lh) : lienHeService.sua(lh);
        if (ok) {
            daLuuThanhCong = true;
            dong();
        } else {
            lblLoi.setText("Không thể lưu liên hệ. Vui lòng thử lại.");
        }
    }

    private boolean khongThuocLienHeDangSua(SoDienThoai sdt) {
        return lienHeDangSua.getDsSoDienThoai().stream()
                .noneMatch(s -> s.getSo().equals(sdt.getSo()));
    }

    @FXML
    private void xuLyHuy() {
        dong();
    }

    public boolean daLuu() {
        return daLuuThanhCong;
    }

    private void dong() {
        ((Stage) btnLuu.getScene().getWindow()).close();
    }
}
