package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mankelfas.model.misc.Kendala;
import com.mankelfas.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Mengelola catatan hambatan yang dilaporkan oleh teknisi di lapangan.
 * Menyimpan informasi detail mengenai masalah perbaikan sehingga memudahkan pemantauan dan evaluasi.
 */
public class KendalaController {

    @FXML private TableView<Kendala> tabelKendala;
    @FXML private TableColumn<Kendala, Integer> colIdKeluhan;
    @FXML private TableColumn<Kendala, String> colTeknisi;
    @FXML private TableColumn<Kendala, String> colAlasan;
    @FXML private TableColumn<Kendala, String> colWaktu;

    /**
     * Mempersiapkan tampilan awal halaman log kendala perbaikan.
     * Mengatur konfigurasi pengikatan kolom tabel terhadap atribut kelas Kendala.
     */
    @FXML
    public void initialize() {
        // Menghubungkan setiap elemen kolom dengan variabel pembawa data masing-masing
        colIdKeluhan.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
        colTeknisi.setCellValueFactory(new PropertyValueFactory<>("namaTeknisi"));
        colAlasan.setCellValueFactory(new PropertyValueFactory<>("alasan"));
        colWaktu.setCellValueFactory(new PropertyValueFactory<>("waktuDilaporkan"));

        // Menginisiasi proses pengambilan data setelah kolom siap
        loadData();
    }

    /**
     * Memanggil riwayat daftar kendala langsung dari database dengan metode sambungan langsung (JDBC).
     * Fokus pada penggabungan informasi teknisi (JOIN) dan disortir berdasarkan waktu lapor terbaru.
     */
    private void loadData() {
        ObservableList<Kendala> list = FXCollections.observableArrayList();
        
        // Meracik baris perintah kueri SQL untuk menarik dan menggabungkan data relevan
        String sql = "SELECT k.id_keluhan, u.nama, k.alasan_kendala, k.waktu_dilaporkan " +
                     "FROM kendala_teknisi k " +
                     "JOIN users u ON k.id_teknisi = u.id_user " +
                     "ORDER BY k.waktu_dilaporkan DESC";
        
        // Membuka jalur komunikasi terisolasi dengan database menggunakan mekanisme try-with-resources
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Menyisir baris per baris hasil dari eksekusi kueri
            while (rs.next()) {
                // Merangkai objek representasi kendala baru lalu memasukkannya ke dalam daftar sementara
                list.add(new Kendala(
                    rs.getInt("id_keluhan"),
                    rs.getString("nama"),
                    rs.getString("alasan_kendala"),
                    rs.getTimestamp("waktu_dilaporkan").toString()
                ));
            }
            
            // Mengganti isi tabel saat ini dengan data paling mutakhir dari database
            tabelKendala.setItems(list);
            
        } catch (Exception e) {
            // Mencatat jejak pengecualian di sistem konsol apabila transaksi gagal ditunaikan
            e.printStackTrace();
        }
    }
}
