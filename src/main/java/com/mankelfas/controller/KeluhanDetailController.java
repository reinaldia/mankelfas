package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
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
    
    @FXML private TextArea txtAreaRiwayatStatus;
    @FXML private TextArea txtAreaKomentar;
    @FXML private ImageView imgBukti;
    @FXML private Label lblNoFoto;

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

        StringBuilder sbRiwayat = new StringBuilder();
        for (RiwayatKeluhan r : k.getRiwayat()) {
            sbRiwayat.append(r.getInfo()).append("\n");
        }
        txtAreaRiwayatStatus.setText(sbRiwayat.toString());

        StringBuilder sbKomentar = new StringBuilder();
        if (k.getKomentar() != null && !k.getKomentar().isEmpty()) {
            for (Komentar c : k.getKomentar()) {
                sbKomentar.append(c.getKomentar()).append("\n\n");
            }
        } else {
            sbKomentar.append("Belum ada komentar teknisi.");
        }
        txtAreaKomentar.setText(sbKomentar.toString());

        if (k.getFotoBukti() != null && !k.getFotoBukti().isEmpty()) {
            try {
                File fileFoto = new File(k.getFotoBukti());
                if (!fileFoto.exists()) {
                    fileFoto = new File(k.getFotoBukti().replace("file:", ""));
                }
                
                if (fileFoto.exists()) {
                    BufferedImage bufferedImage = ImageIO.read(fileFoto);
                    if (bufferedImage != null) {
                        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                        imgBukti.setImage(fxImage);
                        lblNoFoto.setVisible(false);
                    } else {
                        lblNoFoto.setText("Gagal decode foto WebP.");
                        lblNoFoto.setVisible(true);
                    }
                } else {
                    lblNoFoto.setText("File foto tidak ditemukan.");
                    lblNoFoto.setVisible(true);
                }
            } catch (Exception e) {
                lblNoFoto.setText("Error memuat foto: " + e.getMessage());
                lblNoFoto.setVisible(true);
            }
        } else {
            lblNoFoto.setVisible(true);
        }
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
