package com.mankelfas.model.user;

import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.enumeration.Prioritas;

/**
 * Turunan dari User yang memiliki hak akses penuh terhadap aplikasi.
 * Bertugas mengelola akun, mendelegasikan teknisi ke suatu keluhan, dan memantau seluruh sistem.
 */
public class Admin extends User {
    private String level;

    public Admin(int idUser, String nama, String email, String password, String level) {
        super(idUser, nama, email, password);
        this.level = level;
    }

    public void verifikasiKeluhan(Keluhan k, boolean disetujui) {
        try {
            if (k != null) {
                // Logika verifikasi
            }
        } catch (Exception e) {
            System.err.println("Error memverifikasi keluhan: " + e.getMessage());
        }
    }

    public void assignTeknisi(Keluhan k, Teknisi t) {
        try {
            if (k != null && t != null) {
                k.setTeknisi(t);
                t.tambahTugas(k);
            }
        } catch (Exception e) {
            System.err.println("Error assign teknisi: " + e.getMessage());
        }
    }
    
    public void setPrioritas(Keluhan k, Prioritas prioritas) {
        try {
            if (k != null) {
                k.setPrioritas(prioritas);
            }
        } catch (Exception e) {
            System.err.println("Error set prioritas: " + e.getMessage());
        }
    }

    @Override
    public void tampilDashboard() {
        System.out.println("--- Dashboard Admin ---");
        System.out.println("Selamat datang, " + nama + " (Level: " + level + ")");
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public String getLevel() { return level; }
}
