package com.mankelfas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.mankelfas.model.user.User;
import com.mankelfas.model.user.Admin;
import com.mankelfas.model.user.Mahasiswa;
import com.mankelfas.model.user.Teknisi;
import com.mankelfas.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.util.Optional;

public class AkunController {

    @FXML private TableView<User> tabelAkun;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colNama;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colEkstra;

    private UserService userService;
    private ObservableList<User> observableData;

    @FXML
    public void initialize() {
        userService = UserService.getInstance();

        colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        
        colEkstra.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            if (u instanceof Mahasiswa) {
                return new SimpleStringProperty("NIM: " + ((Mahasiswa) u).getNim());
            } else if (u instanceof Teknisi) {
                return new SimpleStringProperty("Keahlian: " + ((Teknisi) u).getKeahlian());
            } else if (u instanceof Admin) {
                return new SimpleStringProperty("Level: " + ((Admin) u).getLevel());
            }
            return new SimpleStringProperty("-");
        });

        refreshData();
    }

    public void refreshData() {
        try {
            observableData = FXCollections.observableArrayList(userService.getAllUsers());
            tabelAkun.setItems(observableData);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat data akun: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void bukaFormTambah() {
        bukaForm(null);
    }

    @FXML
    private void bukaFormEdit() {
        User selectedUser = tabelAkun.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            bukaForm(selectedUser);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih akun yang ingin diedit terlebih dahulu!");
            alert.show();
        }
    }

    private void bukaForm(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/form_akun.fxml"));
            Parent root = loader.load();
            
            AkunFormController controller = loader.getController();
            controller.setParentController(this);
            if (user != null) {
                controller.setUserData(user);
            }
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(user == null ? "Tambah Akun" : "Edit Akun");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat form akun: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void hapusAkun() {
        User selectedUser = tabelAkun.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            alert.setHeaderText("Hapus Akun?");
            alert.setContentText("Apakah Anda yakin ingin menghapus akun: " + selectedUser.getNama() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    userService.deleteUser(selectedUser.getIdUser());
                    refreshData();
                } catch (Exception e) {
                    Alert err = new Alert(Alert.AlertType.ERROR);
                    err.setContentText("Gagal menghapus akun: " + e.getMessage());
                    err.show();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih akun yang ingin dihapus terlebih dahulu!");
            alert.show();
        }
    }

    @FXML
    private void tutupWindow() {
        Stage stage = (Stage) tabelAkun.getScene().getWindow();
        stage.close();
    }
}
