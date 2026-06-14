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
import java.util.stream.Collectors;
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
    @FXML private javafx.scene.layout.BorderPane mainPane;
    @FXML private javafx.scene.layout.VBox dashboardContent;


    @FXML private ComboBox<String> comboLokasi;
    @FXML private ComboBox<String> comboFasilitas;
    @FXML private ComboBox<com.mankelfas.enumeration.KondisiFasilitas> comboKondisi;
    @FXML private TextArea inputDeskripsi;
    @FXML private ImageView imagePreview;
    @FXML private javafx.scene.control.Label lblPlaceholder;
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
        tabelKeluhan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        com.mankelfas.util.Navigator.registerMainPane(mainPane, dashboardContent);

        // Menampilkan pesan sapaan khusus menggunakan nama pengguna aktif
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        try {
            // Memulai interaksi dengan layanan inti untuk menarik data keluhan dan fasilitas
            keluhanService = KeluhanService.getInstance();
            fasilitasList = keluhanService.getAllFasilitas();

            // Menyusun daftar lokasi unik
            java.util.List<String> lokasis = fasilitasList.stream().map(Fasilitas::getLokasi).distinct().collect(Collectors.toList());
            comboLokasi.getItems().addAll(lokasis);
            
            // Ketika lokasi dipilih, saring fasilitas
            comboLokasi.setOnAction(e -> {
                comboFasilitas.getItems().clear();
                String loc = comboLokasi.getValue();
                for (Fasilitas f : fasilitasList) {
                    if (loc == null || f.getLokasi().equals(loc)) {
                        comboFasilitas.getItems().add(f.getInfo());
                    }
                }
            });

            for (Fasilitas f : fasilitasList) {
                comboFasilitas.getItems().add(f.getInfo());
            }

            comboKondisi.getItems().addAll(
                com.mankelfas.enumeration.KondisiFasilitas.RUSAK_RINGAN,
                com.mankelfas.enumeration.KondisiFasilitas.RUSAK_PARAH
            );

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
    private void clearLokasi() {
        comboLokasi.getSelectionModel().clearSelection();
        comboFasilitas.getItems().clear();
        for (Fasilitas f : fasilitasList) {
            comboFasilitas.getItems().add(f.getInfo());
        }
    }

    @FXML
    private void clearFasilitas() {
        comboFasilitas.getSelectionModel().clearSelection();
    }

    @FXML
    private void clearKondisi() {
        comboKondisi.getSelectionModel().clearSelection();
    }

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
        String selectedFasilitasInfo = comboFasilitas.getValue();
        com.mankelfas.enumeration.KondisiFasilitas selectedKondisi = comboKondisi.getValue();
        String deskripsi = inputDeskripsi.getText();

        if (selectedFasilitasInfo != null && selectedKondisi != null && deskripsi != null && !deskripsi.trim().isEmpty()) {
            try {
                Fasilitas f = fasilitasList.stream().filter(fas -> fas.getInfo().equals(selectedFasilitasInfo)).findFirst().orElse(null);
                if (f == null) throw new RuntimeException("Fasilitas tidak valid");
                
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
                
                // Update kondisi fasilitas di database
                f.setKondisi(selectedKondisi);
                keluhanService.updateFasilitas(f);
                
                keluhanService.addKeluhan(k);
                
                // Memasukkan tambahan data ke tabel tanpa memuat ulang layar
                keluhanObservableList.add(k);
                tabelKeluhan.refresh();
                
                // Membersihkan UI form setelah pengiriman berhasil
                inputDeskripsi.clear();
                comboFasilitas.getSelectionModel().clearSelection();
                comboKondisi.getSelectionModel().clearSelection();
                selectedFile = null;
                imagePreview.setImage(null);
                if (lblPlaceholder != null) lblPlaceholder.setVisible(true);

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
            alert.setContentText("Pilih fasilitas, kondisi, dan isi deskripsi!");
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
     * Memuat dan menampilkan antarmuka khusus Profil Pengguna.
     */
    @FXML
    private void lihatInfoProfil() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mankelfas/view/profil_view.fxml"));
            javafx.scene.Parent root = loader.load();
            
            ProfilController controller = loader.getController();
            controller.setUserData(com.mankelfas.util.Session.getCurrentUser());
            
            com.mankelfas.util.Navigator.navigate(root);
        } catch (Exception e) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Error", "Gagal memuat UI profil: " + e.getMessage());
        }
    }

    /**
     * Membalikkan warna tema aplikasi antara Terang dan Gelap.
     */
    @FXML
    private void toggleMode(javafx.event.ActionEvent event) {
        com.mankelfas.util.ThemeManager.toggleTheme();
        
        // Memuat ulang tema pada jendela (Scene) yang sedang aktif
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        com.mankelfas.util.ThemeManager.applyTheme(btn.getScene());
        
        // Memperbarui ikon matahari/bulan pada tombol
        if (com.mankelfas.util.ThemeManager.isDarkMode()) {
            btn.setText("☽");
        } else {
            btn.setText("☼");
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
            com.mankelfas.util.Navigator.navigate(root);
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
    private void kembaliKeBeranda() {
        com.mankelfas.util.Navigator.goHome();
    }

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
