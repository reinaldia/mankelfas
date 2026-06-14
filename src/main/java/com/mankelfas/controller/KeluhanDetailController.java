package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.RiwayatKeluhan;
import com.mankelfas.model.misc.Komentar;

/**
 * Menampilkan rincian laporan keluhan secara menyeluruh.
 * Menyajikan informasi lengkap mengenai jenis kerusakan, identitas pelapor, serta riwayat penanganan oleh teknisi.
 */
public class KeluhanDetailController {

    @FXML private Label lblIdKeluhan;
    @FXML private Label lblStatus;
    @FXML private Label lblWaktuDiproses;
    @FXML private Label lblEstimasi;
    @FXML private Label lblDeskripsi;
    
    @FXML private Label lblNamaFasilitas;
    @FXML private Label lblLokasi;
    @FXML private Label lblKategori;
    @FXML private Label lblKondisi;
    
    @FXML private TextArea txtAreaRiwayat;

    /**
     * Menyuntikkan data objek keluhan ke dalam elemen visual UI rincian.
     * Secara otomatis menguraikan isi laporan, detail fasilitas, serta riwayat interaksi sebelumnya.
     * 
     * @param k Objek keluhan yang dipilih untuk diinspeksi
     */
    public void setKeluhanData(Keluhan k) {
        // Menghentikan eksekusi secara diam-diam jika data kosong untuk mencegah error tampilan
        if (k == null) return;
        
        // Memasukkan identitas umum keluhan
        lblIdKeluhan.setText("ID: " + k.getIdKeluhan());
        lblStatus.setText("Status: " + k.getStatus().name());
        
        // Memformat tampilan waktu menjadi standar Indonesia agar mudah dibaca
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String waktu = (k.getWaktuDiproses() != null) ? k.getWaktuDiproses().format(formatter) : "-";
        lblWaktuDiproses.setText("Mulai Diproses: " + waktu);
        
        // Memasukkan detail durasi perkiraan dan isi pesan keluhan
        lblEstimasi.setText("Estimasi Waktu: " + (k.getEstimasiWaktu() != null ? k.getEstimasiWaktu() : "-"));
        lblDeskripsi.setText("Deskripsi: " + k.getDeskripsi());
        
        // Memeriksa dan menampilkan informasi spesifik fasilitas terkait
        Fasilitas f = k.getFasilitas();
        if (f != null) {
            lblNamaFasilitas.setText("Nama: " + f.getNama());
            lblLokasi.setText("Lokasi: " + f.getLokasi());
            lblKategori.setText("Kategori: " + f.getKategori());
            lblKondisi.setText("Kondisi Terakhir: " + f.getKondisi());
        }

        // Merangkai log jejak rekam (riwayat status dan percakapan) menggunakan StringBuilder untuk efisiensi
        StringBuilder sb = new StringBuilder();
        sb.append("--- RIWAYAT STATUS ---\n");
        // Melakukan perulangan untuk mengekstrak setiap baris riwayat yang terekam
        for (RiwayatKeluhan r : k.getRiwayat()) {
            sb.append(r.getInfo()).append("\n");
        }
        sb.append("\n--- KOMENTAR TEKNISI ---\n");
        // Melakukan perulangan untuk mengekstrak setiap catatan dari teknisi lapangan
        for (Komentar c : k.getKomentar()) {
            sb.append(c.getKomentar()).append("\n");
        }
        
        // Menyajikan teks gabungan panjang ke area teks UI
        txtAreaRiwayat.setText(sb.toString());
    }

    /**
     * Mengakhiri sesi pratinjau detail keluhan dan menutup layarnya secara paksa.
     */
    @FXML
    private void tutupDetail() {
        // Menemukan referensi jendela aktif dan mengeksekusi perintah tutup
        Stage stage = (Stage) lblIdKeluhan.getScene().getWindow();
        stage.close();
    }
}
