package com.mankelfas.model.user;

/**
 * Kelas abstrak dasar yang menjadi cetak biru untuk seluruh tipe pengguna di sistem Mankelfas.
 * Menyimpan data autentikasi inti (email, password) agar bisa diwariskan ke Admin, Mahasiswa, maupun Teknisi.
 */
public abstract class User {
    protected int idUser;
    protected String nama;
    protected String email;
    protected String password;

    public User(int idUser, String nama, String email, String password) {
        this.idUser = idUser;
        this.nama = nama;
        this.email = email;
        this.password = password;
    }

    /**
     * Memeriksa apakah input kredensial (email & password) sesuai dengan data asli milik user ini.
     */
    public boolean login(String email, String password) {
        try {
            return this.email.equals(email) && this.password.equals(password);
        } catch (Exception e) {
            System.err.println("Error during login verification: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        System.out.println(nama + " has logged out.");
    }

    public abstract void tampilDashboard();
    public abstract String getRole();
    
    // Getters
    public int getIdUser() { return idUser; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
}
