package com.mankelfas.model.misc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Kendala {
    private SimpleIntegerProperty idKeluhan;
    private SimpleStringProperty namaTeknisi;
    private SimpleStringProperty alasan;
    private SimpleStringProperty waktuDilaporkan;

    public Kendala(int idKeluhan, String namaTeknisi, String alasan, String waktuDilaporkan) {
        this.idKeluhan = new SimpleIntegerProperty(idKeluhan);
        this.namaTeknisi = new SimpleStringProperty(namaTeknisi);
        this.alasan = new SimpleStringProperty(alasan);
        this.waktuDilaporkan = new SimpleStringProperty(waktuDilaporkan);
    }

    public int getIdKeluhan() { return idKeluhan.get(); }
    public String getNamaTeknisi() { return namaTeknisi.get(); }
    public String getAlasan() { return alasan.get(); }
    public String getWaktuDilaporkan() { return waktuDilaporkan.get(); }
}
