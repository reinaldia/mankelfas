package com.mankelfas.model.misc;

/**
 * Merepresentasikan rintangan atau kesulitan yang dihadapi teknisi saat proses perbaikan.
 * Mencatat keterangan masalah agar pihak terkait dapat segera mengetahui hambatannya.
 */
public class Kendala {
    private int idKeluhan;
    private String namaTeknisi;
    private String alasan;
    private String waktuDilaporkan;

    /**
     * Mencatat rincian masalah baru yang menghalangi perbaikan keluhan.
     * 
     * @param idKeluhan Tanda identitas keluhan yang sedang dikerjakan
     * @param namaTeknisi Identitas pelapor kendala (teknisi)
     * @param alasan Uraian singkat akar permasalahan
     * @param waktuDilaporkan Stempel waktu saat teknisi mencatat kendala tersebut
     */
    public Kendala(int idKeluhan, String namaTeknisi, String alasan, String waktuDilaporkan) {
        this.idKeluhan = idKeluhan;
        this.namaTeknisi = namaTeknisi;
        this.alasan = alasan;
        this.waktuDilaporkan = waktuDilaporkan;
    }

    public int getIdKeluhan() { return idKeluhan; }
    public String getNamaTeknisi() { return namaTeknisi; }
    public String getAlasan() { return alasan; }
    public String getWaktuDilaporkan() { return waktuDilaporkan; }
}
