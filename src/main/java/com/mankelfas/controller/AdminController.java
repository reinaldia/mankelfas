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

/**
 * Mengelola seluruh operasi yang dilakukan oleh Admin melalui UI pengguna.
 * Memproses instruksi mulai dari manajemen akun pengguna hingga memantau keseluruhan rekapitulasi keluhan fasilitas.
 */
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

    /**
     * Mempersiapkan tampilan awal halaman dasbor Admin.
     * Menginisialisasi komponen UI, memuat data pengguna aktif, 
     * serta menyiapkan pengikatan data kolom tabel keluhan beserta referensi teknisi.
     */
    @FXML
    public void initialize() {
        // Menampilkan pesan sambutan dengan nama pengguna yang sedang masuk log
        if (com.mankelfas.util.Session.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + com.mankelfas.util.Session.getCurrentUser().getNama() + "!");
        }

        try {
            // Menginisialisasi instansi layanan untuk berinteraksi dengan database
            keluhanService = KeluhanService.getInstance();
            userService = UserService.getInstance();
            
            // Mengambil seluruh data Teknisi dari database
            teknisiList = userService.getAllUsers().stream()
                .filter(u -> u instanceof Teknisi)
                .map(u -> (Teknisi) u)
                .collect(Collectors.toList());
            
            // Menyusun daftar teknisi ke dalam menu lungsur (dropdown)
            for (Teknisi t : teknisiList) {
                comboTeknisi.getItems().add(t.getNama() + " (" + t.getKeahlian() + ")");
            }

            // Menghubungkan setiap kolom tabel dengan atribut bawaan dari entitas Keluhan
            colId.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
            colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
            colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
            
            // Menangani pengikatan data kolom Pelapor dengan memastikan objek tidak bernilai null
            colPelapor.setCellValueFactory(cellData -> {
                if (cellData.getValue().getPelapor() != null) {
                    return new SimpleStringProperty(cellData.getValue().getPelapor().getNama());
                }
                return new SimpleStringProperty("-");
            });

            // Menangani pengikatan data kolom Fasilitas dengan memastikan objek tidak bernilai null
            colFasilitas.setCellValueFactory(cellData -> {
                if (cellData.getValue().getFasilitas() != null) {
                    return new SimpleStringProperty(cellData.getValue().getFasilitas().getNama());
                }
                return new SimpleStringProperty("-");
            });

            // Menangani pengikatan data kolom Teknisi yang ditugaskan
            colTeknisi.setCellValueFactory(cellData -> {
                Teknisi t = cellData.getValue().getTeknisi();
                return new SimpleStringProperty(t != null ? t.getNama() : "Belum Ada");
            });

            // Memuat ulang data pada tabel agar informasi terbaru langsung tampil
            refreshTabel();
        } catch (Exception e) {
            // Menampilkan peringatan apabila terjadi kesalahan saat memuat data awal
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat dashboard admin: " + e.getMessage());
            alert.show();
        }
    }

    /**
     * Mendelegasikan penanganan keluhan kepada teknisi yang dipilih.
     * Mengubah status keluhan menjadi DIPROSES apabila status sebelumnya adalah DILAPORKAN.
     */
    @FXML
    private void assignTeknisi() {
        // Mengambil keluhan yang dipilih dari tabel serta teknisi yang dipilih dari menu lungsur
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        int selectedTeknisiIndex = comboTeknisi.getSelectionModel().getSelectedIndex();

        // Memastikan pengguna telah memilih keluhan dan teknisi sebelum melanjutkan
        if (selectedKeluhan != null && selectedTeknisiIndex >= 0) {
            try {
                // Menyematkan teknisi ke dalam objek keluhan
                Teknisi t = teknisiList.get(selectedTeknisiIndex);
                selectedKeluhan.setTeknisi(t);
                
                // Memperbarui status keluhan secara otomatis jika baru pertama kali diproses
                if (selectedKeluhan.getStatus() == StatusKeluhan.DILAPORKAN) {
                    selectedKeluhan.setStatus(StatusKeluhan.DIPROSES);
                }

                // Menyimpan pembaruan ke dalam database dan menyegarkan tampilan tabel
                keluhanService.updateKeluhan(selectedKeluhan);
                tabelKeluhan.refresh();
                
                // Memberikan informasi keberhasilan kepada pengguna
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Teknisi berhasil di-assign!");
                alert.show();
            } catch (Exception e) {
                // Menangani kegagalan saat proses penyimpanan
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal assign teknisi: " + e.getMessage());
                alert.show();
            }
        } else {
            // Memperingatkan pengguna apabila ada pilihan yang belum lengkap
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan dan teknisi terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Memperbarui daftar keluhan yang ditampilkan pada tabel.
     * Menerapkan penyaringan berdasarkan opsi apakah keluhan yang telah diarsipkan perlu ditampilkan atau tidak.
     */
    @FXML
    private void refreshTabel() {
        try {
            // Mengecek apakah kotak centang untuk menampilkan arsip sedang aktif
            boolean showArchived = checkTampilkanArsip.isSelected();
            List<Keluhan> all = keluhanService.getAllKeluhan();
            
            // Menyaring daftar keluhan sesuai kriteria penampilan arsip
            List<Keluhan> filtered = all.stream()
                .filter(k -> showArchived || !k.isArchived())
                .collect(Collectors.toList());
                
            // Memasukkan data hasil saringan kembali ke dalam tabel
            keluhanObservableList = FXCollections.observableArrayList(filtered);
            tabelKeluhan.setItems(keluhanObservableList);
            tabelKeluhan.refresh();
        } catch (Exception e) {
            // Mencetak jejak kesalahan apabila gagal memuat ulang data
            e.printStackTrace();
        }
    }

    /**
     * Mengarsipkan keluhan yang telah selesai agar tidak menumpuk di tampilan utama.
     */
    @FXML
    private void arsipkanKeluhan() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        // Memastikan terdapat keluhan yang dipilih sebelum proses pengarsipan
        if (selectedKeluhan != null) {
            try {
                // Mengubah status keluhan menjadi diarsipkan dan menyimpannya ke database
                selectedKeluhan.arsipkan();
                keluhanService.updateKeluhan(selectedKeluhan);
                
                // Memperbarui tampilan UI
                refreshTabel();
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Arsip Keluhan", "Keluhan telah dipindahkan ke arsip.");
            } catch (Exception e) {
                // Menangani kemungkinan kesalahan komunikasi dengan database
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal mengarsipkan: " + e.getMessage());
                alert.show();
            }
        } else {
            // Memperingatkan pengguna apabila tidak ada keluhan yang dipilih
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Mengatur tingkat urgensi atau prioritas penanganan untuk keluhan yang dipilih.
     */
    @FXML
    private void setPrioritas() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        String prioStr = comboPrioritas.getValue();
        
        // Memastikan keluhan serta nilai prioritas telah dipilih secara valid
        if (selectedKeluhan != null && prioStr != null) {
            try {
                // Mengonversi teks menjadi enumerasi prioritas dan memperbarui datanya
                selectedKeluhan.setPrioritas(com.mankelfas.enumeration.Prioritas.valueOf(prioStr));
                keluhanService.updateKeluhan(selectedKeluhan);
                tabelKeluhan.refresh();
                
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Prioritas Disetel", "Prioritas keluhan diubah menjadi " + prioStr);
            } catch (Exception e) {
                // Memberikan pesan peringatan apabila operasi penyimpanan gagal
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal set prioritas: " + e.getMessage());
                alert.show();
            }
        } else {
            // Mengingatkan pengguna jika isian belum lengkap
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan dan prioritas terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Membatalkan sebuah keluhan jika dianggap tidak valid atau salah lapor.
     */
    @FXML
    private void batalkanKeluhan() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        // Melakukan pengecekan validitas pilihan keluhan
        if (selectedKeluhan != null) {
            try {
                // Mengganti status keluhan dan menyimpannya secara permanen
                selectedKeluhan.setStatus(StatusKeluhan.DIBATALKAN);
                keluhanService.updateKeluhan(selectedKeluhan);
                tabelKeluhan.refresh();
                
                // Menampilkan notifikasi sukses kepada Admin
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Keluhan telah dibatalkan.");
                alert.show();
            } catch (Exception e) {
                // Menampilkan notifikasi kegagalan apabila terjadi rintangan teknis
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

    /**
     * Membuka jendela baru yang menampilkan rincian informasi dan riwayat keluhan.
     */
    @FXML
    private void lihatDetail() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        // Memastikan keluhan sudah dipilih sebelum mencoba membuka detail
        if (selectedKeluhan != null) {
            try {
                // Memuat layout UI dari file FXML yang sesuai
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/keluhan_detail.fxml"));
                Parent root = loader.load();
                
                // Mengirimkan data keluhan ke pengelola UI (controller) tujuan
                KeluhanDetailController controller = loader.getController();
                controller.setKeluhanData(selectedKeluhan);
                
                // Menyiapkan dan menampilkan jendela baru secara eksklusif (modal)
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Detail Keluhan");
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (Exception e) {
                // Menampilkan pesan kesalahan jika gagal memuat UI rincian
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

    /**
     * Menampilkan informasi mendetail dari akun administrator yang sedang aktif.
     */
    @FXML
    private void lihatInfoProfil() {
        // Melakukan validasi keamanan untuk memastikan peran pengguna adalah Admin
        if (com.mankelfas.util.Session.getCurrentUser() instanceof com.mankelfas.model.user.Admin) {
            com.mankelfas.model.user.Admin admin = (com.mankelfas.model.user.Admin) com.mankelfas.util.Session.getCurrentUser();
            
            // Merangkai informasi identitas akun menjadi satu teks utuh
            String info = "ID: " + admin.getIdUser() + "\nNama: " + admin.getNama() + 
                          "\nEmail: " + admin.getEmail() + "\nRole: " + admin.getRole() + 
                          "\nLevel: " + admin.getLevel();
            com.mankelfas.util.DialogHelper.showInfoDialog("Profil Admin", "Informasi Akun", info);
        }
    }

    /**
     * Membuka modul manajemen daftar fasilitas ke dalam jendela baru.
     */
    @FXML
    private void bukaFasilitas() {
        try {
            // Memuat dan mengonfigurasi tampilan manajemen fasilitas
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

    /**
     * Membuka modul manajemen data pengguna ke dalam jendela baru.
     */
    @FXML
    private void bukaDaftarAkun() {
        try {
            // Memuat UI pengelola akun pengguna
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

    /**
     * Membuka modul pemantauan rekapitulasi rintangan teknisi ke dalam jendela baru.
     */
    @FXML
    private void bukaKendalaTeknisi() {
        try {
            // Menyiapkan jendela pemantauan catatan hambatan teknisi
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/kendala_view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Daftar Kendala Teknisi");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            // Mencatat jejak kesalahan apabila UI gagal dimuat
            e.printStackTrace();
        }
    }

    /**
     * Mengeluarkan pengguna dari sesi aktif saat ini dan mengembalikannya ke layar masuk.
     */
    @FXML
    private void logout() {
        try {
            // Mengganti tampilan UI utama kembali ke halaman login
            com.mankelfas.App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
