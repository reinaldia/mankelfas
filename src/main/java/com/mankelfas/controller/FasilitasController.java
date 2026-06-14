package com.mankelfas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.service.KeluhanService;
import com.mankelfas.util.DialogHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mengelola inventaris fasilitas yang terdaftar dalam sistem.
 * Membantu pengguna untuk melihat, menambah, serta menemukan data fasilitas dengan fitur pencarian dan penyaringan lokasi.
 */
public class FasilitasController {

    @FXML private TextField inputSearch;
    @FXML private ComboBox<String> comboFilterLokasi;
    
    @FXML private TableView<Fasilitas> tabelFasilitas;
    @FXML private TableColumn<Fasilitas, Integer> colId;
    @FXML private TableColumn<Fasilitas, String> colNama;
    @FXML private TableColumn<Fasilitas, String> colKategori;
    @FXML private TableColumn<Fasilitas, String> colLokasi;
    @FXML private TableColumn<Fasilitas, String> colKondisi;

    private ObservableList<Fasilitas> fasilitasList;

    /**
     * Menyiapkan tampilan awal jendela manajemen fasilitas.
     * Mengatur kolom tabel, memuat data awal, dan menginisialisasi fitur pencarian lanjutan.
     */
    @FXML
    public void initialize() {
        // Menyiapkan konfigurasi kolom tabel
        colId.setCellValueFactory(new PropertyValueFactory<>("idFasilitas"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colKondisi.setCellValueFactory(new PropertyValueFactory<>("kondisi"));

        // Memuat data fasilitas dari layanan keluhan
        List<Fasilitas> allFasilitas = KeluhanService.getInstance().getAllFasilitas();
        fasilitasList = FXCollections.observableArrayList(allFasilitas);

        // Mengisi kotak pilihan penyaring lokasi berdasarkan data unik
        Set<String> lokasiUnik = allFasilitas.stream().map(Fasilitas::getLokasi).collect(Collectors.toSet());
        comboFilterLokasi.getItems().add("Semua Lokasi");
        comboFilterLokasi.getItems().addAll(lokasiUnik);
        comboFilterLokasi.getSelectionModel().selectFirst();

        // Menyiapkan fungsi pencarian dan penyaringan menggunakan FilteredList
        FilteredList<Fasilitas> filteredData = new FilteredList<>(fasilitasList, p -> true);

        // Menambahkan pemantau kejadian (listeners) untuk merespons perubahan teks pencarian dan pilihan lokasi
        inputSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate(filteredData);
        });

        comboFilterLokasi.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate(filteredData);
        });

        // Menyiapkan fitur pengurutan data tabel
        SortedList<Fasilitas> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabelFasilitas.comparatorProperty());

        tabelFasilitas.setItems(sortedData);
    }

    /**
     * Memperbarui aturan penyaringan data tabel berdasarkan teks pencarian dan pilihan lokasi.
     * Fungsi ini dipanggil setiap kali pengguna mengetik di kotak pencarian atau memilih menu lungsur.
     * 
     * @param filteredData Wadah data yang memiliki kapabilitas penyaringan bawaan
     */
    private void updatePredicate(FilteredList<Fasilitas> filteredData) {
        // Menerapkan logika filter untuk memeriksa kecocokan data dengan kriteria pencarian
        filteredData.setPredicate(fasilitas -> {
            String filterLoc = comboFilterLokasi.getValue();
            // Menentukan apakah lokasi fasilitas sesuai dengan opsi yang dipilih (atau "Semua Lokasi")
            boolean matchLokasi = filterLoc == null || filterLoc.equals("Semua Lokasi") || fasilitas.getLokasi().equals(filterLoc);
            
            String searchStr = inputSearch.getText();
            // Menentukan apakah nama fasilitas memuat password pencarian (mengabaikan huruf besar/kecil)
            boolean matchNama = searchStr == null || searchStr.isEmpty() || fasilitas.getNama().toLowerCase().contains(searchStr.toLowerCase());

            // Baris data hanya ditampilkan jika memenuhi kedua kondisi tersebut
            return matchLokasi && matchNama;
        });
    }

    /**
     * Menampilkan jendela dialog kecil berisi informasi detail dari fasilitas yang sedang disorot di tabel.
     */
    @FXML
    private void lihatInfoPopup() {
        Fasilitas selected = tabelFasilitas.getSelectionModel().getSelectedItem();
        
        // Memastikan pengguna telah memilih sebuah baris fasilitas
        if (selected != null) {
            // Mengambil rangkaian teks informasi dasar dari objek model
            String info = selected.getInfo();
            
            // Menyusun format teks agar lebih mudah dibaca beserta status kondisi terkini
            String fullInfo = "Hasil ringkasan data fasilitas: \n" + info + "\n\nDetail Tambahan:\nKondisi Saat Ini: " + selected.getKondisi();
            
            // Memanggil kelas utilitas untuk menyajikan informasi ke layar
            DialogHelper.showInfoDialog("Info Fasilitas", "Detail Kelengkapan Fasilitas", fullInfo);
        } else {
            // Memunculkan teguran jika pengguna lupa menyorot data
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih fasilitas di tabel terlebih dahulu!");
            alert.show();
        }
    }

    /**
     * Menyisipkan entitas fasilitas yang baru ditambahkan langsung ke dalam tabel tanpa perlu memuat ulang dari database.
     * Sekaligus mendaftarkan lokasi barunya ke dalam menu lungsur penyaringan.
     * 
     * @param f Objek fasilitas baru
     */
    public void addFasilitasToTable(Fasilitas f) {
        // Menambahkan objek ke daftar reaktif
        fasilitasList.add(f);
        
        // Mengecek apakah lokasi fasilitas ini benar-benar baru, lalu memasukkannya ke opsi filter
        if (!comboFilterLokasi.getItems().contains(f.getLokasi())) {
            comboFilterLokasi.getItems().add(f.getLokasi());
        }
    }

    /**
     * Menginisiasi prosedur pembukaan formulir penambahan fasilitas baru.
     */
    @FXML
    private void bukaFormTambah() {
        try {
            // Memuat UI visual khusus untuk penambahan fasilitas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/tambah_fasilitas.fxml"));
            Parent root = loader.load();
            
            // Mengaitkan pengontrol anak dengan pengontrol induk ini
            TambahFasilitasController controller = loader.getController();
            controller.setParentController(this);
            
            // Mengatur parameter jendela agar fokus terpusat pada form ini (modal)
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Tambah Fasilitas Baru");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Gagal memuat form tambah fasilitas: " + e.getMessage());
            alert.show();
        }
    }

    /**
     * Menutup jendela manajemen fasilitas ini secara aman.
     */
    @FXML
    private void tutupWindow() {
        // Mengidentifikasi wadah tampilan aktif dan menutup prosesnya
        Stage stage = (Stage) tabelFasilitas.getScene().getWindow();
        stage.close();
    }
}
