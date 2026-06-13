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

    @FXML
    public void initialize() {
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        try {
            keluhanService = KeluhanService.getInstance();
            fasilitasList = keluhanService.getAllFasilitas();

            for (Fasilitas f : fasilitasList) {
                comboFasilitas.getItems().add(f.getInfo());
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
            colDeskripsi.setCellValueFactory(cellData -> {
                Keluhan k = cellData.getValue();
                String desc = k.getDeskripsi();
                if (k.isArchived()) {
                    desc = "[DIARSIPKAN] " + desc;
                }
                return new SimpleStringProperty(desc);
            });
            colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
            colFasilitas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFasilitas().getInfo()));

            keluhanObservableList = FXCollections.observableArrayList(keluhanService.getAllKeluhan());
            tabelKeluhan.setItems(keluhanObservableList);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat data dashboard: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void pilihFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti Keluhan");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        File file = fileChooser.showOpenDialog(tabelKeluhan.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void kirimKeluhan() {
        int selectedIndex = comboFasilitas.getSelectionModel().getSelectedIndex();
        String deskripsi = inputDeskripsi.getText();

        if (selectedIndex >= 0 && deskripsi != null && !deskripsi.trim().isEmpty()) {
            try {
                Fasilitas f = fasilitasList.get(selectedIndex);
                
                com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
                if (!(currentUser instanceof com.mankelfas.model.user.Mahasiswa)) {
                    throw new RuntimeException("Sesi mahasiswa tidak valid!");
                }
                
                com.mankelfas.model.user.Mahasiswa mhs = (com.mankelfas.model.user.Mahasiswa) currentUser;
                
                String webpFilePath = null;
                if (selectedFile != null) {
                    File uploadDir = new File("uploads");
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    String newFileName = "keluhan_awal_" + System.currentTimeMillis() + ".webp";
                    File outputFile = new File(uploadDir, newFileName);
                    
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {
                        ImageIO.write(image, "webp", outputFile);
                        webpFilePath = outputFile.getAbsolutePath();
                    }
                }
                
                Keluhan k = new Keluhan(0, deskripsi, webpFilePath, mhs, f);
                keluhanService.addKeluhan(k);
                
                keluhanObservableList.add(k);
                tabelKeluhan.refresh();
                inputDeskripsi.clear();
                comboFasilitas.getSelectionModel().clearSelection();
                selectedFile = null;
                imagePreview.setImage(null);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Keluhan berhasil dikirim dan tersimpan di database!");
                alert.show();
            } catch (Exception e) {
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

    @FXML
    private void lihatDetail() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selectedKeluhan != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/keluhan_detail.fxml"));
                Parent root = loader.load();
                
                KeluhanDetailController controller = loader.getController();
                controller.setKeluhanData(selectedKeluhan);
                
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

    @FXML
    private void lihatInfoProfil() {
        if (com.mankelfas.util.Session.getCurrentUser() instanceof com.mankelfas.model.user.Mahasiswa) {
            com.mankelfas.model.user.Mahasiswa mhs = (com.mankelfas.model.user.Mahasiswa) com.mankelfas.util.Session.getCurrentUser();
            String info = "ID/NIM: " + mhs.getIdUser() + "\nNama: " + mhs.getNama() + 
                          "\nEmail: " + mhs.getEmail() + "\nRole: " + mhs.getRole() + 
                          "\nNIM: " + mhs.getNim();
            com.mankelfas.util.DialogHelper.showInfoDialog("Profil Mahasiswa", "Informasi Akun", info);
        }
    }

    @FXML
    private void bukaFasilitas() {
        try {
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

    @FXML
    private void logout() {
        try {
            com.mankelfas.App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
