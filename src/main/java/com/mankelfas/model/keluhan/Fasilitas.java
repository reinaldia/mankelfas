package com.mankelfas.model.keluhan;

/**
 * Merepresentasikan sebuah objek fasilitas di kampus (misal: AC ruang kelas, Proyektor, dsb).
 * Digunakan sebagai titik acuan saat ada pelaporan kerusakan.
 */
public class Fasilitas {
    private int idFasilitas;
    private String nama;
    private String kategori;
    private String lokasi;
    private String kondisi;

    public Fasilitas(int idFasilitas, String nama, String kategori, String lokasi, String kondisi) {
        this.idFasilitas = idFasilitas;
        this.nama = nama;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.kondisi = kondisi;
    }

    public String getInfo() {
        return nama + " - " + lokasi + " (" + kategori + ")";
    }

    public int getIdFasilitas() { return idFasilitas; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public String getLokasi() { return lokasi; }
    public String getKondisi() { return kondisi; }
    public void setIdFasilitas(int idFasilitas) { this.idFasilitas = idFasilitas; }
}
