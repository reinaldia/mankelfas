package com.mankelfas.model.user;

import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.enumeration.Prioritas;

/**
 * Merepresentasikan pengguna dengan peran sebagai Administrator.
 * Menyimpan informasi tingkat akses dan memiliki wewenang untuk mendelegasikan tugas perbaikan kepada teknisi.
 */
public class Admin extends User {
    private String level;

    /**
     * Mengonstruksi entitas administrator baru lengkap dengan identitas dan tingkat kewenangannya.
     * 
     * @param idUser Tanda pengenal spesifik dari database
     * @param nama Nama lengkap yang akan ditampilkan
     * @param email Alamat surat elektronik untuk keperluan masuk
     * @param password Password rahasia akun
     * @param level Tingkatan atau pangkat hak akses admin (misalnya: Superadmin, Reguler)
     */
    public Admin(int idUser, String nama, String email, String password, String level) {
        super(idUser, nama, email, password);
        this.level = level;
    }

    /**
     * Memeriksa dan memberikan persetujuan akhir pada keluhan sebelum diproses lebih lanjut.
     * 
     * @param k Entitas keluhan yang sedang diperiksa
     * @param disetujui Tanda persetujuan atau penolakan keluhan
     */
    public void verifikasiKeluhan(Keluhan k, boolean disetujui) {
        try {
            if (k != null) {
                // Logika verifikasi
            }
        } catch (Exception e) {
            // Melaporkan ke layar log jika terjadi kendala pada sistem verifikasi
            System.err.println("Error memverifikasi keluhan: " + e.getMessage());
        }
    }

    /**
     * Mendelegasikan tugas penanganan keluhan kepada seorang teknisi secara spesifik.
     * Menjalankan fungsi sinkronisasi antara objek keluhan dan daftar tugas teknisi.
     * 
     * @param k Keluhan yang membutuhkan perbaikan
     * @param t Teknisi yang ditunjuk untuk menyelesaikan masalah
     */
    public void assignTeknisi(Keluhan k, Teknisi t) {
        try {
            if (k != null && t != null) {
                k.setTeknisi(t);
                t.tambahTugas(k);
            }
        } catch (Exception e) {
            // Mengamankan aplikasi dari kelumpuhan mendadak jika tugas gagal dibebankan
            System.err.println("Error assign teknisi: " + e.getMessage());
        }
    }
    
    /**
     * Menetapkan tingkat urgensi dari suatu keluhan untuk menentukan prioritas perbaikannya.
     * 
     * @param k Objek keluhan yang sedang dinilai
     * @param prioritas Tingkat kepentingan baru yang ditetapkan
     */
    public void setPrioritas(Keluhan k, Prioritas prioritas) {
        try {
            if (k != null) {
                k.setPrioritas(prioritas);
            }
        } catch (Exception e) {
            System.err.println("Error set prioritas: " + e.getMessage());
        }
    }

    /**
     * Menampilkan sambutan teks UI di layar konsol khusus untuk peran Admin.
     */
    @Override
    public void tampilDashboard() {
        System.out.println("--- Dashboard Admin ---");
        System.out.println("Selamat datang, " + nama + " (Level: " + level + ")");
    }

    /**
     * Memberikan penanda identitas fungsional kelas ini.
     * 
     * @return Teks identifikasi jabatan (Admin)
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    public String getLevel() { return level; }
}
