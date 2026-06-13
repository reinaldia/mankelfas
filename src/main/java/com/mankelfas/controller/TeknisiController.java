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

    @FXML
    public void initialize() {
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        keluhanService = KeluhanService.getInstance();
        
        // Dummy active teknisi to match our assignments
        activeTeknisi = new com.mankelfas.model.user.Teknisi(2, "Budi Teknisi", "budi@tek.com", "123", "Elektronik");

        colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colPelapor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelapor().getNama()));
        colFasilitas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFasilitas().getNama()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        // Display only complaints assigned to some teknisi (simulating task assignment)
        keluhanObservableList = FXCollections.observableArrayList(
            keluhanService.getAllKeluhan().stream()
                .filter(k -> k.getTeknisi() != null)
                .collect(Collectors.toList())
        );
        tabelKeluhan.setItems(keluhanObservableList);
        
        startReminderBackgroundWorker();
    }

    private void startReminderBackgroundWorker() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> checkOverdueKeluhan());
            }
        }, 60000, 60000); // 1 minute interval
    }

    private void checkOverdueKeluhan() {
        LocalDateTime now = LocalDateTime.now();
        for (Keluhan k : keluhanObservableList) {
            if (k.getStatus() == StatusKeluhan.DIPROSES && k.getTargetSelesai() != null) {
                if (now.isAfter(k.getTargetSelesai())) {
                    showOverdueAlert(k);
                }
            }
        }
    }

    private void showOverdueAlert(Keluhan k) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan Darurat");
        alert.setHeaderText("Waktu Habis!");
        alert.setContentText("⚠️ KRITIS: Tenggat waktu pengerjaan Keluhan #" + k.getIdKeluhan() + 
                             " (Lokasi: " + k.getFasilitas().getLokasi() + ") telah terlampaui! Segera selesaikan tugas ini atau laporkan kendala ke Admin.");

        javafx.scene.control.ButtonType btnLapor = new javafx.scene.control.ButtonType("Laporkan Kendala");
        alert.getButtonTypes().addAll(btnLapor);
        
        alert.showAndWait().ifPresent(type -> {
            if (type == btnLapor) {
                tampilkanFormKendala(k);
            }
        });
    }

    private void tampilkanFormKendala(Keluhan k) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lapor Kendala");
        dialog.setHeaderText("Keluhan #" + k.getIdKeluhan());
        dialog.setContentText("Alasan Kendala:");
        dialog.showAndWait().ifPresent(alasan -> {
            simpanKendalaKeDB(k.getIdKeluhan(), alasan);
            com.mankelfas.util.DialogHelper.showInfoDialog("Terkirim", "Kendala Dilaporkan", "Admin telah menerima laporan kendala Anda.");
        });
    }

    private void simpanKendalaKeDB(int idKeluhan, String alasan) {
        String sql = "INSERT INTO kendala_teknisi (id_keluhan, id_teknisi, alasan_kendala) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idKeluhan);
            stmt.setInt(2, activeTeknisi.getIdUser());
            stmt.setString(3, alasan);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateDiproses() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selectedKeluhan != null) {
            String angkaStr = inputEstimasiAngka.getText();
            String satuan = comboEstimasiSatuan.getValue();
            
            if (angkaStr != null && !angkaStr.isEmpty() && satuan != null) {
                try {
                    int angka = Integer.parseInt(angkaStr);
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime target = now;
                    if ("Menit".equals(satuan)) target = target.plusMinutes(angka);
                    else if ("Jam".equals(satuan)) target = target.plusHours(angka);
                    else if ("Hari".equals(satuan)) target = target.plusDays(angka);
                    
                    selectedKeluhan.setEstimasiWaktu(angka + " " + satuan);
                    selectedKeluhan.setWaktuDiproses(now);
                    selectedKeluhan.setTargetSelesai(target);
                } catch (NumberFormatException e) {
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
        updateStatus(StatusKeluhan.DIPROSES);
    }

    @FXML
    private void updateSelesai() {
        updateStatus(StatusKeluhan.SELESAI);
    }

    @FXML
    private void pilihFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        File file = fileChooser.showOpenDialog(tabelKeluhan.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private void updateStatus(StatusKeluhan status) {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        String komentarTxt = inputKomentar.getText();
        
        if (selectedKeluhan != null) {
            selectedKeluhan.updateStatus(status);
            
            String webpFilePath = null;
            if (selectedFile != null) {
                try {
                    File uploadDir = new File("uploads");
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    String newFileName = "bukti_" + System.currentTimeMillis() + ".webp";
                    File outputFile = new File(uploadDir, newFileName);
                    
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {
                        ImageIO.write(image, "webp", outputFile);
                        webpFilePath = outputFile.getAbsolutePath();
                    }
                } catch (IOException e) {
                    System.err.println("Gagal menyimpan foto bukti: " + e.getMessage());
                }
            }

            if(komentarTxt != null && !komentarTxt.trim().isEmpty() || webpFilePath != null){
                selectedKeluhan.tambahKomentar(new Komentar(komentarTxt != null ? komentarTxt : "", activeTeknisi, webpFilePath));
            }
            
            tabelKeluhan.refresh();
            inputKomentar.clear();
            selectedFile = null;
            imagePreview.setImage(null);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Status keluhan diperbarui menjadi " + status.name());
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
            alert.show();
        }
    }

    @FXML
    private void logout() {
        try {
            com.mankelfas.App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
