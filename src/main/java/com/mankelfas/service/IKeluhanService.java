package com.mankelfas.service;

import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import java.util.List;

public interface IKeluhanService {
    /**
     * Mengambil keseluruhan daftar rekaman keluhan yang ada di dalam sistem.
     * 
     * @return Kumpulan data keluhan
     */
    List<Keluhan> getAllKeluhan();

    /**
     * Menambahkan laporan keluhan baru dari pengguna ke dalam tempat penyimpanan.
     * 
     * @param k Entitas keluhan yang hendak disimpan
     */
    void addKeluhan(Keluhan k);

    /**
     * Menerapkan perubahan terbaru pada data keluhan yang sudah ada sebelumnya.
     * 
     * @param k Entitas keluhan dengan data terbaru
     */
    void updateKeluhan(Keluhan k);

    /**
     * Menghapus secara permanen sebuah rekaman keluhan berdasarkan nomor pengenalnya.
     * 
     * @param idKeluhan Tanda pengenal keluhan yang akan dibuang
     */
    void deleteKeluhan(int idKeluhan);

    /**
     * Mengambil daftar lengkap sarana dan prasarana (fasilitas) yang diawasi sistem.
     * 
     * @return Kumpulan data fasilitas
     */
    List<Fasilitas> getAllFasilitas();

    /**
     * Mendaftarkan sarana baru ke dalam inventaris fasilitas sistem.
     * 
     * @param f Entitas fasilitas yang ingin ditambahkan
     */
    void addFasilitas(Fasilitas f);

    /**
     * Menyimpan pembaruan keterangan atau kondisi pada suatu fasilitas yang ada.
     * 
     * @param f Entitas fasilitas dengan informasi yang telah diperbarui
     */
    void updateFasilitas(Fasilitas f);

    /**
     * Mencabut atau menghapus data fasilitas dari daftar inventaris aktif.
     * 
     * @param idFasilitas Tanda identitas fasilitas yang menjadi target
     */
    void deleteFasilitas(int idFasilitas);
}
