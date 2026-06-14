package com.mankelfas.repository;

import com.mankelfas.model.user.User;
import java.util.List;

public interface IUserRepository {
    /**
     * Mencocokkan pasangan email dan password dengan rekaman autentikasi di database.
     * 
     * @param email Alamat email identitas pengguna
     * @param password Password rahasia
     * @return Entitas pengguna utuh jika validasi lulus, atau kosong (null) jika gagal
     */
    User login(String email, String password);

    /**
     * Mengambil daftar seluruh pengguna dari berbagai peran (Admin, Teknisi, Mahasiswa) yang ada di database.
     * 
     * @return Kumpulan data para pengguna
     */
    List<User> getAllUsers();

    /**
     * Merekam identitas pengguna baru ke dalam tabel database.
     * 
     * @param user Detail lengkap pengguna baru
     * @return Tanda keberhasilan proses perekaman
     */
    boolean addUser(User user);

    /**
     * Menyimpan hasil modifikasi profil pengguna ke dalam database.
     * 
     * @param user Data pengguna yang sudah diperbarui
     * @return Tanda keberhasilan proses penyimpanan
     */
    boolean updateUser(User user);

    /**
     * Mencabut hak akses dengan menghapus data pengguna dari sistem permanen.
     * 
     * @param idUser Tanda pengenal pengguna yang akan dihapus
     * @return Tanda keberhasilan operasi pemusnahan
     */
    boolean deleteUser(int idUser);
}
