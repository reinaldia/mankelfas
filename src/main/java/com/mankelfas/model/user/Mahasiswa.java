package com.mankelfas.model.user;

import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.model.keluhan.Fasilitas;
import java.util.ArrayList;
import java.util.List;

/**
 * Turunan dari User khusus untuk civitas akademika Mahasiswa.
 * Berperan sebagai pihak yang dapat membuat laporan (Keluhan) jika menemukan fasilitas rusak.
 */
public class Mahasiswa extends User {
    private String nim;
    private List<Keluhan> daftarKeluhan;

    public Mahasiswa(int idUser, String nama, String email, String password, String nim) {
        super(idUser, nama, email, password);
        this.nim = nim;
        this.daftarKeluhan = new ArrayList<>();
    }

    public Keluhan buatKeluhan(String deskripsi, String fotoBukti, Fasilitas fasilitas) {
        try {
            Keluhan keluhan = new Keluhan(daftarKeluhan.size() + 1, deskripsi, fotoBukti, this, fasilitas);
            daftarKeluhan.add(keluhan);
            return keluhan;
        } catch (Exception e) {
            System.err.println("Gagal membuat keluhan: " + e.getMessage());
            return null;
        }
    }

    public void lihatStatus(Keluhan k) {
        if (k != null) {
            System.out.println("Status Keluhan #" + k.getIdKeluhan() + ": " + k.getStatus());
        }
    }

    @Override
    public void tampilDashboard() {
        System.out.println("--- Dashboard Mahasiswa ---");
        System.out.println("Selamat datang, " + nama + " (" + nim + ")");
    }

    @Override
    public String getRole() {
        return "Mahasiswa";
    }

    public String getNim() { return nim; }
    public List<Keluhan> getDaftarKeluhan() { return daftarKeluhan; }
}
