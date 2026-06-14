package com.mankelfas.model.misc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Merepresentasikan rintangan atau kesulitan yang dihadapi teknisi saat proses perbaikan.
 * Mencatat keterangan masalah agar pihak terkait dapat segera mengetahui hambatannya.
 */
public class Kendala {
    private SimpleIntegerProperty idKeluhan;
    private SimpleStringProperty namaTeknisi;
    private SimpleStringProperty alasan;
    private SimpleStringProperty waktuDilaporkan;

    /**
     * Mencatat rincian masalah baru yang menghalangi perbaikan keluhan.
     * 
     * @param idKeluhan Tanda identitas keluhan yang sedang dikerjakan
     * @param namaTeknisi Identitas pelapor kendala (teknisi)
     * @param alasan Uraian singkat akar permasalahan
     * @param waktuDilaporkan Stempel waktu saat teknisi mencatat kendala tersebut
     */
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
