package com.mankelfas.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/db_mankelfas";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {}

    /**
     * Membangun jembatan komunikasi antara aplikasi dengan database MySQL.
     * Menggunakan pengaturan alamat bawaan untuk sistem lokal.
     * 
     * @return Objek Connection yang siap digunakan, atau kosong (null) jika server menolak
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Failed to connect to database", e);
        }
        return null;
    }
}
