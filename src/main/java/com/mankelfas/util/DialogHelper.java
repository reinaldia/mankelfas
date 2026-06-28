package com.mankelfas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DialogHelper {

    /**
     * Menampilkan kotak pesan mengambang untuk memberikan informasi kepada pengguna.
     * 
     * @param title Judul utama pada jendela pesan
     * @param header Teks sorotan ringkas di bagian atas
     * @param content Penjelasan lengkap atau detail dari pesan tersebut
     */
    public static void showInfoDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setGraphic(null);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setContent(expContent);
        ThemeManager.applyTheme(alert.getDialogPane());
        alert.showAndWait();
    }

    /**
     * Menampilkan kotak pesan kesalahan.
     * 
     * @param title Judul utama pada jendela pesan
     * @param content Penjelasan pesan kesalahan
     */
    public static void showInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(content);
        ThemeManager.applyTheme(alert.getDialogPane());
        alert.showAndWait();
    }

    /**
     * Menampilkan kotak pesan kesalahan (Error).
     * 
     * @param title Judul utama pada jendela pesan
     * @param content Penjelasan rinci mengenai kesalahan yang terjadi
     */
    public static void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(content);
        ThemeManager.applyTheme(alert.getDialogPane());
        alert.showAndWait();
    }

    /**
     * Menampilkan kotak pesan peringatan (Warning).
     * 
     * @param title Judul utama pada jendela pesan
     * @param content Penjelasan rinci mengenai peringatan tersebut
     */
    public static void showWarningDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(content);
        ThemeManager.applyTheme(alert.getDialogPane());
        alert.showAndWait();
    }

    /**
     * Menerapkan gaya tema visual kustom (ThemeManager) pada sebuah instans Alert bawaan.
     * Fungsi ini akan menghapus ikon dan header bawaan agar konsisten dengan desain aplikasi.
     * 
     * @param alert Objek Alert yang ingin diformat gayanya
     */
    public static void styleAlert(Alert alert) {
        alert.setHeaderText(null);
        alert.setGraphic(null);
        ThemeManager.applyTheme(alert.getDialogPane());
    }
}
