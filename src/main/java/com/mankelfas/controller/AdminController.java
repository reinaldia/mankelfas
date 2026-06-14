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
    @FXML private javafx.scene.layout.BorderPane mainPane;
    @FXML private javafx.scene.layout.VBox dashboardContent;


    @FXML private TableView<Keluhan> tabelKeluhan;
    @FXML private TableColumn<Keluhan, Integer> colId;
    @FXML private TableColumn<Keluhan, String> colPelapor;
    @FXML private TableColumn<Keluhan, String> colFasilitas;
    @FXML private TableColumn<Keluhan, String> colDeskripsi;
    @FXML private TableColumn<Keluhan, String> colPrioritas;
    @FXML private TableColumn<Keluhan, String> colStatus;
    @FXML private TableColumn<Keluhan, String> colTeknisi;
    
    @FXML private ComboBox<String> comboTeknisi;
    @FXML private ComboBox<String> comboPrioritas;
    @FXML private javafx.scene.control.CheckBox checkTampilkanArsip;
    @FXML private Label lblWelcome;
    @FXML private Label lblTotalAktif;
    @FXML private Label lblMenunggu;
    @FXML private Label lblDitangani;

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
        tabelKeluhan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        com.mankelfas.util.Navigator.registerMainPane(mainPane, dashboardContent);

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
            colStatus.setCellValueFactory(cellData -> {
                com.mankelfas.model.keluhan.Keluhan k = cellData.getValue();
                String st = k.getStatus().name();
                return new javafx.beans.property.SimpleStringProperty(k.isArchived() ? st + " (ARSIP)" : st);
            });

            colPrioritas.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrioritas().name()));
            colPrioritas.setCellFactory(column -> new javafx.scene.control.TableCell<Keluhan, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        javafx.scene.control.Label chip = new javafx.scene.control.Label(item);
                        chip.getStyleClass().add("chip");
                        chip.getStyleClass().add("chip-" + item.toLowerCase());
                        setGraphic(chip);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            });

            
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
            if (selectedKeluhan.getStatus() == com.mankelfas.enumeration.StatusKeluhan.DITOLAK) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Aksi Ditolak", "Keluhan yang sudah ditolak tidak dapat di-assign teknisi.");
                return;
            }
            try {
                // Menyematkan teknisi ke dalam objek keluhan dengan mencatat riwayatnya
                Teknisi t = teknisiList.get(selectedTeknisiIndex);
                boolean isReplacement = (selectedKeluhan.getTeknisi() != null);
                selectedKeluhan.updateTeknisi(t);
                
                // Memperbarui status keluhan secara otomatis jika baru pertama kali diproses
                if (selectedKeluhan.getStatus() == StatusKeluhan.DILAPORKAN) {
                    selectedKeluhan.updateStatus(StatusKeluhan.DITUGASKAN);
                    com.mankelfas.enumeration.KondisiFasilitas k = selectedKeluhan.getFasilitas().getKondisi();
                    selectedKeluhan.updateKondisiFasilitas(k.toSedangDiperiksa());
                    keluhanService.updateFasilitas(selectedKeluhan.getFasilitas());
                }

                // Menyimpan pembaruan ke dalam database dan menyegarkan tampilan tabel
                keluhanService.updateKeluhan(selectedKeluhan);
                refreshTabel();
                
                // Memberikan informasi keberhasilan kepada pengguna
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(isReplacement ? "Teknisi berhasil diganti!" : "Teknisi berhasil di-assign!");
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

            // Hitung statistik mengabaikan arsip, DITOLAK, dan SELESAI
            java.util.function.Predicate<Keluhan> isActive = k -> 
                !k.isArchived() && 
                k.getStatus() != StatusKeluhan.DITOLAK && 
                k.getStatus() != StatusKeluhan.SELESAI;

            long totalAktif = all.stream().filter(isActive).count();
            long menunggu = all.stream().filter(k -> isActive.test(k) && k.getTeknisi() == null).count();
            long ditangani = all.stream().filter(k -> isActive.test(k) && k.getTeknisi() != null).count();
            
            if(lblTotalAktif != null) lblTotalAktif.setText(String.valueOf(totalAktif));
            if(lblMenunggu != null) lblMenunggu.setText(String.valueOf(menunggu));
            if(lblDitangani != null) lblDitangani.setText(String.valueOf(ditangani));

            
            // Menyaring daftar keluhan sesuai kriteria penampilan arsip
            // Keluhan tetap tampil di tabel meskipun DITOLAK/SELESAI asalkan belum diarsipkan
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
    /**
     * Mengembalikan keluhan yang telah diarsipkan ke daftar aktif.
     */
    @FXML
    private void batalArsip() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        if (selectedKeluhan != null) {
            if (!selectedKeluhan.isArchived()) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Status Tidak Valid", "Keluhan ini tidak sedang diarsipkan.");
                return;
            }
            try {
                selectedKeluhan.batalArsip();
                keluhanService.updateKeluhan(selectedKeluhan);
                
                refreshTabel();
                com.mankelfas.util.DialogHelper.showInfoDialog("Sukses", "Pulihkan Arsip", "Keluhan telah dikembalikan dari arsip.");
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal memulihkan arsip: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih keluhan terlebih dahulu!");
            alert.show();
        }
    }

    @FXML
    private void arsipkanKeluhan() {
        Keluhan selectedKeluhan = tabelKeluhan.getSelectionModel().getSelectedItem();
        
        // Memastikan terdapat keluhan yang dipilih sebelum proses pengarsipan
        if (selectedKeluhan != null) {
            if (selectedKeluhan.getStatus() != com.mankelfas.enumeration.StatusKeluhan.SELESAI && selectedKeluhan.getStatus() != com.mankelfas.enumeration.StatusKeluhan.DITOLAK) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Status Tidak Valid", "Hanya keluhan dengan status SELESAI atau DITOLAK yang dapat diarsipkan.");
                return;
            }
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
            if (selectedKeluhan.getStatus() == com.mankelfas.enumeration.StatusKeluhan.DITOLAK) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Aksi Ditolak", "Keluhan yang sudah ditolak tidak dapat diubah prioritasnya.");
                return;
            }
            try {
                // Mengonversi teks menjadi enumerasi prioritas dan memperbarui datanya
                selectedKeluhan.updatePrioritas(com.mankelfas.enumeration.Prioritas.valueOf(prioStr));
                keluhanService.updateKeluhan(selectedKeluhan);
                refreshTabel();
                
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
            if (selectedKeluhan.getStatus() != com.mankelfas.enumeration.StatusKeluhan.DILAPORKAN) {
                com.mankelfas.util.DialogHelper.showErrorDialog("Status Tidak Valid", "Hanya keluhan dengan status DILAPORKAN yang dapat ditolak.");
                return;
            }
            try {
                // Mengganti status keluhan dan menyimpannya secara permanen
                selectedKeluhan.updateStatus(StatusKeluhan.DITOLAK);
                keluhanService.updateKeluhan(selectedKeluhan);
                refreshTabel();
                
                // Menampilkan notifikasi sukses kepada Admin
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Keluhan telah ditolak.");
                alert.show();
            } catch (Exception e) {
                // Menampilkan notifikasi kegagalan apabila terjadi rintangan teknis
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal menolak keluhan: " + e.getMessage());
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
     * Memuat dan menampilkan antarmuka khusus Profil Pengguna.
     */
    @FXML
    private void lihatInfoProfil() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mankelfas/view/profil_view.fxml"));
            javafx.scene.Parent root = loader.load();
            
            ProfilController controller = loader.getController();
            controller.setUserData(com.mankelfas.util.Session.getCurrentUser());
            
            com.mankelfas.util.Navigator.navigate(root);
        } catch (Exception e) {
            com.mankelfas.util.DialogHelper.showErrorDialog("Error", "Gagal memuat UI profil: " + e.getMessage());
        }
    }

    /**
     * Membalikkan warna tema aplikasi antara Terang dan Gelap.
     */
    @FXML
    private void toggleMode(javafx.event.ActionEvent event) {
        com.mankelfas.util.ThemeManager.toggleTheme();
        
        // Memuat ulang tema pada jendela (Scene) yang sedang aktif
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        com.mankelfas.util.ThemeManager.applyTheme(btn.getScene());
        
        // Memperbarui ikon matahari/bulan pada tombol
        if (com.mankelfas.util.ThemeManager.isDarkMode()) {
            btn.setText("☽");
        } else {
            btn.setText("☼");
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
            com.mankelfas.util.Navigator.navigate(root);
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
            com.mankelfas.util.Navigator.navigate(root);
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
            com.mankelfas.util.Navigator.navigate(root);
        } catch (Exception e) {
            // Mencatat jejak kesalahan apabila UI gagal dimuat
            e.printStackTrace();
        }
    }

    /**
     * Mengeluarkan pengguna dari sesi aktif saat ini dan mengembalikannya ke layar masuk.
     */
    @FXML
    private void kembaliKeBeranda() {
        com.mankelfas.util.Navigator.goHome();
    }

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
