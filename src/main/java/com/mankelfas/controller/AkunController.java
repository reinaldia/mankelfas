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

/**
 * Mengatur pengelolaan data akun pengguna dalam sistem.
 * Menangani fungsi tampilan daftar pengguna, penambahan akun baru, serta penghapusan data secara aman.
 */
public class AkunController {

    @FXML private TableView<User> tabelAkun;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colNama;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colEkstra;

    private UserService userService;
    private ObservableList<User> observableData;

    /**
     * Mempersiapkan tampilan awal halaman pengelola akun.
     * Menghubungkan setiap kolom pada tabel dengan atribut dari entitas pengguna.
     */
    @FXML
    public void initialize() {
        tabelAkun.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Mendapatkan instansi layanan pengguna tunggal
        userService = UserService.getInstance();

        // Mengatur properti nilai untuk kolom dasar
        colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Memanfaatkan polimorfisme untuk mengambil peran dari setiap objek pengguna
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        
        // Menyajikan atribut spesifik berdasarkan instansi turunan kelas pengguna
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

        // Memuat data pertama kali saat halaman dibuka
        refreshData();
    }

    /**
     * Memuat ulang data pengguna dari database untuk memastikan tabel sinkron dengan perubahan terbaru.
     */
    public void refreshData() {
        try {
            // Memanggil data seluruh akun dan menyajikannya ke tabel FX
            observableData = FXCollections.observableArrayList(userService.getAllUsers());
            tabelAkun.setItems(observableData);
        } catch (Exception e) {
            // Menampilkan informasi peringatan apabila operasi baca data gagal
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat data akun: " + e.getMessage());
            alert.show();
        }
    }

    /**
     * Menginisiasi prosedur pembukaan formulir pembuatan akun pengguna baru.
     */
    @FXML
    private void bukaFormTambah() {
        // Mengirim referensi kosong menandakan pembuatan entitas baru
        bukaForm(null);
    }

    /**
     * Menginisiasi prosedur pembukaan formulir untuk memodifikasi data akun yang telah ada.
     */
    @FXML
    private void bukaFormEdit() {
        User selectedUser = tabelAkun.getSelectionModel().getSelectedItem();
        
        // Memastikan baris data yang ingin dimodifikasi telah disorot terlebih dahulu
        if (selectedUser != null) {
            bukaForm(selectedUser);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih akun yang ingin diedit terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Menangani proses pemuatan dan penampilan UI formulir akun secara dinamis.
     * Dapat digunakan secara multifungsi, baik untuk registrasi baru maupun penyuntingan.
     * 
     * @param user Referensi entitas pengguna (null jika penambahan data baru)
     */
    private void bukaForm(User user) {
        try {
            // Memuat cetak biru visual untuk layar formulir
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/form_akun.fxml"));
            Parent root = loader.load();
            
            // Menyematkan koneksi balik agar formulir dapat meminta penyegaran tabel setelah operasi sukses
            AkunFormController controller = loader.getController();
            controller.setParentController(this);
            
            // Melakukan pra-pengisian kolom input jika beroperasi dalam mode penyuntingan
            if (user != null) {
                controller.setUserData(user);
            }
            
            // Membatasi interaksi pengguna pada jendela tunggal yang terfokus
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

    /**
     * Menghapus entitas akun pengguna dari database setelah mendapat persetujuan ganda.
     */
    @FXML
    private void hapusAkun() {
        User selectedUser = tabelAkun.getSelectionModel().getSelectedItem();
        
        // Mengevaluasi validitas pilihan baris tabel
        if (selectedUser != null) {
            // Meminta validasi tambahan untuk mencegah penghapusan yang tak disengaja
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            alert.setHeaderText("Hapus Akun?");
            alert.setContentText("Apakah Anda yakin ingin menghapus akun: " + selectedUser.getNama() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            
            // Mengeksekusi penghapusan jika persetujuan berhasil didapatkan
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
            // Mengingatkan prasyarat seleksi ke pengguna
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih akun yang ingin dihapus terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Mengakhiri sesi pratinjau halaman pengelolaan dan menutup layarnya.
     */
    @FXML
    private void tutupWindow() {
        // Melacak konteks UI yang sedang beroperasi dan menginstruksikan terminasi
        com.mankelfas.util.Navigator.goHome();
    }
}
