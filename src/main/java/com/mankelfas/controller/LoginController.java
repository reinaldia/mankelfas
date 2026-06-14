package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.mankelfas.model.user.User;
import com.mankelfas.service.IUserService;
import com.mankelfas.service.UserService;

/**
 * Menangani proses autentikasi pengguna saat masuk ke dalam aplikasi.
 * Memastikan kesesuaian kredensial (email dan password) sebelum memberikan hak akses sesuai peran masing-masing.
 */
public class LoginController {

    @FXML
    private TextField inputEmail;

    @FXML
    private PasswordField inputPassword;

    private final IUserService userService;

    /**
     * Konstruktor standar untuk menginisialisasi layanan pengguna secara otomatis.
     */
    public LoginController() {
        // Mengikat pengontrol ini dengan instansi tunggal layanan akun
        this.userService = UserService.getInstance();
    }

    /**
     * Memproses percobaan masuk setelah tombol login ditekan.
     * Mengarahkan pengguna ke halaman dasbor yang sesuai jika kredensial cocok.
     */
    @FXML
    private void btnLoginActionPerformed() {
        try {
            // Mengambil nilai teks dari isian email dan password
            String email = inputEmail.getText();
            String password = inputPassword.getText();

            // Memvalidasi kecocokan data dengan rekam jejak di database
            User user = userService.authenticate(email, password);

            if (user != null) {
                // Mencatat data identitas ke dalam sesi aktif aplikasi
                com.mankelfas.util.Session.setCurrentUser(user);
                String role = user.getRole();
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login berhasil sebagai: " + role);
                
                try {
                    // Mengarahkan tampilan ke dasbor berdasarkan jenis hak akses
                    if (role.equalsIgnoreCase("Mahasiswa")) {
                        com.mankelfas.App.setRoot("mahasiswa_dashboard");
                    } else if (role.equalsIgnoreCase("Admin")) {
                        com.mankelfas.App.setRoot("admin_dashboard");
                    } else if (role.equalsIgnoreCase("Teknisi")) {
                        com.mankelfas.App.setRoot("teknisi_dashboard");
                    }
                } catch (Exception ex) {
                    // Menangkap error jika peralihan layar gagal dimuat
                    showAlert(Alert.AlertType.ERROR, "Error UI", "Gagal memuat dashboard: " + ex.getMessage());
                }
                
            } else {
                // Menegur pengguna jika kombinasi email dan sandi tidak terdaftar
                showAlert(Alert.AlertType.ERROR, "Gagal", "Email atau password salah!");
            }
        } catch (Exception e) {
            // Memberitahukan masalah komunikasi infrastruktur server
            showAlert(Alert.AlertType.ERROR, "Error Sistem", "Terjadi masalah dengan koneksi ke DB");
        }
    }

    /**
     * Menampilkan kotak pesan dialog standar kepada pengguna.
     * 
     * @param type Jenis ikon peringatan
     * @param title Judul kotak pesan
     * @param message Isi pesan yang hendak disampaikan
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        try {
            // Mengonfigurasi dan memunculkan pop-up pemberitahuan
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            // Mencatat diam-diam apabila pop-up gagal ditampilkan
            System.err.println("Gagal menampilkan dialog box: " + e.getMessage());
        }
    }
}
