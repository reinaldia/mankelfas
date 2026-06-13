package com.mankelfas.service;

import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.repository.IFasilitasRepository;
import com.mankelfas.repository.FasilitasRepository;
import com.mankelfas.repository.IKeluhanRepository;
import com.mankelfas.repository.KeluhanRepository;

import java.util.List;

public class KeluhanService implements IKeluhanService {

    private static KeluhanService instance;

    private final IKeluhanRepository keluhanRepository;
    private final IFasilitasRepository fasilitasRepository;

    private KeluhanService() {
        this.keluhanRepository = new KeluhanRepository();
        this.fasilitasRepository = new FasilitasRepository();
    }

    public static KeluhanService getInstance() {
        if (instance == null) {
            instance = new KeluhanService();
        }
        return instance;
    }

    public List<Keluhan> getAllKeluhan() {
        return keluhanRepository.getAllKeluhan();
    }

    /**
     * Menyimpan data keluhan baru.
     * Sistem akan memastikan deskripsinya cukup panjang, ada fasilitas yang dipilih, dan pelapornya valid.
     */
    public void addKeluhan(Keluhan k) {
        if (k == null) {
            throw new IllegalArgumentException("Data keluhan tidak boleh kosong.");
        }
        if (k.getDeskripsi() == null || k.getDeskripsi().trim().length() < 10) {
            throw new IllegalArgumentException("Deskripsi keluhan harus detail (minimal 10 karakter).");
        }
        if (k.getFasilitas() == null) {
            throw new IllegalArgumentException("Harus memilih fasilitas yang dikeluhkan.");
        }
        if (k.getPelapor() == null) {
            throw new IllegalArgumentException("Pelapor keluhan tidak valid.");
        }
        keluhanRepository.addKeluhan(k);
    }
    
    public void updateKeluhan(Keluhan k) {
        if (k == null || k.getIdKeluhan() <= 0) {
            throw new IllegalArgumentException("Data keluhan tidak valid untuk diupdate.");
        }
        keluhanRepository.updateKeluhan(k);
    }
    
    public void deleteKeluhan(int idKeluhan) {
        if (idKeluhan <= 0) {
            throw new IllegalArgumentException("ID keluhan tidak valid.");
        }
        keluhanRepository.deleteKeluhan(idKeluhan);
    }

    public List<Fasilitas> getAllFasilitas() {
        return fasilitasRepository.getAllFasilitas();
    }
    
    /**
     * Menambahkan fasilitas baru ke dalam sistem.
     * Nama dan lokasi fasilitas wajib diisi agar teknisi tidak bingung saat mencari lokasinya nanti.
     */
    public void addFasilitas(Fasilitas f) {
        if (f == null) {
            throw new IllegalArgumentException("Data fasilitas tidak boleh kosong.");
        }
        if (f.getNama() == null || f.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama fasilitas harus diisi.");
        }
        if (f.getLokasi() == null || f.getLokasi().trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasi fasilitas harus diisi agar teknisi mudah mencarinya.");
        }
        fasilitasRepository.addFasilitas(f);
    }
    
    public void updateFasilitas(Fasilitas f) {
        if (f == null || f.getIdFasilitas() <= 0) {
            throw new IllegalArgumentException("Data fasilitas tidak valid untuk diupdate.");
        }
        if (f.getNama() == null || f.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama fasilitas harus diisi.");
        }
        fasilitasRepository.updateFasilitas(f);
    }
    
    public void deleteFasilitas(int idFasilitas) {
        if (idFasilitas <= 0) {
            throw new IllegalArgumentException("ID fasilitas tidak valid.");
        }
        fasilitasRepository.deleteFasilitas(idFasilitas);
    }
}
