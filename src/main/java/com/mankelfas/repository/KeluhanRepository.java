package com.mankelfas.repository;

import com.mankelfas.config.DatabaseConnection;
import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.model.user.Mahasiswa;
import com.mankelfas.model.user.Teknisi;
import com.mankelfas.model.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KeluhanRepository implements IKeluhanRepository {

    private UserRepository userRepository = new UserRepository();
    private FasilitasRepository fasilitasRepository = new FasilitasRepository();

    /**
     * Mengambil daftar seluruh keluhan dari database.
     * Merangkai kembali data relasional dari tabel keluhan, fasilitas, dan pelapor menjadi satu kesatuan objek keluhan yang utuh.
     * 
     * @return Kumpulan objek keluhan
     */
    @Override
    public List<Keluhan> getAllKeluhan() {
        List<Keluhan> list = new ArrayList<>();
        String sql = "SELECT * FROM keluhan";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Mengambil semua data pengguna dan fasilitas untuk dipasangkan ke dalam objek keluhan yang bersesuaian.
            List<User> allUsers = userRepository.getAllUsers();
            List<Fasilitas> allFasilitas = fasilitasRepository.getAllFasilitas();

            while (rs.next()) {
                int idPelapor = rs.getInt("id_pelapor");
                int idFasilitas = rs.getInt("id_fasilitas");
                
                Mahasiswa pelapor = (Mahasiswa) allUsers.stream().filter(u -> u.getIdUser() == idPelapor).findFirst().orElse(null);
                Fasilitas fasilitas = allFasilitas.stream().filter(f -> f.getIdFasilitas() == idFasilitas).findFirst().orElse(null);

                Keluhan k = new Keluhan(
                    rs.getInt("id_keluhan"),
                    rs.getString("deskripsi"),
                    rs.getString("foto_bukti"),
                    pelapor,
                    fasilitas
                );

                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    k.setStatus(StatusKeluhan.valueOf(statusStr));
                }
                
                String prioritasStr = rs.getString("prioritas");
                if (prioritasStr != null) {
                    k.setPrioritas(com.mankelfas.enumeration.Prioritas.valueOf(prioritasStr));
                }

                k.setEstimasiWaktu(rs.getString("estimasi_waktu"));
                k.setArchived(rs.getBoolean("archived"));
                
                java.sql.Timestamp waktuDiproses = rs.getTimestamp("waktu_diproses");
                if (waktuDiproses != null) {
                    k.setWaktuDiproses(waktuDiproses.toLocalDateTime());
                }
                
                java.sql.Timestamp targetSelesai = rs.getTimestamp("target_selesai");
                if (targetSelesai != null) {
                    k.setTargetSelesai(targetSelesai.toLocalDateTime());
                }

                int idTeknisi = rs.getInt("id_teknisi");
                if (!rs.wasNull()) {
                    Teknisi teknisi = (Teknisi) allUsers.stream().filter(u -> u.getIdUser() == idTeknisi).findFirst().orElse(null);
                    k.setTeknisi(teknisi);
                }

                list.add(k);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data keluhan: " + e.getMessage(), e);
        }
        
        return list;
    }

    /**
     * Merekam data pengajuan keluhan baru ke dalam database.
     * Jika berhasil, akan secara otomatis menyematkan nomor identitas yang dibangkitkan oleh sistem ke dalam objek keluhan tersebut.
     * 
     * @param keluhan Objek keluhan baru yang akan disimpan
     * @return Tanda keberhasilan operasi penyimpanan
     */
    @Override
    public boolean addKeluhan(Keluhan keluhan) {
        String sql = "INSERT INTO keluhan (deskripsi, foto_bukti, status, id_pelapor, id_fasilitas) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, keluhan.getDeskripsi());
            stmt.setString(2, keluhan.getFotoBukti());
            stmt.setString(3, keluhan.getStatus().name());
            stmt.setInt(4, keluhan.getPelapor().getIdUser());
            stmt.setInt(5, keluhan.getFasilitas().getIdFasilitas());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        keluhan.setIdKeluhan(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambah data keluhan: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Menerapkan pembaruan status, penugasan teknisi, atau detail tenggat waktu ke dalam database.
     * 
     * @param keluhan Entitas keluhan yang telah dimodifikasi
     * @return Tanda keberhasilan operasi pembaruan
     */
    @Override
    public boolean updateKeluhan(Keluhan keluhan) {
        String sql = "UPDATE keluhan SET deskripsi=?, foto_bukti=?, status=?, prioritas=?, estimasi_waktu=?, waktu_diproses=?, target_selesai=?, archived=?, id_teknisi=? WHERE id_keluhan=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, keluhan.getDeskripsi());
            stmt.setString(2, keluhan.getFotoBukti());
            stmt.setString(3, keluhan.getStatus().name());
            stmt.setString(4, keluhan.getPrioritas() != null ? keluhan.getPrioritas().name() : null);
            stmt.setString(5, keluhan.getEstimasiWaktu());
            stmt.setObject(6, keluhan.getWaktuDiproses());
            stmt.setObject(7, keluhan.getTargetSelesai());
            stmt.setBoolean(8, keluhan.isArchived());
            
            if (keluhan.getTeknisi() != null) {
                stmt.setInt(9, keluhan.getTeknisi().getIdUser());
            } else {
                stmt.setNull(9, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(10, keluhan.getIdKeluhan());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal update data keluhan: " + e.getMessage(), e);
        }
    }

    /**
     * Menghapus baris data keluhan secara permanen dari database.
     * 
     * @param idKeluhan Tanda identitas keluhan yang menjadi target penghapusan
     * @return Tanda keberhasilan operasi penghapusan
     */
    @Override
    public boolean deleteKeluhan(int idKeluhan) {
        String sql = "DELETE FROM keluhan WHERE id_keluhan=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKeluhan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus data keluhan: " + e.getMessage(), e);
        }
    }
}
