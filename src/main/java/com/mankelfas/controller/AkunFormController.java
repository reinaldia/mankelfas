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

/**
 * Menangani tampilan formulir untuk pendaftaran dan penyuntingan akun.
 * Bertugas memastikan kelengkapan dan keabsahan data pengguna sebelum disimpan ke dalam database.
 */
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

    /**
     * Menghubungkan formulir ini dengan pengontrol utama daftar akun agar dapat memicu penyegaran tabel.
     * 
     * @param parentController Referensi pengontrol halaman akun utama
     */
    public void setParentController(AkunController parentController) {
        this.parentController = parentController;
    }

    /**
     * Mengisi kolom input formulir dengan data pengguna yang sudah ada untuk proses penyuntingan.
     * 
     * @param user Data pengguna yang akan diedit
     */
    public void setUserData(User user) {
        this.editData = user;
        
        // Memperbarui judul jendela untuk menandakan mode penyuntingan
        lblTitle.setText("Edit Akun - ID: " + user.getIdUser());
        
        // Memasukkan data identitas dasar ke kolom input
        inputNama.setText(user.getNama());
        inputEmail.setText(user.getEmail());
        inputPassword.setText(user.getPassword());
        
        // Memilih peran pengguna dan menyesuaikan tampilan formulir dinamis
        comboRole.setValue(user.getRole());
        roleChanged();

        // Mengekstraksi informasi tambahan (level, NIM, atau keahlian) berdasarkan tipe pengguna
        if (user instanceof Admin) {
            inputEkstra.setText(((Admin) user).getLevel());
        } else if (user instanceof Mahasiswa) {
            inputEkstra.setText(((Mahasiswa) user).getNim());
        } else if (user instanceof Teknisi) {
            inputEkstra.setText(((Teknisi) user).getKeahlian());
        }
        
        // Menonaktifkan perubahan peran saat proses penyuntingan untuk menjaga konsistensi data
        comboRole.setDisable(true);
    }

    /**
     * Merespons perubahan pilihan pada menu lungsur peran pengguna.
     * Secara dinamis menyesuaikan label dan petunjuk pada kolom input ekstra (level, NIM, atau keahlian).
     */
    @FXML
    private void roleChanged() {
        String role = comboRole.getValue();
        // Menghentikan eksekusi jika tidak ada peran yang dipilih
        if (role == null) return;

        // Memastikan wadah komponen ekstra terlihat oleh pengguna
        vboxEkstra.setVisible(true);
        vboxEkstra.setManaged(true);

        // Menyesuaikan tampilan berdasarkan spesifikasi jenis pengguna
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

    /**
     * Memproses logika penyimpanan akun baru maupun modifikasi akun lama.
     * Melakukan validasi input sebelum mengirim data ke lapisan layanan (Service).
     */
    @FXML
    private void simpan() {
        // Mengumpulkan seluruh isian dari elemen UI
        String nama = inputNama.getText();
        String email = inputEmail.getText();
        String password = inputPassword.getText();
        String role = comboRole.getValue();
        String ekstra = inputEkstra.getText();

        // Melakukan validasi dasar untuk mencegah penyimpanan data yang tidak lengkap
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            com.mankelfas.util.DialogHelper.styleAlert(alert);
            alert.setContentText("Mohon lengkapi semua field utama (Nama, Email, Password, Role)!");
            alert.show();
            return;
        }
        
        if (password.length() < 6) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            com.mankelfas.util.DialogHelper.styleAlert(alert);
            alert.setContentText("Password harus memiliki minimal 6 karakter!");
            alert.show();
            return;
        }

        try {
            if (editData == null) {
                // Proses pembuatan akun pengguna baru
                User newUser;
                // Membangun instansi objek yang tepat berdasarkan peran yang dipilih
                if ("Admin".equals(role)) {
                    newUser = new Admin(0, nama, email, password, ekstra);
                } else if ("Mahasiswa".equals(role)) {
                    newUser = new Mahasiswa(0, nama, email, password, ekstra);
                } else {
                    newUser = new Teknisi(0, nama, email, password, ekstra);
                }

                // Mengeksekusi penambahan ke database dan memberikan tanggapan kepada pengguna
                if (userService.addUser(newUser)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
            com.mankelfas.util.DialogHelper.styleAlert(alert);
                    alert.setContentText("Berhasil menambahkan akun!");
                    alert.show();
                }
            } else {
                // Proses modifikasi data pengguna yang sudah ada
                editData.setIdUser(editData.getIdUser()); // Memastikan ID pengguna tetap dipertahankan
                
                // Membuat instansi objek pengguna baru dengan ID yang sama untuk mempermudah proses pembaruan di lapisan repositori.
                User updatedUser;
                if ("Admin".equals(role)) {
                    updatedUser = new Admin(editData.getIdUser(), nama, email, password, ekstra);
                } else if ("Mahasiswa".equals(role)) {
                    updatedUser = new Mahasiswa(editData.getIdUser(), nama, email, password, ekstra);
                } else {
                    updatedUser = new Teknisi(editData.getIdUser(), nama, email, password, ekstra);
                }

                // Mengeksekusi penyuntingan ke database
                if (userService.updateUser(updatedUser)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
            com.mankelfas.util.DialogHelper.styleAlert(alert);
                    alert.setContentText("Berhasil mengedit akun!");
                    alert.show();
                }
            }

            // Memerintahkan pengontrol utama untuk memuat ulang daftar data akun
            if (parentController != null) {
                parentController.refreshData();
            }
            // Menutup jendela formulir
            batal();
        } catch (Exception e) {
            // Menampilkan laporan kegagalan operasional
            Alert alert = new Alert(Alert.AlertType.ERROR);
            com.mankelfas.util.DialogHelper.styleAlert(alert);
            alert.setContentText("Gagal menyimpan data: " + e.getMessage());
            alert.show();
        }
    }

    /**
     * Membatalkan proses pengisian formulir dan menutup jendela saat ini secara paksa.
     */
    @FXML
    private void batal() {
        // Menemukan jendela induk (Stage) yang sedang berjalan dan menutupnya
        Stage stage = (Stage) inputNama.getScene().getWindow();
        stage.close();
    }
}
