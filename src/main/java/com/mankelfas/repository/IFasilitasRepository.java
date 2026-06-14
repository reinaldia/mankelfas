package com.mankelfas.repository;

import com.mankelfas.model.keluhan.Fasilitas;
import java.util.List;

public interface IFasilitasRepository {
    /**
     * Membaca dan mengembalikan seluruh data fasilitas yang tercatat di database.
     * 
     * @return Kumpulan entitas fasilitas
     */
    List<Fasilitas> getAllFasilitas();

    /**
     * Memasukkan data fasilitas baru ke dalam tempat penyimpanan permanen.
     * 
     * @param fasilitas Entitas fasilitas yang ingin ditambahkan
     * @return Status keberhasilan operasi penambahan
     */
    boolean addFasilitas(Fasilitas fasilitas);

    /**
     * Memperbarui detail keterangan pada suatu fasilitas di database.
     * 
     * @param fasilitas Entitas fasilitas dengan data termutakhir
     * @return Status keberhasilan operasi pembaruan
     */
    boolean updateFasilitas(Fasilitas fasilitas);

    /**
     * Menghapus baris data fasilitas dari database berdasarkan nomor identitasnya.
     * 
     * @param idFasilitas Nomor pengenal khusus fasilitas
     * @return Status keberhasilan operasi penghapusan
     */
    boolean deleteFasilitas(int idFasilitas);
}
