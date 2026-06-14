package com.mankelfas.util;

import com.mankelfas.model.user.User;

public class Session {
    private static User currentUser;

    /**
     * Menyimpan data pengguna yang saat ini memegang kendali atas aplikasi.
     * 
     * @param user Entitas pengguna yang sukses melewati gerbang autentikasi
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Mengembalikan identitas pengguna yang sedang masuk.
     * Sangat berguna untuk menyesuaikan menu atau mencatat pelapor keluhan.
     * 
     * @return Objek pengguna aktif
     */
    public static User getCurrentUser() {
        return currentUser;
    }
}
