package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mankelfas.model.user.User;
import com.mankelfas.model.user.Admin;
import com.mankelfas.model.user.Mahasiswa;
import com.mankelfas.model.user.Teknisi;
import com.mankelfas.service.UserService;

public class AkunFormController {

    @FXML private Label lblTitle;
    @FXML private TextField inputNama;
    @FXML private TextField inputEmail;
    @FXML private TextField inputPassword;
    @FXML private ComboBox<String> comboRole;
    @FXML private VBox vboxEkstra;
    @FXML private Label lblEkstra;
    @FXML private TextField inputEkstra;

    private AkunController parentController;
    private User editData;
    private UserService userService = UserService.getInstance();

    public void setParentController(AkunController parentController) {
        this.parentController = parentController;
    }

    public void setUserData(User user) {
        this.editData = user;
        lblTitle.setText("Edit Akun - ID: " + user.getIdUser());
        inputNama.setText(user.getNama());
        inputEmail.setText(user.getEmail());
        inputPassword.setText(user.getPassword());
        
        comboRole.setValue(user.getRole());
        roleChanged();

        if (user instanceof Admin) {
            inputEkstra.setText(((Admin) user).getLevel());
        } else if (user instanceof Mahasiswa) {
            inputEkstra.setText(((Mahasiswa) user).getNim());
        } else if (user instanceof Teknisi) {
            inputEkstra.setText(((Teknisi) user).getKeahlian());
        }
        
        // Cannot change role during edit to simplify logic
        comboRole.setDisable(true);
    }

    @FXML
    private void roleChanged() {
        String role = comboRole.getValue();
        if (role == null) return;

        vboxEkstra.setVisible(true);
        vboxEkstra.setManaged(true);

        switch (role) {
            case "Admin":
                lblEkstra.setText("Level (Contoh: Super Admin, Moderator):");
                inputEkstra.setPromptText("Masukkan Level");
                break;
            case "Mahasiswa":
                lblEkstra.setText("NIM:");
                inputEkstra.setPromptText("Masukkan NIM");
                break;
            case "Teknisi":
                lblEkstra.setText("Keahlian Khusus:");
                inputEkstra.setPromptText("Contoh: Listrik, Pipa, Furnitur");
                break;
        }
    }

    @FXML
    private void simpan() {
        String nama = inputNama.getText();
        String email = inputEmail.getText();
        String password = inputPassword.getText();
        String role = comboRole.getValue();
        String ekstra = inputEkstra.getText();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Mohon lengkapi semua field utama (Nama, Email, Password, Role)!");
            alert.show();
            return;
        }

        try {
            if (editData == null) {
                // Tambah data baru
                User newUser;
                if ("Admin".equals(role)) {
                    newUser = new Admin(0, nama, email, password, ekstra);
                } else if ("Mahasiswa".equals(role)) {
                    newUser = new Mahasiswa(0, nama, email, password, ekstra);
                } else {
                    newUser = new Teknisi(0, nama, email, password, ekstra);
                }

                if (userService.addUser(newUser)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Berhasil menambahkan akun!");
                    alert.show();
                }
            } else {
                // Edit data lama
                editData.setIdUser(editData.getIdUser()); // just in case
                // We cannot use setter for all properties since User class has no setters for some, wait!
                // We need to either create a new user object with the same ID, or add setters.
                // Creating a new object with same ID for repository update is cleaner.
                User updatedUser;
                if ("Admin".equals(role)) {
                    updatedUser = new Admin(editData.getIdUser(), nama, email, password, ekstra);
                } else if ("Mahasiswa".equals(role)) {
                    updatedUser = new Mahasiswa(editData.getIdUser(), nama, email, password, ekstra);
                } else {
                    updatedUser = new Teknisi(editData.getIdUser(), nama, email, password, ekstra);
                }

                if (userService.updateUser(updatedUser)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Berhasil mengedit akun!");
                    alert.show();
                }
            }

            if (parentController != null) {
                parentController.refreshData();
            }
            batal();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal menyimpan data: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void batal() {
        Stage stage = (Stage) inputNama.getScene().getWindow();
        stage.close();
    }
}
