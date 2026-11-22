package com.fakultas.fasilitas.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
<<<<<<< HEAD
    private static final String URL = "jdbc:mysql://localhost:3306/db_fasilitas?useSSL=false&serverTimezone=Asia/Jakarta";
=======
    // Menggunakan database 'db_fasilitas' sesuai dengan yang ada di HeidiSQL/Laragon kamu
    private static final String URL = "jdbc:mysql://localhost:3306/db_fasilitas?useSSL=false&serverTimezone=UTC";
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
<<<<<<< HEAD
            throw new SQLException("Driver MySQL JDBC tidak ditemukan!", e);
=======
            throw new SQLException("Driver MySQL tidak ditemukan: " + e.getMessage());
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}