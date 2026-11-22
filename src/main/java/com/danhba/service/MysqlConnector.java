package com.danhba.service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Ket noi va truy van CSDL MySQL thong qua JDBC (goi service - Bang 4.5).
 * Doc cau hinh tu db.properties (resources), gom url, user, password.
 */
public final class MysqlConnector {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/danh_ba?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    private static String url = DEFAULT_URL;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;

    static {
        try (InputStream in = MysqlConnector.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                url = p.getProperty("db.url", DEFAULT_URL);
                user = p.getProperty("db.user", DEFAULT_USER);
                password = p.getProperty("db.password", DEFAULT_PASSWORD);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("Khong the doc db.properties, dung cau hinh mac dinh: " + e.getMessage());
        }
    }

    private MysqlConnector() {
    }

    /** Tra ve mot ket noi moi toi CSDL danh_ba. Noi goi chiu trach nhiem dong ket noi (try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
