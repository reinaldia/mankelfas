package com.mankelfas.service;

import com.mankelfas.model.user.User;
import java.util.List;

public interface IUserService {
    /**
     * Memverifikasi keabsahan kredensial saat pengguna mencoba mengakses sistem.
     * 
     * @param email Alamat surat elektronik yang didaftarkan
     * @param password Password rahasia akun
     * @return Data pengguna utuh apabila verifikasi berhasil
     */
    User authenticate(String email, String password);

    /**
     * Menarik seluruh daftar pengguna yang terdaftar di dalam sistem, meliputi semua peran.
     * 
     * @return Kumpulan objek pengguna
     */
    List<User> getAllUsers();

    /**
     * Memproses pendaftaran pengguna baru ke dalam database.
     * 
     * @param user Data pengguna yang akan didaftarkan
     * @return Konfirmasi kesuksesan proses pendaftaran
     */
    boolean addUser(User user);

    /**
     * Menimpa informasi lama pengguna dengan data profil yang baru diperbarui.
     * 
     * @param user Data pengguna yang membawa perubahan
     * @return Tanda keberhasilan pembaruan profil
     */
    boolean updateUser(User user);

    /**
     * Menghapus secara mutlak akun pengguna dari catatan sistem.
     * 
     * @param idUser Nomor identitas pengguna bersangkutan
     * @return Konfirmasi apakah akun berhasil dihapus
     */
    boolean deleteUser(int idUser);
}
