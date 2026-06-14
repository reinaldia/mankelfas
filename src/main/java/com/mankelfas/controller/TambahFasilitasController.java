package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.service.KeluhanService;

/**
 * Menangani proses pengisian formulir penambahan fasilitas baru.
 * Bertugas menangkap data masukan dan menyimpannya ke dalam sistem pencatatan fasilitas.
 */
public class TambahFasilitasController {

    @FXML private TextField inputNama;
    @FXML private ComboBox<String> comboKategori;
    @FXML private TextField inputLokasi;
    @FXML private ComboBox<String> comboKondisi;

    private FasilitasController parentController;

    /**
     * Mempersiapkan pilihan standar untuk klasifikasi kategori dan kondisi fasilitas.
     */
    @FXML
    public void initialize() {
        // Mengisi menu pilihan dengan nilai-nilai baku
        comboKategori.getItems().addAll("Elektronik", "Furnitur", "Bangunan", "Lainnya");
        comboKondisi.getItems().addAll("Baik", "Rusak Ringan", "Rusak Berat", "Mati Total");
    }

    /**
     * Mengaitkan formulir ini dengan UI induknya agar dapat mengabari setelah data tersimpan.
     * 
     * @param parent Pengontrol halaman utama fasilitas
     */
    public void setParentController(FasilitasController parent) {
        this.parentController = parent;
    }

    /**
     * Memproses logika pengecekan dan penyimpanan wujud fasilitas baru ke dalam daftar sistem.
     */
    @FXML
    private void simpanFasilitas() {
        // Menyalin setiap hasil ketikan pengguna ke dalam variabel
        String nama = inputNama.getText();
        String kategori = comboKategori.getValue();
        String lokasi = inputLokasi.getText();
        String kondisi = comboKondisi.getValue();

        // Mencegah proses penyimpanan jika terdapat data wajib yang belum terisi
        if (nama == null || nama.trim().isEmpty() ||
            kategori == null || lokasi == null || lokasi.trim().isEmpty() || kondisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Mohon lengkapi semua field data fasilitas!");
            alert.show();
            return;
        }

        // Memanfaatkan layanan untuk mencatat objek fasilitas baru
        KeluhanService service = KeluhanService.getInstance();
        int newId = service.getAllFasilitas().size() + 1;
        Fasilitas fBaru = new Fasilitas(newId, nama, kategori, lokasi, kondisi);
        
        // Memasukkan entri baru ke repositori data yang sedang berjalan
        service.getAllFasilitas().add(fBaru);
        
        // Menyuntikkan entri baru tersebut langsung ke dalam tabel utama jika memungkinkan
        if (parentController != null) {
            parentController.addFasilitasToTable(fBaru);
        }

        // Memberikan tanda bahwa tugas telah selesai dengan sukses
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Fasilitas baru berhasil ditambahkan!");
        alert.showAndWait();

        // Mengakhiri proses dan menutup jendela pop-up
        tutupWindow();
    }

    /**
     * Mengakhiri interaksi pengisian form dan menutup jendela yang sedang aktif.
     */
    @FXML
    private void tutupWindow() {
        // Mengidentifikasi komponen induk dan memberikan instruksi terminasi
        Stage stage = (Stage) inputNama.getScene().getWindow();
        stage.close();
    }
}
