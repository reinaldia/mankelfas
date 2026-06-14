package com.mankelfas.util;

import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

/**
 * Utilitas untuk mengelola navigasi Single-Page Application (SPA).
 * Memungkinkan pergantian konten di tengah (center) BorderPane utama tanpa memunculkan jendela baru.
 */
public class Navigator {
    private static BorderPane mainPane;
    private static Node homeContent;

    /**
     * Mendaftarkan BorderPane utama dan konten beranda saat pertama kali masuk ke dashboard.
     */
    public static void registerMainPane(BorderPane pane, Node home) {
        mainPane = pane;
        homeContent = home;
    }

    /**
     * Mengganti konten yang tampil di bagian tengah aplikasi.
     * 
     * @param content Node tampilan baru (seperti halaman profil, akun, dll.)
     */
    public static void navigate(Node content) {
        if (mainPane != null && content != null) {
            mainPane.setCenter(content);
        }
    }

    /**
     * Mengembalikan tampilan ke beranda (dashboard utama).
     */
    public static void goHome() {
        if (mainPane != null && homeContent != null) {
            mainPane.setCenter(homeContent);
        }
    }
}
