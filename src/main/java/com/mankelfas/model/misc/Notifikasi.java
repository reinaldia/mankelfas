package com.mankelfas.model.misc;

import java.util.Date;

public class Notifikasi {
    private int idNotif;
    private String pesan;
    private Date tanggal;
    private boolean dibaca;

    public Notifikasi(int idNotif, String pesan) {
        this.idNotif = idNotif;
        this.pesan = pesan;
        this.tanggal = new Date();
        this.dibaca = false;
    }

    public void kirimNotif() {
        try {
            System.out.println("Notifikasi Baru: " + pesan);
            this.dibaca = true;
        } catch (Exception e) {
            System.err.println("Gagal mengirim notifikasi: " + e.getMessage());
        }
    }

    public int getIdNotif() { return idNotif; }
    public String getPesan() { return pesan; }
    public Date getTanggal() { return tanggal; }
    public boolean isDibaca() { return dibaca; }
}
