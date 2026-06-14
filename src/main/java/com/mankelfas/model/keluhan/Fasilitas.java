package com.mankelfas.model.keluhan;

/**
 * Merepresentasikan aset atau barang inventaris yang terdaftar di sistem.
 * Menyimpan keterangan nama fasilitas, lokasi penempatan, serta status kelayakan atau kondisinya.
 */
public class Fasilitas {
    private int idFasilitas;
    private String nama;
    private String kategori;
    private String lokasi;
    private String kondisi;

    /**
     * Membentuk objek entitas fasilitas baru beserta segenap kelengkapannya.
     * 
     * @param idFasilitas Nomor urut atau identitas unik dari fasilitas
     * @param nama Sebutan atau nama perangkat fasilitas
     * @param kategori Kelompok penggolongan tipe fasilitas
     * @param lokasi Tempat spesifik fasilitas tersebut diletakkan
     * @param kondisi Status fungsionalitas dari fasilitas saat ini
     */
    public Fasilitas(int idFasilitas, String nama, String kategori, String lokasi, String kondisi) {
        this.idFasilitas = idFasilitas;
        this.nama = nama;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.kondisi = kondisi;
    }

    /**
     * Mengembalikan rangkaian teks singkat yang menjelaskan identitas dasar fasilitas ini.
     * Sangat berguna saat fasilitas perlu ditampilkan ke dalam menu pilihan.
     * 
     * @return Teks gabungan nama, lokasi, dan kategori fasilitas
     */
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
