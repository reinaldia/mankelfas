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
 */
public class TeknisiController {
    @FXML private javafx.scene.layout.BorderPane mainPane;
    @FXML private javafx.scene.layout.VBox dashboardContent;

    @FXML private TableView<Keluhan> tabelKeluhan;
    @FXML private TableColumn<Keluhan, Integer> colId;
    @FXML private TableColumn<Keluhan, String> colPelapor;
    @FXML private TableColumn<Keluhan, String> colFasilitas;
    @FXML private TableColumn<Keluhan, String> colDeskripsi;
    @FXML private TableColumn<Keluhan, String> colPrioritas;
    @FXML private TableColumn<Keluhan, String> colStatus;
    
    @FXML private Label lblWelcome;

    private KeluhanService keluhanService;
    private ObservableList<Keluhan> keluhanObservableList;
    private com.mankelfas.model.user.Teknisi activeTeknisi;

    @FXML
    public void initialize() {
        tabelKeluhan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        com.mankelfas.util.Navigator.registerMainPane(mainPane, dashboardContent);

        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        keluhanService = KeluhanService.getInstance();
        
        com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
        if (currentUser instanceof com.mankelfas.model.user.Teknisi) {
            activeTeknisi = (com.mankelfas.model.user.Teknisi) currentUser;
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colPelapor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelapor().getNama()));
        colFasilitas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFasilitas().getNama()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        colPrioritas.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrioritas().name()));
        colPrioritas.setCellFactory(column -> new javafx.scene.control.TableCell<Keluhan, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label chip = new javafx.scene.control.Label(item);
                    chip.getStyleClass().add("chip");
                    chip.getStyleClass().add("chip-" + item.toLowerCase());
                    setGraphic(chip);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

        keluhanObservableList = FXCollections.observableArrayList(
            keluhanService.getAllKeluhan().stream()
                .filter(k -> k.getTeknisi() != null && activeTeknisi != null && k.getTeknisi().getIdUser() == activeTeknisi.getIdUser())
                .filter(k -> !k.isArchived() && k.getStatus() != StatusKeluhan.DITOLAK && k.getStatus() != StatusKeluhan.SELESAI)
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
        }, 60000, 60000); 
    }

    private void checkOverdueKeluhan() {
        LocalDateTime now = LocalDateTime.now();
        for (Keluhan k : keluhanObservableList) {
            if (k.getStatus() == StatusKeluhan.DIPROSES && k.getTargetSelesai() != null) {
                if (now.isAfter(k.getTargetSelesai()) && k.getTeknisi() != null && activeTeknisi != null && k.getTeknisi().getIdUser() == activeTeknisi.getIdUser()) {
                    // Logic to show warning or something
                }
            }
        }
    }

    @FXML
    private void lihatDetail() {
        Keluhan selected = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Peringatan", "Silakan pilih keluhan dari tabel terlebih dahulu.");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mankelfas/view/keluhan_detail.fxml"));
            javafx.scene.Parent root = loader.load();
            
            KeluhanDetailController controller = loader.getController();
            controller.setKeluhanData(selected);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Detail Keluhan & Fasilitas");
            stage.setScene(new javafx.scene.Scene(root));
            com.mankelfas.util.ThemeManager.applyTheme(stage.getScene());
            stage.showAndWait();
        } catch (Exception e) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Gagal Memuat UI", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    @FXML
    private void updateDiproses() {
        Keluhan selected = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Peringatan", "Silakan pilih keluhan dari tabel terlebih dahulu.");
            return;
        }
        
        if (selected.getStatus() != StatusKeluhan.DITUGASKAN) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Peringatan", "Hanya keluhan dengan status DITUGASKAN yang dapat diproses.");
            return;
        }

        javafx.scene.control.Dialog<String[]> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Proses Keluhan");
        dialog.setHeaderText("Mulai Pemrosesan Keluhan");

        javafx.scene.control.DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);


        javafx.scene.layout.VBox leftPane = new javafx.scene.layout.VBox(10);
        leftPane.setPrefWidth(300);
        
        // Info Keluhan
        javafx.scene.layout.VBox boxKeluhan = new javafx.scene.layout.VBox(5);
        boxKeluhan.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");
        Label lblInfoKeluhan = new Label("Informasi Keluhan");
        lblInfoKeluhan.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblId = new Label("ID: " + selected.getIdKeluhan());
        Label lblStatus = new Label("Status: " + selected.getStatus());
        Label lblPrioritas = new Label("Prioritas: " + selected.getPrioritas());
        Label lblPelapor = new Label("Pelapor: " + selected.getPelapor().getNama());
        Label lblDeskripsi = new Label("Deskripsi: " + selected.getDeskripsi());
        lblDeskripsi.setWrapText(true);
        boxKeluhan.getChildren().addAll(lblInfoKeluhan, lblId, lblStatus, lblPrioritas, lblPelapor, lblDeskripsi);
        
        // Info Fasilitas
        javafx.scene.layout.VBox boxFasilitas = new javafx.scene.layout.VBox(5);
        boxFasilitas.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");
        Label lblInfoFasilitas = new Label("Informasi Fasilitas");
        lblInfoFasilitas.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblNama = new Label("Nama: " + selected.getFasilitas().getNama());
        Label lblLokasi = new Label("Lokasi: " + selected.getFasilitas().getLokasi());
        Label lblKategori = new Label("Kategori: " + selected.getFasilitas().getKategori());
        Label lblKondisi = new Label("Kondisi Terakhir: " + selected.getFasilitas().getKondisi());
        boxFasilitas.getChildren().addAll(lblInfoFasilitas, lblNama, lblLokasi, lblKategori, lblKondisi);
        
        // Foto Bukti
        javafx.scene.layout.VBox boxFoto = new javafx.scene.layout.VBox(5);
        boxFoto.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");
        Label lblFoto = new Label("Foto Bukti Keluhan");
        lblFoto.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        javafx.scene.layout.StackPane fotoPane = new javafx.scene.layout.StackPane();
        fotoPane.setPrefHeight(150);
        fotoPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        
        ImageView imgBukti = new ImageView();
        imgBukti.setFitWidth(280);
        imgBukti.setFitHeight(140);
        imgBukti.setPreserveRatio(true);
        Label lblNoFoto = new Label("Tidak ada foto bukti.");
        lblNoFoto.setTextFill(javafx.scene.paint.Color.GRAY);
        
        if (selected.getFotoBukti() != null && !selected.getFotoBukti().isEmpty()) {
            try {
                File fileFoto = new File(selected.getFotoBukti());
                if (!fileFoto.exists()) {
                    fileFoto = new File(selected.getFotoBukti().replace("file:", ""));
                }
                if (fileFoto.exists()) {
                    BufferedImage bufferedImage = ImageIO.read(fileFoto);
                    if (bufferedImage != null) {
                        Image fxImage = javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
                        imgBukti.setImage(fxImage);
                        lblNoFoto.setVisible(false);
                    } else {
                        lblNoFoto.setText("Gagal decode foto WebP.");
                    }
                } else {
                    lblNoFoto.setText("File foto tidak ditemukan.");
                }
            } catch (Exception e) {
                lblNoFoto.setText("Error: " + e.getMessage());
            }
        }
        fotoPane.getChildren().addAll(lblNoFoto, imgBukti);
        boxFoto.getChildren().addAll(lblFoto, fotoPane);
        
        leftPane.getChildren().addAll(boxKeluhan, boxFasilitas, boxFoto);


        // Right Pane
        javafx.scene.layout.VBox rightPane = new javafx.scene.layout.VBox(10);
        rightPane.setPrefWidth(300);
        Label lblInput = new Label("Berapa estimasi waktu pengerjaan?");
        lblInput.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        TextField fieldAngka = new TextField();
        fieldAngka.setPromptText("Angka (Contoh: 3)");
        ComboBox<String> comboSatuan = new ComboBox<>(FXCollections.observableArrayList("Menit", "Jam", "Hari"));
        comboSatuan.setPromptText("Satuan");
        
        javafx.scene.layout.HBox hboxInput = new javafx.scene.layout.HBox(10, fieldAngka, comboSatuan);
        rightPane.getChildren().addAll(lblInput, hboxInput);
        
        javafx.scene.layout.HBox mainContainer = new javafx.scene.layout.HBox(20, leftPane, rightPane);
        dialogPane.setContent(mainContainer);

        dialog.setResultConverter((javafx.scene.control.ButtonType button) -> {
            if (button == javafx.scene.control.ButtonType.OK) {
                return new String[]{fieldAngka.getText(), comboSatuan.getValue()};
            }
            return null;
        });
        
        com.mankelfas.util.ThemeManager.applyTheme(dialog.getDialogPane());
        java.util.Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(arr -> {
            try {
                int estimasiVal = Integer.parseInt(arr[0]);
                String satuan = arr[1];
                if (satuan == null || satuan.isEmpty()) throw new Exception("Pilih satuan waktu");
                
                selected.updateStatus(StatusKeluhan.DIPROSES);
                com.mankelfas.enumeration.KondisiFasilitas k = selected.getFasilitas().getKondisi();
                selected.updateKondisiFasilitas(k.toDalamPerbaikan());
                keluhanService.updateFasilitas(selected.getFasilitas());
                
                selected.setEstimasiWaktu(estimasiVal + " " + satuan);
                
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                selected.setWaktuDiproses(now);
                
                if (satuan.equalsIgnoreCase("Menit")) {
                    selected.setTargetSelesai(now.plusMinutes(estimasiVal));
                } else if (satuan.equalsIgnoreCase("Jam")) {
                    selected.setTargetSelesai(now.plusHours(estimasiVal));
                } else if (satuan.equalsIgnoreCase("Hari")) {
                    selected.setTargetSelesai(now.plusDays(estimasiVal));
                }
                
                keluhanService.updateKeluhan(selected);
                tabelKeluhan.refresh();
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Status Diperbarui", "Keluhan telah diproses dengan estimasi.");
            } catch (Exception e) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Gagal", "Format tidak valid atau proses gagal: " + e.getMessage());
            }
        });
    }
    @FXML
    private void updateSelesai() {
        Keluhan selected = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Peringatan", "Silakan pilih keluhan dari tabel terlebih dahulu.");
            return;
        }
        
        if (selected.getStatus() != StatusKeluhan.DIPROSES) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Peringatan", "Hanya keluhan dengan status DIPROSES yang dapat diselesaikan.");
            return;
        }

        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Selesaikan Keluhan");
        dialog.setHeaderText("Laporan Penyelesaian Perbaikan");

        javafx.scene.control.DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);


        javafx.scene.layout.VBox leftPane = new javafx.scene.layout.VBox(10);
        leftPane.setPrefWidth(300);
        
        // Info Keluhan
        javafx.scene.layout.VBox boxKeluhan = new javafx.scene.layout.VBox(5);
        boxKeluhan.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");
        Label lblInfoKeluhan = new Label("Informasi Keluhan");
        lblInfoKeluhan.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblId = new Label("ID: " + selected.getIdKeluhan());
        Label lblStatus = new Label("Status: " + selected.getStatus());
        Label lblPrioritas = new Label("Prioritas: " + selected.getPrioritas());
        Label lblPelapor = new Label("Pelapor: " + selected.getPelapor().getNama());
        Label lblDeskripsi = new Label("Deskripsi: " + selected.getDeskripsi());
        lblDeskripsi.setWrapText(true);
        boxKeluhan.getChildren().addAll(lblInfoKeluhan, lblId, lblStatus, lblPrioritas, lblPelapor, lblDeskripsi);
        
        // Info Fasilitas
        javafx.scene.layout.VBox boxFasilitas = new javafx.scene.layout.VBox(5);
        boxFasilitas.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");
        Label lblInfoFasilitas = new Label("Informasi Fasilitas");
        lblInfoFasilitas.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblNama = new Label("Nama: " + selected.getFasilitas().getNama());
        Label lblLokasi = new Label("Lokasi: " + selected.getFasilitas().getLokasi());
        Label lblKategori = new Label("Kategori: " + selected.getFasilitas().getKategori());
        Label lblKondisi = new Label("Kondisi Terakhir: " + selected.getFasilitas().getKondisi());
        boxFasilitas.getChildren().addAll(lblInfoFasilitas, lblNama, lblLokasi, lblKategori, lblKondisi);
        
        // Foto Bukti
        javafx.scene.layout.VBox boxFoto = new javafx.scene.layout.VBox(5);
        boxFoto.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");
        Label lblFoto = new Label("Foto Bukti Keluhan");
        lblFoto.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        javafx.scene.layout.StackPane fotoPane = new javafx.scene.layout.StackPane();
        fotoPane.setPrefHeight(150);
        fotoPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        
        ImageView imgBukti = new ImageView();
        imgBukti.setFitWidth(280);
        imgBukti.setFitHeight(140);
        imgBukti.setPreserveRatio(true);
        Label lblNoFoto = new Label("Tidak ada foto bukti.");
        lblNoFoto.setTextFill(javafx.scene.paint.Color.GRAY);
        
        if (selected.getFotoBukti() != null && !selected.getFotoBukti().isEmpty()) {
            try {
                File fileFoto = new File(selected.getFotoBukti());
                if (!fileFoto.exists()) {
                    fileFoto = new File(selected.getFotoBukti().replace("file:", ""));
                }
                if (fileFoto.exists()) {
                    BufferedImage bufferedImage = ImageIO.read(fileFoto);
                    if (bufferedImage != null) {
                        Image fxImage = javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
                        imgBukti.setImage(fxImage);
                        lblNoFoto.setVisible(false);
                    } else {
                        lblNoFoto.setText("Gagal decode foto WebP.");
                    }
                } else {
                    lblNoFoto.setText("File foto tidak ditemukan.");
                }
            } catch (Exception e) {
                lblNoFoto.setText("Error: " + e.getMessage());
            }
        }
        fotoPane.getChildren().addAll(lblNoFoto, imgBukti);
        boxFoto.getChildren().addAll(lblFoto, fotoPane);
        
        leftPane.getChildren().addAll(boxKeluhan, boxFasilitas, boxFoto);


        // Right Pane
        javafx.scene.layout.VBox rightPane = new javafx.scene.layout.VBox(10);
        rightPane.setPrefWidth(300);
        
        Label lblMasalah = new Label("Permasalahan Sebenarnya");
        lblMasalah.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea txtKomentar = new TextArea();
        txtKomentar.setPromptText("Tuliskan permasalahan sebenarnya yang telah diperbaiki...");
        txtKomentar.setPrefHeight(100);
        txtKomentar.setWrapText(true);
        
        // ComboKondisi dihapus karena secara otomatis menjadi BERFUNGSI_BAIK        
        Label lblFotoPenyelesaian = new Label("Foto Penyelesaian (Opsional)");
        lblFotoPenyelesaian.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        javafx.scene.layout.StackPane fotoSelesaiPane = new javafx.scene.layout.StackPane();
        fotoSelesaiPane.setPrefHeight(120);
        fotoSelesaiPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-style: dashed;");
        
        ImageView imgView = new ImageView();
        imgView.setFitWidth(280);
        imgView.setFitHeight(110);
        imgView.setPreserveRatio(true);
        
        Label lblPlaceholderFoto = new Label("Belum ada foto penyelesaian");
        lblPlaceholderFoto.setTextFill(javafx.scene.paint.Color.GRAY);
        
        fotoSelesaiPane.getChildren().addAll(lblPlaceholderFoto, imgView);
        
        javafx.scene.control.Button btnPilih = new javafx.scene.control.Button("Pilih Foto Bukti");
        btnPilih.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-font-weight: bold;");

        final java.io.File[] chosenFile = {null};

        btnPilih.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp"));
            java.io.File f = chooser.showOpenDialog(dialogPane.getScene().getWindow());
            if (f != null) {
                chosenFile[0] = f;
                imgView.setImage(new Image(f.toURI().toString()));
                lblPlaceholderFoto.setVisible(false);
            }
        });
        
        rightPane.getChildren().addAll(lblMasalah, txtKomentar, lblFotoPenyelesaian, fotoSelesaiPane, btnPilih);
        
        javafx.scene.layout.HBox mainContainer = new javafx.scene.layout.HBox(20, leftPane, rightPane);
        dialogPane.setContent(mainContainer);

        dialog.setResultConverter(btn -> {
            if (btn == javafx.scene.control.ButtonType.OK) {
                try {
                    String imgPath = null;
                    if (chosenFile[0] != null) {
                        java.io.File dir = new java.io.File("uploads");
                        if (!dir.exists()) dir.mkdir();
                        String fName = "bukti_selesai_" + System.currentTimeMillis() + ".webp";
                        java.io.File dest = new java.io.File(dir, fName);
                        BufferedImage bi = ImageIO.read(chosenFile[0]);
                        if (bi != null) {
                            ImageIO.write(bi, "webp", dest);
                            imgPath = "uploads/" + fName;
                        }
                    }

                    selected.updateStatus(StatusKeluhan.SELESAI);
                    Komentar kom = new Komentar(txtKomentar.getText(), com.mankelfas.util.Session.getCurrentUser(), imgPath);
                    selected.tambahKomentar(kom);
                    
                    selected.updateKondisiFasilitas(com.mankelfas.enumeration.KondisiFasilitas.BERFUNGSI_BAIK);
                    keluhanService.updateFasilitas(selected.getFasilitas());
                    
                    keluhanService.updateKeluhan(selected);
                    tabelKeluhan.refresh();
                    com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Perbaikan Selesai", "Data keluhan telah diperbarui menjadi SELESAI.");
                } catch (Exception ex) {
                    com.mankelfas.util.DialogHelper.showErrorDialog("Gagal", "Gagal menyimpan penyelesaian: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
    @FXML
    private void kembaliKeBeranda() {
        com.mankelfas.util.Navigator.goHome();
    }

    @FXML
    private void logout() {
        try {
            com.mankelfas.App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void toggleMode(javafx.event.ActionEvent event) {
        com.mankelfas.util.ThemeManager.toggleTheme();
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        com.mankelfas.util.ThemeManager.applyTheme(btn.getScene());
        if (com.mankelfas.util.ThemeManager.isDarkMode()) {
            btn.setText("☽");
        } else {
            btn.setText("☼");
        }
    }
}
