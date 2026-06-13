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

    @FXML
    public void initialize() {
        // Setup columns
        colId.setCellValueFactory(new PropertyValueFactory<>("idFasilitas"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colKondisi.setCellValueFactory(new PropertyValueFactory<>("kondisi"));

        // Load data
        List<Fasilitas> allFasilitas = KeluhanService.getInstance().getAllFasilitas();
        fasilitasList = FXCollections.observableArrayList(allFasilitas);

        // Populate location filter combo box
        Set<String> lokasiUnik = allFasilitas.stream().map(Fasilitas::getLokasi).collect(Collectors.toSet());
        comboFilterLokasi.getItems().add("Semua Lokasi");
        comboFilterLokasi.getItems().addAll(lokasiUnik);
        comboFilterLokasi.getSelectionModel().selectFirst();

        // Setup Search and Filter (FilteredList)
        FilteredList<Fasilitas> filteredData = new FilteredList<>(fasilitasList, p -> true);

        // Add listeners
        inputSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate(filteredData);
        });

        comboFilterLokasi.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate(filteredData);
        });

        // Setup Sorting
        SortedList<Fasilitas> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabelFasilitas.comparatorProperty());

        tabelFasilitas.setItems(sortedData);
    }

    private void updatePredicate(FilteredList<Fasilitas> filteredData) {
        filteredData.setPredicate(fasilitas -> {
            String filterLoc = comboFilterLokasi.getValue();
            boolean matchLokasi = filterLoc == null || filterLoc.equals("Semua Lokasi") || fasilitas.getLokasi().equals(filterLoc);
            
            String searchStr = inputSearch.getText();
            boolean matchNama = searchStr == null || searchStr.isEmpty() || fasilitas.getNama().toLowerCase().contains(searchStr.toLowerCase());

            return matchLokasi && matchNama;
        });
    }

    @FXML
    private void lihatInfoPopup() {
        Fasilitas selected = tabelFasilitas.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Menggunakan method getInfo() dari model sesuai spesifikasi
            String info = selected.getInfo();
            String fullInfo = "Hasil return getInfo(): \n" + info + "\n\nDetail Tambahan:\nKondisi Saat Ini: " + selected.getKondisi();
            DialogHelper.showInfoDialog("Info Fasilitas", "Detail Ekstraksi Objek Fasilitas", fullInfo);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Pilih fasilitas di tabel terlebih dahulu!");
            alert.show();
        }
    }

    public void addFasilitasToTable(Fasilitas f) {
        fasilitasList.add(f);
        if (!comboFilterLokasi.getItems().contains(f.getLokasi())) {
            comboFilterLokasi.getItems().add(f.getLokasi());
        }
    }

    @FXML
    private void bukaFormTambah() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mankelfas/view/tambah_fasilitas.fxml"));
            Parent root = loader.load();
            
            TambahFasilitasController controller = loader.getController();
            controller.setParentController(this);
            
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

    @FXML
    private void tutupWindow() {
        Stage stage = (Stage) tabelFasilitas.getScene().getWindow();
        stage.close();
    }
}
