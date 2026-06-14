package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.service.KeluhanService;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Mengelola UI khusus untuk pengguna berstatus Mahasiswa.
 * Memfasilitasi pelaporan fasilitas rusak, pengunggahan foto bukti, serta pemantauan status perbaikan secara langsung.
 */
public class MahasiswaController {

    @FXML private ComboBox<String> comboFasilitas;
    @FXML private TextArea inputDeskripsi;
    @FXML private ImageView imagePreview;
    @FXML private TableView<Keluhan> tabelKeluhan;
    @FXML private TableColumn<Keluhan, Integer> colId;
    @FXML private TableColumn<Keluhan, String> colFasilitas;
    @FXML private TableColumn<Keluhan, String> colDeskripsi;
    @FXML private TableColumn<Keluhan, String> colStatus;

    @FXML private Label lblWelcome;

    private KeluhanService keluhanService;
    private ObservableList<Keluhan> keluhanObservableList;
    private List<Fasilitas> fasilitasList;
    private File selectedFile;

    /**
     * Menyiapkan tampilan UI saat halaman dasbor Mahasiswa pertama kali dimuat.
     * Termasuk memuat daftar keluhan yang relevan, mengatur kolom tabel, dan mengisi daftar pilihan fasilitas.
     */
    @FXML
    public void initialize() {
        // Menampilkan pesan sapaan khusus menggunakan nama pengguna aktif
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        try {
            // Memulai interaksi dengan layanan inti untuk menarik data keluhan dan fasilitas
            keluhanService = KeluhanService.getInstance();
            fasilitasList = keluhanService.getAllFasilitas();

            // Memasukkan daftar fasilitas yang tersedia ke dalam menu lungsur pilihan pelaporan
            for (Fasilitas f : fasilitasList) {
                comboFasilitas.getItems().add(f.getInfo());
            }

            // Menyambungkan struktur kolom tabel dengan properti kelas Keluhan
            colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
            
            // Memberikan modifikasi visual pada teks deskripsi jika keluhan tersebut telah diarsipkan
            colDeskripsi.setCellValueFactory(cellData -> {
                Keluhan k = cellData.getValue();
                String desc = k.getDeskripsi();
                if (k.isArchived()) {
                    desc = "[DIARSIPKAN] " + desc;
                }
                return new SimpleStringProperty(desc);
            });
            
            // Mengekstraksi informasi status dan rincian fasilitas agar dapat ditampilkan sebagai teks
            colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
            colFasilitas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFasilitas().getInfo()));

            // Menyajikan daftar utuh keluhan ke dalam tabel
            keluhanObservableList = FXCollections.observableArrayList(keluhanService.getAllKeluhan());
            tabelKeluhan.setItems(keluhanObservableList);
        } catch (Exception e) {
            // Melaporkan kepada pengguna jika terjadi kegagalan pemuatan data
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat data dashboard: " + e.getMessage());
            alert.show();
        }
    }

    /**
     * Membuka jendela dialog pencarian file (File Explorer) agar pengguna dapat menyisipkan foto bukti kerusakan.
     */
    @FXML
    private void pilihFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti Keluhan");
        
        // Membatasi hanya jenis file gambar tertentu yang diizinkan untuk diunggah
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        
        // Menampilkan penjelajah file
        File file = fileChooser.showOpenDialog(tabelKeluhan.getScene().getWindow());
        if (file != null) {
            // Menyimpan referensi file dan menampilkannya sebagai pratinjau di layar
            selectedFile = file;
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Mengumpulkan semua informasi dari form pelaporan dan memprosesnya menjadi entitas keluhan resmi.
     * Mengelola penyimpanan gambar secara lokal dan mengonversinya ke format yang lebih optimal (WEBP).
     */
    @FXML
    private void kirimKeluhan() {
        int selectedIndex = comboFasilitas.getSelectionModel().getSelectedIndex();
        String deskripsi = inputDeskripsi.getText();

        // Memvalidasi apakah isian fasilitas dan rincian teks telah diberikan secara benar
        if (selectedIndex >= 0 && deskripsi != null && !deskripsi.trim().isEmpty()) {
            try {
                // Mengekstraksi objek fasilitas terpilih dari daftar internal
                Fasilitas f = fasilitasList.get(selectedIndex);
                
                // Menentukan keabsahan sesi pengguna aktif
                com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
                if (!(currentUser instanceof com.mankelfas.model.user.Mahasiswa)) {
                    throw new RuntimeException("Sesi mahasiswa tidak valid!");
                }
                
                com.mankelfas.model.user.Mahasiswa mhs = (com.mankelfas.model.user.Mahasiswa) currentUser;
                
                String webpFilePath = null;
                // Memproses unggahan foto jika pengguna menyertakannya
                if (selectedFile != null) {
                    // Memastikan folder penyimpanan lokal 'uploads' telah tersedia
                    File uploadDir = new File("uploads");
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    // Meracik nama file baru yang unik dengan imbuhan penanda waktu
                    String newFileName = "keluhan_awal_" + System.currentTimeMillis() + ".webp";
                    File outputFile = new File(uploadDir, newFileName);
                    
                    // Membaca file gambar mentah dan menyimpannya ke dalam format WEBP demi efisiensi
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {
                        ImageIO.write(image, "webp", outputFile);
                        webpFilePath = outputFile.getAbsolutePath();
                    }
                }
                
                // Menyusun objek keluhan baru dan menitipkannya ke lapisan layanan untuk dicatat ke database
                Keluhan k = new Keluhan(0, deskripsi, webpFilePath, mhs, f);
                keluhanService.addKeluhan(k);
                
                // Memasukkan tambahan data ke tabel tanpa memuat ulang layar
                keluhanObservableList.add(k);
                tabelKeluhan.refresh();
                
                // Membersihkan UI form setelah pengiriman berhasil
                inputDeskripsi.clear();
                comboFasilitas.getSelectionModel().clearSelection();
                selectedFile = null;
                imagePreview.setImage(null);

                // Mengabarkan pesan keberhasilan kepada pelapor
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Keluhan berhasil dikirim dan tersimpan di database!");
                alert.show();
            } catch (Exception e) {
                // Memberikan rincian jika operasi perekaman gagal
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Terjadi kesalahan saat menyimpan keluhan: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih fasilitas dan isi deskripsi!");
            alert.show();
        }
    }

    /**
     * Membuka tampilan rincian untuk memantau status perkembangan penyelesaian suatu keluhan.
     */
    @FXML
    private void lihatDetail() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        // Memastikan mahasiswa telah mengklik suatu baris di tabel riwayat pelaporan
        if (selectedKeluhan != null) {
            try {
                // Menyiapkan cetak biru halaman rincian informasi keluhan
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/keluhan_detail.fxml"));
                Parent root = loader.load();
                
                // Mengirimkan data referensi keluhan terpilih ke pengontrol rincian
                KeluhanDetailController controller = loader.getController();
                controller.setKeluhanData(selectedKeluhan);
                
                // Menampilkan layar detail dalam mode terfokus (modal)
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Detail Fasilitas & Keluhan");
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal memuat detail: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Menampilkan kotak informasi yang memuat ringkasan identitas dan peran akun mahasiswa.
     */
    @FXML
    private void lihatInfoProfil() {
        // Melakukan verifikasi untuk menjamin kelas data yang aktif sesuai dengan harapan
        if (com.mankelfas.util.Session.getCurrentUser() instanceof com.mankelfas.model.user.Mahasiswa) {
            com.mankelfas.model.user.Mahasiswa mhs = (com.mankelfas.model.user.Mahasiswa) com.mankelfas.util.Session.getCurrentUser();
            
            // Merangkai parameter pengguna menjadi susunan teks vertikal
            String info = "ID/NIM: " + mhs.getIdUser() + "\nNama: " + mhs.getNama() + 
                          "\nEmail: " + mhs.getEmail() + "\nRole: " + mhs.getRole() + 
                          "\nNIM: " + mhs.getNim();
            com.mankelfas.util.DialogHelper.showInfoDialog("Profil Mahasiswa", "Informasi Akun", info);
        }
    }

    /**
     * Menyediakan sarana bagi mahasiswa untuk melihat pratinjau daftar fasilitas secara umum.
     */
    @FXML
    private void bukaFasilitas() {
        try {
            // Memanggil UI terpisah yang berfungsi khusus menampilkan direktori fasilitas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/fasilitas_view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Data Fasilitas");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat fasilitas view: " + e.getMessage());
            alert.show();
        }
    }

    /**
     * Melakukan pelepasan sesi aktif saat ini dan mengembalikan layar pada halaman pendaftaran masuk.
     */
    @FXML
    private void logout() {
        try {
            // Memerintahkan pengelola rute UI utama untuk beralih layar
            com.mankelfas.App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
