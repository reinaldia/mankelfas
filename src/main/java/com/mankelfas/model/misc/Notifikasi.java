package com.mankelfas.model.misc;

import java.util.Date;

/**
 * Merepresentasikan satu buah pesan pemberitahuan dalam sistem.
 * Memuat isi pesan, waktu pengiriman, serta status apakah pesan tersebut sudah dibaca atau belum.
 */
public class Notifikasi {
    private int idNotif;
    private String pesan;
    private Date tanggal;
    private boolean dibaca;

    /**
     * Menyusun objek pesan pemberitahuan yang siap dikirimkan kepada penerima.
     * 
     * @param idNotif Nomor unik pembeda setiap laporan notifikasi
     * @param pesan Inti berita atau peringatan yang hendak dipancarkan
     */
    public Notifikasi(int idNotif, String pesan) {
        this.idNotif = idNotif;
        this.pesan = pesan;
        // Menetapkan kapan pesan peringatan itu dicetak pertama kali
        this.tanggal = new Date();
        // Menandai indikator bahwa pesan tersebut berstatus belum dilihat
        this.dibaca = false;
    }

    /**
     * Memancarkan isi peringatan langsung ke layar teks konsol.
     * Sekaligus mengganti tanda indikator pesan menjadi "telah dibaca".
     */
    public void kirimNotif() {
        try {
            System.out.println("Notifikasi Baru: " + pesan);
            this.dibaca = true;
        } catch (Exception e) {
            // Mencegat gangguan tidak terduga tanpa merusak alur aplikasi utama
            System.err.println("Gagal mengirim notifikasi: " + e.getMessage());
        }
    }

    public int getIdNotif() { return idNotif; }
    public String getPesan() { return pesan; }
    public Date getTanggal() { return tanggal; }
    public boolean isDibaca() { return dibaca; }
}
