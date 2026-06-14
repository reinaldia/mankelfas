package com.mankelfas.service;

import com.mankelfas.model.user.User;
import com.mankelfas.repository.IUserRepository;
import com.mankelfas.repository.UserRepository;
import java.util.List;

public class UserService implements IUserService {

    private final IUserRepository userRepository;
    
    // Implementasi pola desain Singleton untuk memastikan hanya ada satu instansi layanan
    private static UserService instance;

    /**
     * Menyiapkan alat komunikasi data untuk mengakses tabel pengguna di repositori.
     */
    public UserService() {
        this.userRepository = new UserRepository();
    }
    
    /**
     * Mengamankan satu pintu akses (Singleton) untuk operasional manajemen pengguna.
     * 
     * @return Instansi aktif UserService
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Menilai kelayakan upaya masuk ke dalam aplikasi menggunakan email dan password.
     * 
     * @param email Kredensial surat elektronik
     * @param password Kredensial password
     * @return Objek User apabila valid, sebaliknya null
     */
    @Override
    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email dan password tidak boleh kosong.");
        }
        return userRepository.login(email, password);
    }

    /**
     * Mengambil kompilasi seluruh pengguna terdaftar dari database.
     * 
     * @return Daftar pengguna lintas jabatan
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    /**
     * Memasukkan identitas anggota baru ke dalam lingkungan sistem setelah lulus sensor keamanan.
     * 
     * @param user Data pengguna baru
     * @return Keberhasilan penambahan
     */
    @Override
    public boolean addUser(User user) {
        validateUser(user);
        
        // Memastikan email tidak duplikat sebelum menyimpannya ke database
        for (User existingUser : userRepository.getAllUsers()) {
            if (existingUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new IllegalArgumentException("Email sudah terdaftar, silakan gunakan email lain.");
            }
        }
        
        return userRepository.addUser(user);
    }

    /**
     * Menyesuaikan detail profil pengguna yang telah ada dengan informasi mutakhir.
     * 
     * @param user Pengguna beserta detail perubahannya
     * @return Status keberhasilan modifikasi
     */
    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getIdUser() <= 0) {
            throw new IllegalArgumentException("Data pengguna tidak valid untuk diupdate.");
        }
        validateUser(user);
        
        // Memastikan email baru yang diinput tidak dipakai orang lain sebelum di-update ke database.
        // Proses ini akan dilewati jika emailnya masih sama dengan milik user itu sendiri.
        for (User existingUser : userRepository.getAllUsers()) {
            if (existingUser.getEmail().equalsIgnoreCase(user.getEmail()) && existingUser.getIdUser() != user.getIdUser()) {
                throw new IllegalArgumentException("Email sudah dipakai oleh pengguna lain.");
            }
        }
        
        return userRepository.updateUser(user);
    }

    /**
     * Menghapus hak dan keberadaan pengguna dari sistem.
     * 
     * @param idUser Penanda pengguna yang akan dikeluarkan
     * @return Konfirmasi kesuksesan eliminasi
     */
    @Override
    public boolean deleteUser(int idUser) {
        if (idUser <= 0) {
            throw new IllegalArgumentException("ID pengguna tidak valid.");
        }
        return userRepository.deleteUser(idUser);
    }
    
    /**
     * Memeriksa kelengkapan dan format data User.
     * Akan langsung membatalkan proses (melempar error) jika ada data yang kosong atau formatnya salah,
     * sehingga aplikasi tidak membuang waktu memanggil database untuk data yang sudah pasti ditolak.
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Data pengguna tidak boleh kosong.");
        }
        if (user.getNama() == null || user.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pengguna harus diisi.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password harus memiliki minimal 6 karakter.");
        }
    }
}
