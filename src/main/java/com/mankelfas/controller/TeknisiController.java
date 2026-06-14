package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.service.KeluhanService;
import com.mankelfas.model.misc.Komentar;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;
import javafx.scene.control.TextInputDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.mankelfas.config.DatabaseConnection;

/**
 * Mengelola UI khusus untuk pengguna berstatus Teknisi.
 * Menampilkan daftar tugas perbaikan, menangani pembaruan status pengerjaan, serta mencatat estimasi waktu penyelesaian.
 */
public class TeknisiController {

    @FXML private TableView<Keluhan> tabelKeluhan;
    @FXML private TableColumn<Keluhan, Integer> colId;
    @FXML private TableColumn<Keluhan, String> colPelapor;
    @FXML private TableColumn<Keluhan, String> colFasilitas;
    @FXML private TableColumn<Keluhan, String> colDeskripsi;
    @FXML private TableColumn<Keluhan, String> colStatus;
    
    @FXML private TextArea inputKomentar;
    @FXML private ImageView imagePreview;
    @FXML private TextField inputEstimasiAngka;
    @FXML private ComboBox<String> comboEstimasiSatuan;
    
    private File selectedFile;
    @FXML private Label lblWelcome;

    private KeluhanService keluhanService;
    private ObservableList<Keluhan> keluhanObservableList;
    private com.mankelfas.model.user.Teknisi activeTeknisi;

    /**
     * Mempersiapkan layout awal layar dasbor untuk teknisi.
     * Mengikat tabel dengan daftar tugas dan mengaktifkan pengingat latar belakang otomatis.
     */
    @FXML
    public void initialize() {
        // Menampilkan sapaan identitas pengguna yang sedang beroperasi
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        // Mengambil referensi instansi pengelola data keluhan
        keluhanService = KeluhanService.getInstance();
        
        // Menyiapkan data teknisi aktif sebagai referensi untuk penugasan keluhan
        activeTeknisi = new com.mankelfas.model.user.Teknisi(2, "Budi Teknisi", "budi@tek.com", "123", "Elektronik");

        // Memetakan struktur kolom pada tabel agar merepresentasikan properti objek keluhan
        colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colPelapor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelapor().getNama()));
        colFasilitas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFasilitas().getNama()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        // Menampilkan secara eksklusif keluhan yang telah ditugaskan kepada seorang teknisi
        keluhanObservableList = FXCollections.observableArrayList(
            keluhanService.getAllKeluhan().stream()
                .filter(k -> k.getTeknisi() != null)
                .collect(Collectors.toList())
        );
        tabelKeluhan.setItems(keluhanObservableList);
        
        // Memicu pengawas latar belakang untuk menghitung waktu pengerjaan
        startReminderBackgroundWorker();
    }

    /**
     * Membangkitkan proses pekerja (worker) yang berjalan diam-diam di belakang layar.
     * Tugas utamanya adalah memantau tenggat waktu perbaikan secara berkala.
     */
    private void startReminderBackgroundWorker() {
        // Menggunakan timer daemon agar tidak memblokir penutupan aplikasi utama
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Memastikan pembaruan UI tetap tereksekusi pada thread JavaFX yang benar
                Platform.runLater(() -> checkOverdueKeluhan());
            }
        }, 60000, 60000); // 1 minute interval
    }

    /**
     * Melakukan penyisiran terhadap daftar keluhan yang sedang dikerjakan.
     * Mencari keluhan mana saja yang waktu pengerjaannya telah melampaui batas target.
     */
    private void checkOverdueKeluhan() {
        // Menangkap titik waktu saat fungsi ini dijalankan
        LocalDateTime now = LocalDateTime.now();
        // Memeriksa satu per satu status keluhan di daftar tugas
        for (Keluhan k : keluhanObservableList) {
            // Memastikan keluhan sedang berada di fase pengerjaan aktif dan memiliki target
            if (k.getStatus() == StatusKeluhan.DIPROSES && k.getTargetSelesai() != null) {
                // Memicu notifikasi darurat jika durasi melebihi batas
                if (now.isAfter(k.getTargetSelesai())) {
                    showOverdueAlert(k);
                }
            }
        }
    }

    /**
     * Menampilkan kotak pesan darurat ketika teknisi gagal menyelesaikan perbaikan tepat waktu.
     * Menawarkan opsi jalur cepat untuk langsung melaporkan alasan keterlambatan.
     * 
     * @param k Data keluhan yang mengalami keterlambatan
     */
    private void showOverdueAlert(Keluhan k) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan Darurat");
        alert.setHeaderText("Waktu Habis!");
        alert.setContentText("⚠️ KRITIS: Tenggat waktu pengerjaan Keluhan #" + k.getIdKeluhan() + 
                             " (Lokasi: " + k.getFasilitas().getLokasi() + ") telah terlampaui! Segera selesaikan tugas ini atau laporkan kendala ke Admin.");

        // Menyematkan tombol tambahan khusus untuk pelaporan kendala cepat
        javafx.scene.control.ButtonType btnLapor = new javafx.scene.control.ButtonType("Laporkan Kendala");
        alert.getButtonTypes().addAll(btnLapor);
        
        // Memantau respons teknisi terhadap kotak peringatan tersebut
        alert.showAndWait().ifPresent(type -> {
            if (type == btnLapor) {
                tampilkanFormKendala(k);
            }
        });
    }

    /**
     * Memunculkan formulir isian singkat agar teknisi bisa menuliskan rintangan perbaikan.
     * 
     * @param k Objek keluhan yang sedang menemui rintangan
     */
    private void tampilkanFormKendala(Keluhan k) {
        // Menyajikan kotak dialog masukan teks satu baris (TextInputDialog)
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lapor Kendala");
        dialog.setHeaderText("Keluhan #" + k.getIdKeluhan());
        dialog.setContentText("Alasan Kendala:");
        
        // Mengeksekusi rekam jejak kendala jika teknisi menekan OK dan memasukkan teks
        dialog.showAndWait().ifPresent(alasan -> {
            simpanKendalaKeDB(k.getIdKeluhan(), alasan);
            com.mankelfas.util.DialogHelper.showInfoDialog("Terkirim", "Kendala Dilaporkan", "Admin telah menerima laporan kendala Anda.");
        });
    }

    /**
     * Mengeksekusi penulisan catatan kendala baru langsung ke dalam database server.
     * 
     * @param idKeluhan ID keluhan yang bermasalah
     * @param alasan Deskripsi singkat rintangan yang dihadapi
     */
    private void simpanKendalaKeDB(int idKeluhan, String alasan) {
        // Merangkai instruksi injeksi data mentah menggunakan pernyataan aman (PreparedStatement)
        String sql = "INSERT INTO kendala_teknisi (id_keluhan, id_teknisi, alasan_kendala) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Melengkapi argumen variabel untuk mencegah kerentanan SQL Injection
            stmt.setInt(1, idKeluhan);
            stmt.setInt(2, activeTeknisi.getIdUser());
            stmt.setString(3, alasan);
            // Mengeksekusi dorongan perubahan
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengubah status operasional keluhan menjadi fase "DIPROSES" atau sedang dikerjakan.
     * Menghitung dan menetapkan target waktu penyelesaian berdasarkan estimasi teknisi.
     */
    @FXML
    private void updateDiproses() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        // Memastikan teknisi telah memilih baris target pekerjaan yang valid
        if (selectedKeluhan != null) {
            String angkaStr = inputEstimasiAngka.getText();
            String satuan = comboEstimasiSatuan.getValue();
            
            // Mengecek apakah komponen estimasi waktu telah diisi lengkap
            if (angkaStr != null && !angkaStr.isEmpty() && satuan != null) {
                try {
                    int angka = Integer.parseInt(angkaStr);
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime target = now;
                    
                    // Menambahkan penanda waktu spesifik berdasarkan skala pilihan
                    if ("Menit".equals(satuan)) target = target.plusMinutes(angka);
                    else if ("Jam".equals(satuan)) target = target.plusHours(angka);
                    else if ("Hari".equals(satuan)) target = target.plusDays(angka);
                    
                    // Mengabadikan tenggat waktu operasi ke dalam memori objek keluhan
                    selectedKeluhan.setEstimasiWaktu(angka + " " + satuan);
                    selectedKeluhan.setWaktuDiproses(now);
                    selectedKeluhan.setTargetSelesai(target);
                } catch (NumberFormatException e) {
                    // Memberikan sanksi teguran bila pengguna salah menginput bilangan teks
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Angka estimasi tidak valid.");
                    alert.show();
                    return;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Harap isi angka dan satuan estimasi waktu sebelum memproses.");
                alert.show();
                return;
            }
        }
        // Melanjutkan proses formal perubahan status setelah estimasi disahkan
        updateStatus(StatusKeluhan.DIPROSES);
    }

    /**
     * Mengganti tanda status keluhan dari tahap pengerjaan menjadi status penyelesaian akhir.
     */
    @FXML
    private void updateSelesai() {
        updateStatus(StatusKeluhan.SELESAI);
    }

    /**
     * Membuka sarana telusur file lokal agar teknisi bisa membubuhkan foto hasil perbaikan.
     */
    @FXML
    private void pilihFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        
        // Menampilkan pop-up dialog sistem operasi pemilih direktori file
        File file = fileChooser.showOpenDialog(tabelKeluhan.getScene().getWindow());
        if (file != null) {
            // Mengambil file terpilih lalu menampilkannya sebagai gambar contoh pratinjau mini
            selectedFile = file;
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Fungsi sentral untuk menerapkan peralihan status keluhan baru, lengkap dengan komentar dan bukti foto.
     * 
     * @param status Konstanta status tujuannya (contoh: SELESAI)
     */
    private void updateStatus(StatusKeluhan status) {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        String komentarTxt = inputKomentar.getText();
        
        // Mencegah insiden interaksi kosong bila tidak ada baris tabel disorot
        if (selectedKeluhan != null) {
            // Mendorong status baru kepada entitas
            selectedKeluhan.updateStatus(status);
            
            String webpFilePath = null;
            // Jika teknisi menitipkan unggahan bukti, maka kerjakan translasi datanya
            if (selectedFile != null) {
                try {
                    // Menjamin struktur direktori tampungan arsip terbentuk
                    File uploadDir = new File("uploads");
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    // Meracik kerangka identitas unik file berdasarkan urutan kejadian milidetik
                    String newFileName = "bukti_" + System.currentTimeMillis() + ".webp";
                    File outputFile = new File(uploadDir, newFileName);
                    
                    // Merekonstruksi memori gambar agar berformat WEBP demi mengirit porsi piringan cakram
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {
                        ImageIO.write(image, "webp", outputFile);
                        webpFilePath = outputFile.getAbsolutePath();
                    }
                } catch (IOException e) {
                    System.err.println("Gagal menyimpan foto bukti: " + e.getMessage());
                }
            }

            // Menyisipkan catatan dan jejak foto ke koleksi rekam keluhan
            if(komentarTxt != null && !komentarTxt.trim().isEmpty() || webpFilePath != null){
                selectedKeluhan.tambahKomentar(new Komentar(komentarTxt != null ? komentarTxt : "", activeTeknisi, webpFilePath));
            }
            
            // Membersihkan UI bekas pakai dan memperbarui UI utama tabel
            tabelKeluhan.refresh();
            inputKomentar.clear();
            selectedFile = null;
            imagePreview.setImage(null);
            
            // Memberi tahu teknisi bahwa laporannya aman tercatat
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Status keluhan diperbarui menjadi " + status.name());
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Mengamankan sesi operasi teknisi ini dengan mengeluarkannya menuju pintu gerbang identifikasi utama.
     */
    @FXML
    private void logout() {
        try {
            // Melontarkan perintah untuk beralih layout ke jendela registrasi masuk
            com.mankelfas.App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
