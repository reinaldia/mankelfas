package com.mankelfas.model.user;

import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.model.keluhan.Fasilitas;
import java.util.ArrayList;
import java.util.List;

/**
 * Merepresentasikan pengguna dengan peran sebagai Mahasiswa.
 * Menyimpan informasi Nomor Induk Mahasiswa (NIM) serta daftar keluhan fasilitas yang telah mereka laporkan.
 */
public class Mahasiswa extends User {
    private String nim;
    private List<Keluhan> daftarKeluhan;

    /**
     * Menyiapkan wujud data mahasiswa lengkap dengan identitas kampus dan repositori laporannya.
     * 
     * @param idUser Tanda pengenal sistem umum
     * @param nama Sebutan lengkap pemilik akun
     * @param email Alamat kontak surat elektronik
     * @param password Password rahasia
     * @param nim Nomor Induk Mahasiswa sebagai identitas akademik
     */
    public Mahasiswa(int idUser, String nama, String email, String password, String nim) {
        super(idUser, nama, email, password);
        this.nim = nim;
        // Mempersiapkan wadah kosong untuk menampung riwayat pelaporan secara mandiri
        this.daftarKeluhan = new ArrayList<>();
    }

    /**
     * Menginisiasi formulasi laporan keluhan baru dan langsung menyimpannya ke daftar pribadi.
     * 
     * @param deskripsi Rincian teks mengenai kerusakan yang dialami
     * @param fotoBukti Jejak alamat file foto pendukung
     * @param fasilitas Target fasilitas atau inventaris yang dikeluhkan
     * @return Objek keluhan yang berhasil dicetak
     */
    public Keluhan buatKeluhan(String deskripsi, String fotoBukti, Fasilitas fasilitas) {
        try {
            Keluhan keluhan = new Keluhan(daftarKeluhan.size() + 1, deskripsi, fotoBukti, this, fasilitas);
            daftarKeluhan.add(keluhan);
            return keluhan;
        } catch (Exception e) {
            // Mencatat diam-diam apabila ada interupsi gagal lapor
            System.err.println("Gagal membuat keluhan: " + e.getMessage());
            return null;
        }
    }

    /**
     * Menyajikan tulisan teks yang mengabarkan tahap pengerjaan suatu keluhan ke layar konsol.
     * 
     * @param k Objek keluhan yang akan ditinjau
     */
    public void lihatStatus(Keluhan k) {
        if (k != null) {
            System.out.println("Status Keluhan #" + k.getIdKeluhan() + ": " + k.getStatus());
        }
    }

    /**
     * Menampilkan sambutan selamat datang di dasbor layar teks konsol untuk Mahasiswa.
     */
    @Override
    public void tampilDashboard() {
        System.out.println("--- Dashboard Mahasiswa ---");
        System.out.println("Selamat datang, " + nama + " (" + nim + ")");
    }

    /**
     * Mengembalikan tanda jabatan atau fungsional kelas.
     * 
     * @return Teks identifikasi hak kelas Mahasiswa
     */
    @Override
    public String getRole() {
        return "Mahasiswa";
    }

    public String getNim() { return nim; }
    public List<Keluhan> getDaftarKeluhan() { return daftarKeluhan; }
}
