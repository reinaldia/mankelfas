package com.mankelfas.model.user;

/**
 * Menjadi kerangka dasar untuk seluruh jenis pengguna dalam sistem.
 * Menyimpan informasi inti seperti identitas, email, dan password untuk digunakan oleh setiap pengguna yang terdaftar.
 */
public abstract class User {
    protected int idUser;
    protected String nama;
    protected String email;
    protected String password;

    /**
     * Mewajibkan pengisian identitas universal saat menginisiasi model pengguna manapun.
     * 
     * @param idUser Nomor identitas dasar bawaan database
     * @param nama Sebutan nama publik milik akun bersangkutan
     * @param email Email atau alamat elektronik autentikasi login
     * @param password Kata rahasia akun
     */
    public User(int idUser, String nama, String email, String password) {
        this.idUser = idUser;
        this.nama = nama;
        this.email = email;
        this.password = password;
    }

    /**
     * Memverifikasi kebenaran pasangan identitas dan rahasia akun.
     * Berguna saat memproses percobaan masuk ke dalam lingkungan sistem.
     * 
     * @param email Isian teks surat elektronik pengguna
     * @param password Isian teks password
     * @return Tanda benar bila verifikasi sukses dicapai
     */
    public boolean login(String email, String password) {
        try {
            return this.email.equals(email) && this.password.equals(password);
        } catch (Exception e) {
            // Mencegat kondisi error teknis saat mencocokkan rangkaian teks
            System.err.println("Error during login verification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Melaporkan ke sistem konsol bahwa sesi pengguna ini resmi dilepas (logout).
     */
    public void logout() {
        System.out.println(nama + " has logged out.");
    }

    /**
     * Wajib diterapkan oleh anak-anak pewaris untuk menayangkan UI pembuka.
     */
    public abstract void tampilDashboard();
    
    /**
     * Wajib diterapkan oleh sub-kelas guna menyerahkan identifikasi spesifik hak perannya.
     * 
     * @return Gelar peran unik seperti "Admin", "Teknisi", dsb.
     */
    public abstract String getRole();
    
    // Metode Akses (Getters)
    public int getIdUser() { return idUser; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public void setNama(String nama) { this.nama = nama; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
