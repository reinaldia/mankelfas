package com.mankelfas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mankelfas.model.misc.Kendala;
import com.mankelfas.repository.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class KendalaController {

    @FXML private TableView<Kendala> tabelKendala;
    @FXML private TableColumn<Kendala, Integer> colIdKeluhan;
    @FXML private TableColumn<Kendala, String> colTeknisi;
    @FXML private TableColumn<Kendala, String> colAlasan;
    @FXML private TableColumn<Kendala, String> colWaktu;

    @FXML
    public void initialize() {
        colIdKeluhan.setCellValueFactory(new PropertyValueFactory<>("idKeluhan"));
        colTeknisi.setCellValueFactory(new PropertyValueFactory<>("namaTeknisi"));
        colAlasan.setCellValueFactory(new PropertyValueFactory<>("alasan"));
        colWaktu.setCellValueFactory(new PropertyValueFactory<>("waktuDilaporkan"));

        loadData();
    }

    private void loadData() {
        ObservableList<Kendala> list = FXCollections.observableArrayList();
        String sql = "SELECT k.id_keluhan, u.nama, k.alasan_kendala, k.waktu_dilaporkan " +
                     "FROM kendala_teknisi k " +
                     "JOIN users u ON k.id_teknisi = u.id_user " +
                     "ORDER BY k.waktu_dilaporkan DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Kendala(
                    rs.getInt("id_keluhan"),
                    rs.getString("nama"),
                    rs.getString("alasan_kendala"),
                    rs.getTimestamp("waktu_dilaporkan").toString()
                ));
            }
            tabelKendala.setItems(list);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
