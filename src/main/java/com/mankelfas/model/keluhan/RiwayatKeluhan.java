package com.mankelfas.model.keluhan;

import com.mankelfas.enumeration.StatusKeluhan;
import java.util.Date;

/**
 * Mencatat jejak perubahan status dari sebuah keluhan.
 * Merekam waktu secara otomatis setiap kali keluhan beralih status dari dilaporkan hingga selesai diperbaiki.
 */
public class RiwayatKeluhan {
    private int idRiwayat;
    private StatusKeluhan status;
    private Date waktu;
    private String customInfo;
    private boolean isSaved = false;

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { this.isSaved = saved; }

    /**
     * Membangun objek rekam jejak untuk mendokumentasikan transisi status keluhan.
     * 
     * @param idRiwayat Urutan log catatan yang berlaku pada sesi tersebut
     * @param status Kondisi target yang menjadi alasan terbentuknya riwayat ini
     */
    
    public RiwayatKeluhan(int idRiwayat, String customInfo) {
        this.idRiwayat = idRiwayat;
        this.customInfo = customInfo;
        this.waktu = new Date();
    }
    
    public RiwayatKeluhan(int idRiwayat, String customInfo, Date waktu) {
        this.idRiwayat = idRiwayat;
        this.customInfo = customInfo;
        this.waktu = waktu;
    }
    
    public RiwayatKeluhan(int idRiwayat, StatusKeluhan status) {
        this.idRiwayat = idRiwayat;
        this.status = status;
        
        // Langsung mengunci stempel waktu seketika kejadian dicatat
        this.waktu = new Date();
    }

    /**
     * Menyajikan resume singkat terkait sebuah catatan sejarah keluhan.
     * 
     * @return Teks terformat memuat nomor urut, status terkait, beserta catatan waktunya
     */
    public String getInfo() {
        // Format waktu agar lebih mudah dibaca
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedWaktu = sdf.format(waktu);
        
        if (customInfo != null) {
            return "Waktu: " + formattedWaktu + " | " + customInfo;
        }
        return "Waktu: " + formattedWaktu + " | Status: " + status;
    }

    public String getPesan() {
        if (customInfo != null) return customInfo;
        return "Status berubah menjadi: " + status;
    }

    public int getIdRiwayat() { return idRiwayat; }
    public StatusKeluhan getStatus() { return status; }
    public Date getWaktu() { return waktu; }
}
