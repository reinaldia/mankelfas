package com.mankelfas.repository;

import com.mankelfas.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KendalaRepository {

    /**
     * Menyimpan data kendala teknisi ke dalam database saat teknisi melebihi target waktu penyelesaian.
     * @param idKeluhan ID dari keluhan yang sedang dikerjakan.
     * @param idTeknisi ID user teknisi yang menangani keluhan.
     * @param alasan Deskripsi atau alasan keterlambatan/kendala yang dihadapi.
     */
    public void tambahKendala(int idKeluhan, int idTeknisi, String alasan) {
        String sql = "INSERT INTO kendala_teknisi (id_keluhan, id_teknisi, alasan_kendala, waktu_dilaporkan) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, idKeluhan);
            stmt.setInt(2, idTeknisi);
            stmt.setString(3, alasan);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Gagal menyimpan data kendala teknisi: " + e.getMessage());
        }
    }
}
