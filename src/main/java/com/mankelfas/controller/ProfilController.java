package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import com.mankelfas.model.user.User;
import com.mankelfas.model.user.Admin;
import com.mankelfas.model.user.Mahasiswa;
import com.mankelfas.model.user.Teknisi;

/**
 * Controller untuk mengelola antarmuka Kartu Profil Pengguna.
 */
public class ProfilController {

    @FXML private Label lblInisial;
    @FXML private Label lblNama;
    @FXML private Label lblRole;
    @FXML private Label lblId;
    @FXML private Label lblEmail;
    @FXML private Label lblLabelEkstra;
    @FXML private Label lblNilaiEkstra;

    /**
     * Memasukkan data pengguna ke dalam UI Profil.
     * 
     * @param user Objek pengguna yang datanya akan ditampilkan
     */
    public void setUserData(User user) {
        if (user == null) return;

        lblNama.setText(user.getNama());
        lblRole.setText(user.getRole().toUpperCase());
        lblId.setText(String.valueOf(user.getIdUser()));
        lblEmail.setText(user.getEmail());

        // Mengatur inisial foto profil (huruf pertama dari nama)
        if (user.getNama() != null && !user.getNama().isEmpty()) {
            lblInisial.setText(user.getNama().substring(0, 1).toUpperCase());
        }

        // Mengisi kolom ekstra berdasarkan Role menggunakan Polimorfisme
        if (user instanceof Admin) {
            lblLabelEkstra.setText("Level:");
            lblNilaiEkstra.setText(((Admin) user).getLevel());
        } else if (user instanceof Mahasiswa) {
            lblLabelEkstra.setText("NIM:");
            lblNilaiEkstra.setText(((Mahasiswa) user).getNim());
        } else if (user instanceof Teknisi) {
            lblLabelEkstra.setText("Keahlian:");
            lblNilaiEkstra.setText(((Teknisi) user).getKeahlian());
        } else {
            lblLabelEkstra.setVisible(false);
            lblNilaiEkstra.setVisible(false);
        }
    }

    /**
     * Menutup jendela profil.
     */
    @FXML
    private void tutupProfil() {
        com.mankelfas.util.Navigator.goHome();
    }

    @FXML
    private void editProfil() {
        User currentUser = com.mankelfas.util.Session.getCurrentUser();
        if (currentUser == null) return;

        javafx.scene.control.Dialog<String[]> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Edit Profil");
        dialog.setHeaderText("Ubah Data Profil");

        javafx.scene.control.ButtonType simpanButtonType = new javafx.scene.control.ButtonType("Simpan", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, javafx.scene.control.ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        javafx.scene.control.TextField namaField = new javafx.scene.control.TextField(currentUser.getNama());
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField(currentUser.getEmail());
        javafx.scene.control.PasswordField passwordField = new javafx.scene.control.PasswordField();
        passwordField.setPromptText("Biarkan kosong jika tidak ubah");

        grid.add(new Label("Nama:"), 0, 0);
        grid.add(namaField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                String newPassword = passwordField.getText();
                if (newPassword != null && !newPassword.isEmpty() && newPassword.length() < 6) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    com.mankelfas.util.DialogHelper.styleAlert(alert);
                    alert.setContentText("Password baru harus memiliki minimal 6 karakter!");
                    alert.show();
                    return null; // Membatalkan proses update
                }
                
                return new String[]{namaField.getText(), emailField.getText(), newPassword};
            }
            return null;
        });

        com.mankelfas.util.ThemeManager.applyTheme(dialog.getDialogPane());
        java.util.Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(data -> {
            try {
                if (data[0] != null && !data[0].isEmpty()) currentUser.setNama(data[0]);
                if (data[1] != null && !data[1].isEmpty()) currentUser.setEmail(data[1]);
                if (data[2] != null && !data[2].isEmpty()) currentUser.setPassword(data[2]);
                
                com.mankelfas.service.UserService.getInstance().updateUser(currentUser);
                setUserData(currentUser);
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Profil Diperbarui", "Data profil Anda berhasil disimpan.");
            } catch (Exception e) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Gagal", "Gagal memperbarui profil: " + e.getMessage());
            }
        });
    }

    @FXML
    private void hapusAkun() {
        User currentUser = com.mankelfas.util.Session.getCurrentUser();
        if (currentUser == null) return;
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Akun");
        alert.setHeaderText("Konfirmasi Penghapusan Akun");
        alert.setGraphic(null);
        alert.setContentText("Anda yakin ingin keluar dari sistem?");

        com.mankelfas.util.ThemeManager.applyTheme(alert.getDialogPane());
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                com.mankelfas.service.UserService.getInstance().deleteUser(currentUser.getIdUser());
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Akun Dihapus", "Akun Anda telah berhasil dihapus.");
                // otomatis logout
                com.mankelfas.App.setRoot("login");
            } catch (Exception e) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Gagal", "Gagal menghapus akun: " + e.getMessage());
            }
        }
    }
}
