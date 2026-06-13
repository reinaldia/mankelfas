package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.service.KeluhanService;

public class TambahFasilitasController {

    @FXML private TextField inputNama;
    @FXML private ComboBox<String> comboKategori;
    @FXML private TextField inputLokasi;
    @FXML private ComboBox<String> comboKondisi;

    private FasilitasController parentController;

    @FXML
    public void initialize() {
        comboKategori.getItems().addAll("Elektronik", "Furnitur", "Bangunan", "Lainnya");
        comboKondisi.getItems().addAll("Baik", "Rusak Ringan", "Rusak Berat", "Mati Total");
    }

    public void setParentController(FasilitasController parent) {
        this.parentController = parent;
    }

    @FXML
    private void simpanFasilitas() {
        String nama = inputNama.getText();
        String kategori = comboKategori.getValue();
        String lokasi = inputLokasi.getText();
        String kondisi = comboKondisi.getValue();

        if (nama == null || nama.trim().isEmpty() ||
            kategori == null || lokasi == null || lokasi.trim().isEmpty() || kondisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Mohon lengkapi semua field data fasilitas!");
            alert.show();
            return;
        }

        KeluhanService service = KeluhanService.getInstance();
        int newId = service.getAllFasilitas().size() + 1;
        Fasilitas fBaru = new Fasilitas(newId, nama, kategori, lokasi, kondisi);
        
        service.getAllFasilitas().add(fBaru);
        
        if (parentController != null) {
            parentController.addFasilitasToTable(fBaru);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Fasilitas baru berhasil ditambahkan!");
        alert.showAndWait();

        tutupWindow();
    }

    @FXML
    private void tutupWindow() {
        Stage stage = (Stage) inputNama.getScene().getWindow();
        stage.close();
    }
}
