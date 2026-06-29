package com.mankelfas.model.misc;

import java.util.Date;
import com.mankelfas.model.user.User;

/**
 * Menyimpan catatan diskusi atau keterangan tambahan pada setiap keluhan.
 * Mendukung penambahan pesan teks maupun lampiran foto sebagai bukti tindak lanjut perbaikan.
 */
public class Komentar {
    private String isiKomentar;
    private Date tanggal;
    private User pengirim;
    private String fotoBukti;

    /**
     * Membuat balasan percakapan berbentuk teks murni tanpa sisipan media pendukung.
     * 
     * @param isiKomentar Teks uraian tanggapan
     * @param pengirim Identitas pembuat catatan (misal: Admin, Teknisi)
     */
    public Komentar(String isiKomentar, User pengirim) {
        this.isiKomentar = isiKomentar;
        this.pengirim = pengirim;
        // Mengunci jejak waktu penulisan secara langsung saat catatan terbentuk
        this.tanggal = new Date();
    }

    /**
     * Membentuk respons interaksi lengkap dengan lampiran foto sebagai barang bukti.
     * 
     * @param isiKomentar Uraian penjelasan tambahan
     * @param pengirim Pemilik identitas penulis pesan
     * @param fotoBukti Jalur penunjuk (path) menuju file foto terunggah
     */
    public Komentar(String isiKomentar, User pengirim, String fotoBukti) {
        this.isiKomentar = isiKomentar;
        this.pengirim = pengirim;
        this.tanggal = new Date();
        this.fotoBukti = fotoBukti;
    }

    /**
     * Merangkai elemen-elemen pesan menjadi satu susunan teks yang runut.
     * 
     * @return Format teks utuh yang layak baca (contoh: [Tanggal] Pengirim: Pesan)
     */
    public String getKomentar() {
        return "[" + tanggal + "] " + pengirim.getNama() + ": " + isiKomentar;
    }

    public String getIsiKomentar() { return isiKomentar; }
    public Date getTanggal() { return tanggal; }
    public User getPengirim() { return pengirim; }
    public String getFotoBukti() { return fotoBukti; }
    public void setFotoBukti(String fotoBukti) { this.fotoBukti = fotoBukti; }
}
