package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.model.user.Teknisi;
import com.mankelfas.service.KeluhanService;
import com.mankelfas.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {

    @FXML private TableView<Keluhan> tabelKeluhan;
    @FXML private TableColumn<Keluhan, Integer> colId;
    @FXML private TableColumn<Keluhan, String> colPelapor;
    @FXML private TableColumn<Keluhan, String> colFasilitas;
    @FXML private TableColumn<Keluhan, String> colDeskripsi;
    @FXML private TableColumn<Keluhan, String> colStatus;
    @FXML private TableColumn<Keluhan, String> colTeknisi;
    
    @FXML private ComboBox<String> comboTeknisi;
    @FXML private ComboBox<String> comboPrioritas;
    @FXML private javafx.scene.control.CheckBox checkTampilkanArsip;
    @FXML private Label lblWelcome;

    private KeluhanService keluhanService;
    private UserService userService;
    private ObservableList<Keluhan> keluhanObservableList;
    private List<Teknisi> teknisiList;

    @FXML
    public void initialize() {
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        try {
            keluhanService = KeluhanService.getInstance();
            userService = UserService.getInstance();
            
            // Get all Teknisi from DB
            teknisiList = userService.getAllUsers().stream()
                .filter(u -> u instanceof Teknisi)
                .map(u -> (Teknisi) u)
                .collect(Collectors.toList());
            
            for (Teknisi t : teknisiList) {
                comboTeknisi.getItems().add(t.getNama() + " (" + t.getKeahlian() + ")");
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
            colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
            colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
            
            colPelapor.setCellValueFactory(cellData -> {
                if (cellData.getValue().getPelapor() != null) {
                    return new SimpleStringProperty(cellData.getValue().getPelapor().getNama());
                }
                return new SimpleStringProperty("-");
            });

            colFasilitas.setCellValueFactory(cellData -> {
                if (cellData.getValue().getFasilitas() != null) {
                    return new SimpleStringProperty(cellData.getValue().getFasilitas().getNama());
                }
                return new SimpleStringProperty("-");
            });

            colTeknisi.setCellValueFactory(cellData -> {
                Teknisi t = cellData.getValue().getTeknisi();
                return new SimpleStringProperty(t != null ? t.getNama() : "Belum Ada");
            });

            refreshTabel();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat dashboard admin: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void assignTeknisi() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        int selectedTeknisiIndex = comboTeknisi.getSelectionModel().getSelectedIndex();

        if (selectedKeluhan != null && selectedTeknisiIndex >= 0) {
            try {
                Teknisi t = teknisiList.get(selectedTeknisiIndex);
                selectedKeluhan.setTeknisi(t);
                
                if (selectedKeluhan.getStatus() == StatusKeluhan.DILAPORKAN) {
                    selectedKeluhan.setStatus(StatusKeluhan.DIPROSES);
                }

                keluhanService.updateKeluhan(selectedKeluhan);
                tabelKeluhan.refresh();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Teknisi berhasil di-assign!");
                alert.show();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal assign teknisi: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan dan teknisi terlebih dahulu!");
            alert.show();
        }
    }

    @FXML
    private void refreshTabel() {
        try {
            boolean showArchived = checkTampilkanArsip.isSelected();
            List<Keluhan> all = keluhanService.getAllKeluhan();
            List<Keluhan> filtered = all.stream()
                .filter(k -> showArchived || !k.isArchived())
                .collect(Collectors.toList());
            keluhanObservableList = FXCollections.observableArrayList(filtered);
            tabelKeluhan.setItems(keluhanObservableList);
            tabelKeluhan.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void arsipkanKeluhan() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selectedKeluhan != null) {
            try {
                selectedKeluhan.arsipkan();
                keluhanService.updateKeluhan(selectedKeluhan);
                refreshTabel();
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Arsip Keluhan", "Keluhan telah dipindahkan ke arsip.");
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal mengarsipkan: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
            alert.show();
        }
    }

    @FXML
    private void setPrioritas() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        String prioStr = comboPrioritas.getValue();
        if (selectedKeluhan != null && prioStr != null) {
            try {
                selectedKeluhan.setPrioritas(com.mankelfas.enumeration.Prioritas.valueOf(prioStr));
                keluhanService.updateKeluhan(selectedKeluhan);
                tabelKeluhan.refresh();
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Prioritas Disetel", "Prioritas keluhan diubah menjadi " + prioStr);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal set prioritas: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan dan prioritas terlebih dahulu!");
            alert.show();
        }
    }

    @FXML
    private void batalkanKeluhan() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        if (selectedKeluhan != null) {
            try {
                selectedKeluhan.setStatus(StatusKeluhan.DIBATALKAN);
                keluhanService.updateKeluhan(selectedKeluhan);
                tabelKeluhan.refresh();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Keluhan telah dibatalkan.");
                alert.show();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal membatalkan keluhan: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
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
                stage.setTitle("Detail Keluhan");
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
        if (com.mankelfas.util.Session.getCurrentUser() instanceof com.mankelfas.model.user.Admin) {
            com.mankelfas.model.user.Admin admin = (com.mankelfas.model.user.Admin) com.mankelfas.util.Session.getCurrentUser();
            String info = "ID: " + admin.getIdUser() + "\nNama: " + admin.getNama() + 
                          "\nEmail: " + admin.getEmail() + "\nRole: " + admin.getRole() + 
                          "\nLevel: " + admin.getLevel();
            com.mankelfas.util.DialogHelper.showInfoDialog("Profil Admin", "Informasi Akun", info);
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
            alert.setContentText("Gagal membuka window fasilitas: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void bukaDaftarAkun() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/akun_view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Daftar Akun Pengguna");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal membuka daftar akun: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void bukaKendalaTeknisi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/kendala_view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Daftar Kendala Teknisi");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
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
