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
     * Membuka koneksi database dan menarik keseluruhan data fasilitas yang tersedia.
     * Dapat digunakan untuk mengisi pilihan pada menu saat Mahasiswa melaporkan masalah.
     * 
     * @return Kumpulan data fasilitas
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
     * Merekam data fasilitas baru secara permanen ke dalam tabel database.
     * Secara otomatis mengambil identitas unik yang dibangkitkan oleh sistem.
     * 
     * @param fasilitas Entitas fasilitas yang ingin ditambahkan
     * @return Status keberhasilan operasi penambahan
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
     * Menjalankan instruksi pembaruan keterangan untuk fasilitas tertentu di database.
     * 
     * @param fasilitas Entitas fasilitas dengan informasi terbaru
     * @return Status keberhasilan proses pembaruan
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

    /**
     * Mencabut dan menghapus data fasilitas sepenuhnya dari database.
     * 
     * @param idFasilitas Nomor pengenal fasilitas yang hendak dihapus
     * @return Status keberhasilan proses penghapusan
     */
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
