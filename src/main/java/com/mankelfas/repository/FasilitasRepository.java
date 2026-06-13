package com.mankelfas.repository;

import com.mankelfas.config.DatabaseConnection;
import com.mankelfas.model.keluhan.Fasilitas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FasilitasRepository implements IFasilitasRepository {

    /**
     * Mengambil daftar semua fasilitas yang ada di database.
     * Dapat digunakan untuk mengisi pilihan combobox (dropdown) saat Mahasiswa akan melaporkan masalah.
     */
    @Override
    public List<Fasilitas> getAllFasilitas() {
        List<Fasilitas> fasilitasList = new ArrayList<>();
        String sql = "SELECT * FROM fasilitas";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Fasilitas f = new Fasilitas(
                    rs.getInt("id_fasilitas"),
                    rs.getString("nama"),
                    rs.getString("kategori"),
                    rs.getString("lokasi"),
                    rs.getString("kondisi")
                );
                fasilitasList.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data fasilitas: " + e.getMessage(), e);
        }
        
        return fasilitasList;
    }

    /**
     * Menyimpan data fasilitas baru ke dalam database.
     * Mengembalikan true jika fasilitas sukses ditambahkan.
     */
    @Override
    public boolean addFasilitas(Fasilitas fasilitas) {
        String sql = "INSERT INTO fasilitas (nama, kategori, lokasi, kondisi) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, fasilitas.getNama());
            stmt.setString(2, fasilitas.getKategori());
            stmt.setString(3, fasilitas.getLokasi());
            stmt.setString(4, fasilitas.getKondisi());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        fasilitas.setIdFasilitas(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambah data fasilitas: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Memperbarui informasi dari sebuah fasilitas (seperti mengubah kondisi menjadi 'Rusak' atau 'Bagus').
     */
    @Override
    public boolean updateFasilitas(Fasilitas fasilitas) {
        String sql = "UPDATE fasilitas SET nama=?, kategori=?, lokasi=?, kondisi=? WHERE id_fasilitas=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, fasilitas.getNama());
            stmt.setString(2, fasilitas.getKategori());
            stmt.setString(3, fasilitas.getLokasi());
            stmt.setString(4, fasilitas.getKondisi());
            stmt.setInt(5, fasilitas.getIdFasilitas());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal update data fasilitas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFasilitas(int idFasilitas) {
        String sql = "DELETE FROM fasilitas WHERE id_fasilitas=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idFasilitas);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus data fasilitas: " + e.getMessage(), e);
        }
    }
}
