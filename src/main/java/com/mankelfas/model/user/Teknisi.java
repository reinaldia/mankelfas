package com.mankelfas.model.user;

import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.model.misc.Komentar;
import java.util.ArrayList;
import java.util.List;

/**
 * Merepresentasikan pengguna dengan peran sebagai Teknisi.
 * Menyimpan informasi spesialisasi keahlian perbaikan serta daftar tugas penanganan keluhan yang sedang dikerjakan.
 */
public class Teknisi extends User {
    private String keahlian;
    private List<Keluhan> tugas;

    /**
     * Mengonstruksi struktur data teknisi bersamaan dengan rekam keahlian perbaikannya.
     * 
     * @param idUser Identitas unik database internal
     * @param nama Panggilan lengkap teknisi
     * @param email Alamat kotak surat elektronik
     * @param password Password akses
     * @param keahlian Bidang perbaikan yang dikuasai
     */
    public Teknisi(int idUser, String nama, String email, String password, String keahlian) {
        super(idUser, nama, email, password);
        this.keahlian = keahlian;
        // Menyiapkan repositori kosong khusus menangani mandat pekerjaan
        this.tugas = new ArrayList<>();
    }

    /**
     * Menerima limpahan tugas baru yang ditugaskan secara eksklusif ke teknisi ini.
     * 
     * @param k Entitas keluhan yang dimandatkan
     */
    public void tambahTugas(Keluhan k) {
        if (k != null) {
            tugas.add(k);
        }
    }

    /**
     * Mendorong penyesuaian tahap kerja terbaru dan menyisipkan catatan pelaksanaan di dalamnya.
     * 
     * @param k Keluhan yang sedang ditangani
     * @param sk Konstanta tanda perkembangan pekerjaan terbaru
     * @param keterangan Teks penjelasan atau instruksi pendukung
     */
    public void updateStatus(Keluhan k, StatusKeluhan sk, String keterangan) {
        try {
            // Memastikan keluhan sah dan benar-benar menjadi tanggung jawab teknisi ini
            if (k != null && tugas.contains(k)) {
                k.updateStatus(sk);
                k.tambahKomentar(new Komentar(keterangan, this));
            }
        } catch (Exception e) {
            // Mengamankan alur aplikasi walau pencatatan data status menemukan jalan buntu
            System.err.println("Error update status keluhan: " + e.getMessage());
        }
    }

    /**
     * Memunculkan ucapan selamat datang di layar panel kontrol sistem teks dasar konsol.
     */
    @Override
    public void tampilDashboard() {
        System.out.println("--- Dashboard Teknisi ---");
        System.out.println("Selamat datang, " + nama + " (Keahlian: " + keahlian + ")");
    }

    /**
     * Menerjemahkan kelompok wewenang atas operasi objek berjalan.
     * 
     * @return Kode akses yang merepresentasikan kelas Teknisi
     */
    @Override
    public String getRole() {
        return "Teknisi";
    }

    public String getKeahlian() { return keahlian; }
    public List<Keluhan> getTugas() { return tugas; }
}
