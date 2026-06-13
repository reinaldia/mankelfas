package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.mankelfas.model.user.User;
import com.mankelfas.service.IUserService;
import com.mankelfas.service.UserService;

public class LoginController {

    @FXML
    private TextField inputEmail;

    @FXML
    private PasswordField inputPassword;

    private final IUserService userService;

    public LoginController() {
        this.userService = UserService.getInstance();
    }

    @FXML
    private void btnLoginActionPerformed() {
        try {
            String email = inputEmail.getText();
            String password = inputPassword.getText();

            User user = userService.authenticate(email, password);

            if (user != null) {
                com.mankelfas.util.Session.setCurrentUser(user);
                String role = user.getRole();
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login berhasil sebagai: " + role);
                
                try {
                    if (role.equalsIgnoreCase("Mahasiswa")) {
                        com.mankelfas.App.setRoot("mahasiswa_dashboard");
                    } else if (role.equalsIgnoreCase("Admin")) {
                        com.mankelfas.App.setRoot("admin_dashboard");
                    } else if (role.equalsIgnoreCase("Teknisi")) {
                        com.mankelfas.App.setRoot("teknisi_dashboard");
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error UI", "Gagal memuat dashboard: " + ex.getMessage());
                }
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Email atau password salah!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Sistem", "Terjadi kesalahan internal: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Gagal menampilkan dialog box: " + e.getMessage());
        }
    }
}
