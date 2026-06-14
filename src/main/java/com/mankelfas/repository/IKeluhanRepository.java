package com.mankelfas.repository;

import com.mankelfas.model.keluhan.Keluhan;
import java.util.List;

public interface IKeluhanRepository {
    /**
     * Menarik semua data riwayat dan rincian keluhan dari database.
     * 
     * @return Daftar seluruh keluhan
     */
    List<Keluhan> getAllKeluhan();

    /**
     * Menyimpan dokumen keluhan baru ke dalam database.
     * 
     * @param keluhan Objek keluhan yang akan direkam
     * @return Tanda keberhasilan pencatatan keluhan
     */
    boolean addKeluhan(Keluhan keluhan);

    /**
     * Mengubah status atau keterangan keluhan yang telah tersimpan.
     * 
     * @param keluhan Data keluhan dengan status terbaru
     * @return Tanda keberhasilan pembaruan keluhan
     */
    boolean updateKeluhan(Keluhan keluhan);

    /**
     * Memusnahkan data keluhan beserta riwayatnya dari database.
     * 
     * @param idKeluhan Tanda pengenal keluhan yang akan dihapus
     * @return Tanda keberhasilan operasi penghapusan
     */
    boolean deleteKeluhan(int idKeluhan);
}
