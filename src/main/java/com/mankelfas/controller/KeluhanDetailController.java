package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.RiwayatKeluhan;
import com.mankelfas.model.misc.Komentar;

public class KeluhanDetailController {

    @FXML private Label lblIdKeluhan;
    @FXML private Label lblStatus;
    @FXML private Label lblDeskripsi;
    
    @FXML private Label lblNamaFasilitas;
    @FXML private Label lblLokasi;
    @FXML private Label lblKategori;
    @FXML private Label lblKondisi;
    
    @FXML private TextArea txtAreaRiwayat;

    public void setKeluhanData(Keluhan k) {
        if (k == null) return;
        
        lblIdKeluhan.setText("ID: " + k.getIdKeluhan());
        lblStatus.setText("Status: " + k.getStatus().name());
        lblDeskripsi.setText("Deskripsi: " + k.getDeskripsi());
        
        Fasilitas f = k.getFasilitas();
        if (f != null) {
            lblNamaFasilitas.setText("Nama: " + f.getNama());
            lblLokasi.setText("Lokasi: " + f.getLokasi());
            lblKategori.setText("Kategori: " + f.getKategori());
            lblKondisi.setText("Kondisi Terakhir: " + f.getKondisi());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- RIWAYAT STATUS ---\n");
        for (RiwayatKeluhan r : k.getRiwayat()) {
            sb.append(r.getInfo()).append("\n");
        }
        sb.append("\n--- KOMENTAR TEKNISI ---\n");
        for (Komentar c : k.getKomentar()) {
            sb.append(c.getKomentar()).append("\n");
        }
        
        txtAreaRiwayat.setText(sb.toString());
    }

    @FXML
    private void tutupDetail() {
        Stage stage = (Stage) lblIdKeluhan.getScene().getWindow();
        stage.close();
    }
}
