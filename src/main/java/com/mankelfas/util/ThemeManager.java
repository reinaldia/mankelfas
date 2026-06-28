package com.mankelfas.util;

import javafx.scene.Scene;
import java.util.prefs.Preferences;

/**
 * Mengelola status mode warna (Terang/Gelap) secara global untuk aplikasi.
 */
public class ThemeManager {

    private static final String PREF_DARK_MODE = "isDarkMode";
    private static Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    // Secara default menggunakan Light Mode (false)
    private static boolean isDarkMode = prefs.getBoolean(PREF_DARK_MODE, false);

    /**
     * Mengecek apakah mode gelap sedang aktif.
     */
    public static boolean isDarkMode() {
        return isDarkMode;
    }

    /**
     * Membalikkan status tema (Gelap -> Terang atau sebaliknya)
     */
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        prefs.putBoolean(PREF_DARK_MODE, isDarkMode);
    }

    /**
     * Menerapkan stylesheet yang sesuai pada Scene yang diberikan.
     * Harus dipanggil setiap kali sebuah jendela (Scene) baru dimuat.
     * 
     * @param scene Scene target
     */
    public static void applyTheme(Scene scene) {
        if (scene == null) return;
        
        // Bersihkan stylesheets yang ada agar tidak menumpuk
        scene.getStylesheets().clear();
        
        // 1. Tambahkan common.css (gaya struktural universal)
        String commonCss = ThemeManager.class.getResource("/com/mankelfas/css/common.css").toExternalForm();
        scene.getStylesheets().add(commonCss);
        
        // 2. Tambahkan tema spesifik (warna)
        if (isDarkMode) {
            String darkCss = ThemeManager.class.getResource("/com/mankelfas/css/dark-theme.css").toExternalForm();
            scene.getStylesheets().add(darkCss);
        } else {
            String lightCss = ThemeManager.class.getResource("/com/mankelfas/css/light-theme.css").toExternalForm();
            scene.getStylesheets().add(lightCss);
        }
    }

    /**
     * Menerapkan stylesheet pada sebuah kotak pesan dialog (Alert).
     */
    public static void applyTheme(javafx.scene.control.DialogPane dialogPane) {
        if (dialogPane == null) return;
        
        dialogPane.getStylesheets().clear();
        String commonCss = ThemeManager.class.getResource("/com/mankelfas/css/common.css").toExternalForm();
        dialogPane.getStylesheets().add(commonCss);
        
        if (isDarkMode) {
            String darkCss = ThemeManager.class.getResource("/com/mankelfas/css/dark-theme.css").toExternalForm();
            dialogPane.getStylesheets().add(darkCss);
        } else {
            String lightCss = ThemeManager.class.getResource("/com/mankelfas/css/light-theme.css").toExternalForm();
            dialogPane.getStylesheets().add(lightCss);
        }
        
        // Tambahkan class CSS khusus untuk memicu custom styling di dialog
        dialogPane.getStyleClass().add("custom-dialog");
    }
}
