package com.danhba;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Diem khoi dong ung dung (goi main - Bang 4.5).
 * Mo man hinh dang nhap (dang_nhap.fxml) dau tien, dung voi UC01.
 */
public class Main extends Application {

    @Override
    public void start(Stage stageChinh) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dang_nhap.fxml"));
        Parent root = loader.load();

        stageChinh.setTitle("Quản lý danh bạ điện thoại");
        stageChinh.setScene(new Scene(root));
        stageChinh.setResizable(false);
        stageChinh.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
