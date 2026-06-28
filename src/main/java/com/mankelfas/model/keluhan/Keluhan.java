package com.mankelfas.model.keluhan;

import com.mankelfas.enumeration.Prioritas;
import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.model.misc.Komentar;
import com.mankelfas.model.user.Mahasiswa;
import com.mankelfas.model.user.Teknisi;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Merupakan entitas utama yang merepresentasikan laporan kerusakan.
 * Merangkum informasi pelapor, fasilitas yang rusak, identitas teknisi, hingga riwayat perkembangan perbaikan.
 */
public class Keluhan {
    private int idKeluhan;
    private String deskripsi;
    private Date tanggal;
    private Prioritas prioritas;
    private String fotoBukti;
    private String progress;
    private String estimasiWaktu;
    private java.time.LocalDateTime waktuDiproses;
    private java.time.LocalDateTime targetSelesai;
    private boolean archived;
    private StatusKeluhan status;

    private Mahasiswa pelapor;
    private Teknisi teknisi;
    private Fasilitas fasilitas;
    private List<RiwayatKeluhan> riwayat;
    private List<Komentar> komentar;

    /**
     * Mengonstruksi wujud utuh sebuah keluhan baru yang diajukan oleh pengguna mahasiswa.
     * 
     * @param idKeluhan Tanda pengenal spesifik dari sistem database
     * @param deskripsi Uraian teks terkait kendala yang diamati pelapor
     * @param fotoBukti Alamat letak direktori tempat foto bukti disimpan
     * @param pelapor Referensi objek identitas mahasiswa yang melapor
     * @param fasilitas Referensi objek barang atau tempat yang dikeluhkan
     */
    public Keluhan(int idKeluhan, String deskripsi, String fotoBukti, Mahasiswa pelapor, Fasilitas fasilitas) {
        this.idKeluhan = idKeluhan;
        this.deskripsi = deskripsi;
        this.fotoBukti = fotoBukti;
        this.pelapor = pelapor;
        this.fasilitas = fasilitas;
        
        // Secara mandiri menandai waktu laporan dibuat secara waktu nyata
        this.tanggal = new Date();
        
        // Menetapkan status awal pada saat keluhan pertama kali dibentuk
        this.status = StatusKeluhan.DILAPORKAN;
        this.prioritas = Prioritas.RENDAH;
        this.riwayat = new ArrayList<>();
        this.komentar = new ArrayList<>();
    }

    /**
     * Menerapkan perubahan status kerja ke dalam sistem keluhan ini.
     * Bersamaan dengan itu, kejadian ini juga terekam ke dalam buku harian riwayat.
     * 
     * @param statusBaru Kondisi operasional terbaru yang hendak disematkan
     */
    public void updateStatus(StatusKeluhan statusBaru) {
        try {
            com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
            String username = currentUser != null ? currentUser.getNama() : "Sistem";
            String pesan = username + " mengubah status keluhan dari " + this.status + " menjadi " + statusBaru;
            RiwayatKeluhan rLog = new RiwayatKeluhan(riwayat.size() + 1, pesan);
            riwayat.add(rLog);
            this.status = statusBaru;
            
            // Mengirim notifikasi perubahan status kepada pelapor / sistem
            new com.mankelfas.model.misc.NotifikasiSistem().kirimNotif("Keluhan #" + this.idKeluhan + ": " + pesan);
        } catch (Exception e) {
            System.err.println("Gagal update status: " + e.getMessage());
        }
    }

    /**
     * Menyisipkan tanggapan, balasan, atau instruksi dari teknisi ke daftar catatan keluhan.
     * 
     * @param k Entitas komentar baru yang hendak dilampirkan
     */
    public void tambahKomentar(Komentar k) {
        try {
            komentar.add(k);
        } catch (Exception e) {
            System.err.println("Gagal tambah komentar: " + e.getMessage());
        }
    }

    /**
     * Memperbarui kondisi fisik fasilitas serta mencatatnya ke dalam riwayat keluhan.
     * 
     * @param kondisiBaru Kondisi terbaru dari fasilitas yang diperbaiki
     */
    public void updateKondisiFasilitas(com.mankelfas.enumeration.KondisiFasilitas kondisiBaru) {
        if (this.fasilitas != null) {
            try {
                this.fasilitas.setKondisi(kondisiBaru);
                RiwayatKeluhan r = new RiwayatKeluhan(riwayat.size() + 1, "Kondisi fasilitas diperbarui menjadi: " + kondisiBaru.getDeskripsi());
                riwayat.add(r);
            } catch (Exception e) {
                System.err.println("Gagal update kondisi fasilitas: " + e.getMessage());
            }
        }
    }

    /**
     * Memperbarui tingkat prioritas penanganan keluhan dan mencatat riwayat perubahannya.
     * 
     * @param prioBaru Tingkat prioritas terbaru yang akan diterapkan
     */
    public void updatePrioritas(Prioritas prioBaru) {
        try {
            com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
            String username = currentUser != null ? currentUser.getNama() : "Sistem";
            String pesan = username + " mengubah prioritas keluhan dari " + this.prioritas + " menjadi " + prioBaru;
            
            RiwayatKeluhan r = new RiwayatKeluhan(riwayat.size() + 1, pesan);
            riwayat.add(r);
            this.prioritas = prioBaru;
        } catch (Exception e) {}
    }
    
    /**
     * Mengarsipkan keluhan yang telah selesai atau dibatalkan agar tidak memenuhi daftar aktif.
     * Tindakan ini juga akan tercatat di dalam riwayat keluhan.
     */
    public void arsipkan() {
        this.archived = true;
        try {
            com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
            String username = currentUser != null ? currentUser.getNama() : "Sistem";
            RiwayatKeluhan r = new RiwayatKeluhan(riwayat.size() + 1, username + " mengarsipkan keluhan ini");
            riwayat.add(r);
        } catch (Exception e) {}
    }

    /**
     * Membatalkan status arsip (unarchive) pada keluhan sehingga kembali aktif.
     */
    public void batalArsip() {
        this.archived = false;
        try {
            com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
            String username = currentUser != null ? currentUser.getNama() : "Sistem";
            RiwayatKeluhan r = new RiwayatKeluhan(riwayat.size() + 1, username + " memulihkan keluhan ini dari arsip");
            riwayat.add(r);
        } catch (Exception e) {}
    }

    // Metode Akses dan Modifikasi (Getters dan Setters)
    public int getIdKeluhan() { return idKeluhan; }
    public String getDeskripsi() { return deskripsi; }
    public Date getTanggal() { return tanggal; }
    public Prioritas getPrioritas() { return prioritas; }
    public String getFotoBukti() { return fotoBukti; }
    public StatusKeluhan getStatus() { return status; }
    public Mahasiswa getPelapor() { return pelapor; }
    public Teknisi getTeknisi() { return teknisi; }
    public Fasilitas getFasilitas() { return fasilitas; }
    public String getProgress() { return progress; }
    public String getEstimasiWaktu() { return estimasiWaktu; }
    public java.time.LocalDateTime getWaktuDiproses() { return waktuDiproses; }
    public java.time.LocalDateTime getTargetSelesai() { return targetSelesai; }
    public boolean isArchived() { return archived; }
    public List<RiwayatKeluhan> getRiwayat() { return riwayat; }
    public List<Komentar> getKomentar() { return komentar; }

    public void setIdKeluhan(int idKeluhan) { this.idKeluhan = idKeluhan; }
    public void setStatus(StatusKeluhan status) { this.status = status; }
    public void setPrioritas(Prioritas prioritas) { this.prioritas = prioritas; }

    /**
     * Menugaskan atau mengganti teknisi yang menangani keluhan ini, 
     * serta mencatat proses penugasan tersebut ke dalam riwayat.
     * 
     * @param teknisiBaru Referensi objek teknisi yang baru ditugaskan
     */
    public void updateTeknisi(Teknisi teknisiBaru) {
        try {
            com.mankelfas.model.user.User currentUser = com.mankelfas.util.Session.getCurrentUser();
            String username = currentUser != null ? currentUser.getNama() : "Sistem";
            String pesan;
            
            if (this.teknisi == null) {
                pesan = username + " menugaskan keluhan ke teknisi " + teknisiBaru.getNama();
            } else if (!this.teknisi.getNama().equals(teknisiBaru.getNama())) {
                pesan = username + " mengganti teknisi dari " + this.teknisi.getNama() + " menjadi " + teknisiBaru.getNama();
            } else {
                return; // Tidak ada perubahan
            }
            
            RiwayatKeluhan r = new RiwayatKeluhan(riwayat.size() + 1, pesan);
            riwayat.add(r);
            this.teknisi = teknisiBaru;
        } catch (Exception e) {
            System.err.println("Gagal update teknisi: " + e.getMessage());
        }
    }

    public void setTeknisi(Teknisi teknisi) { this.teknisi = teknisi; }
    public void setProgress(String progress) { this.progress = progress; }
    public void setEstimasiWaktu(String estimasiWaktu) { this.estimasiWaktu = estimasiWaktu; }
    public void setWaktuDiproses(java.time.LocalDateTime waktuDiproses) { this.waktuDiproses = waktuDiproses; }
    public void setTargetSelesai(java.time.LocalDateTime targetSelesai) { this.targetSelesai = targetSelesai; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
