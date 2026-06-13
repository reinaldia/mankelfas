package com.mankelfas.model.keluhan;

import com.mankelfas.enumeration.StatusKeluhan;
import java.util.Date;

public class RiwayatKeluhan {
    private int idRiwayat;
    private StatusKeluhan status;
    private Date waktu;

    public RiwayatKeluhan(int idRiwayat, StatusKeluhan status) {
        this.idRiwayat = idRiwayat;
        this.status = status;
        this.waktu = new Date();
    }

    public String getInfo() {
        return "Riwayat ID: " + idRiwayat + " | Status: " + status + " | Waktu: " + waktu;
    }

    public int getIdRiwayat() { return idRiwayat; }
    public StatusKeluhan getStatus() { return status; }
    public Date getWaktu() { return waktu; }
}
