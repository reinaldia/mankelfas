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
            // Mencatat jejak transisi status baru lengkap dengan stempel waktunya
            RiwayatKeluhan r = new RiwayatKeluhan(riwayat.size() + 1, statusBaru);
            riwayat.add(r);
            this.status = statusBaru;
        } catch (Exception e) {
            // Melaporkan diam-diam jika sistem jejak riwayat mengalami kendala
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
     * Mengarsipkan keluhan jika sudah selesai atau dibatalkan.
     */
    public void arsipkan() {
        this.archived = true;
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
    public void setTeknisi(Teknisi teknisi) { this.teknisi = teknisi; }
    public void setProgress(String progress) { this.progress = progress; }
    public void setEstimasiWaktu(String estimasiWaktu) { this.estimasiWaktu = estimasiWaktu; }
    public void setWaktuDiproses(java.time.LocalDateTime waktuDiproses) { this.waktuDiproses = waktuDiproses; }
    public void setTargetSelesai(java.time.LocalDateTime targetSelesai) { this.targetSelesai = targetSelesai; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
