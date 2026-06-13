package com.mankelfas.model.user;

import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.model.misc.Komentar;
import java.util.ArrayList;
import java.util.List;

/**
 * Turunan dari User yang bertindak sebagai petugas perbaikan di lapangan.
 * Memiliki spesialisasi keahlian tertentu (misal: Listrik, Pipa, Jaringan).
 */
public class Teknisi extends User {
    private String keahlian;
    private List<Keluhan> tugas;

    public Teknisi(int idUser, String nama, String email, String password, String keahlian) {
        super(idUser, nama, email, password);
        this.keahlian = keahlian;
        this.tugas = new ArrayList<>();
    }

    public void tambahTugas(Keluhan k) {
        if (k != null) {
            tugas.add(k);
        }
    }

    public void updateStatus(Keluhan k, StatusKeluhan sk, String keterangan) {
        try {
            if (k != null && tugas.contains(k)) {
                k.updateStatus(sk);
                k.tambahKomentar(new Komentar(keterangan, this));
            }
        } catch (Exception e) {
            System.err.println("Error update status keluhan: " + e.getMessage());
        }
    }

    @Override
    public void tampilDashboard() {
        System.out.println("--- Dashboard Teknisi ---");
        System.out.println("Selamat datang, " + nama + " (Keahlian: " + keahlian + ")");
    }

    @Override
    public String getRole() {
        return "Teknisi";
    }

    public String getKeahlian() { return keahlian; }
    public List<Keluhan> getTugas() { return tugas; }
}
