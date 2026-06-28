package com.mankelfas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    /**
     * Titik awal eksekusi UI pengguna berbasis JavaFX.
     * Mengatur jendela utama dan menampilkan halaman masuk (login) sebagai layar pembuka.
     * 
     * @param stage Panggung utama atau jendela dasar aplikasi
     * @throws IOException Jika file layout (FXML) gagal ditemukan
     */
    @Override
    public void start(Stage stage) throws IOException {
        try {
            scene = new Scene(loadFXML("login"), 640, 480);
            stage.setTitle("MankelFas - Manajemen Keluhan Fasilitas");
            stage.getIcons().add(new Image(App.class.getResourceAsStream("/com/mankelfas/assets/logo.png")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat UI pengguna utama: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Menukar tampilan halaman yang sedang aktif di dalam jendela utama.
     * 
     * @param fxml Nama file layout tanpa ekstensi (contoh: "dashboard")
     * @throws IOException Jika file target tidak dapat dibaca
     */
    public static void setRoot(String fxml) throws IOException {
        try {
            scene.setRoot(loadFXML(fxml));
            if ("login".equals(fxml)) {
                scene.getStylesheets().clear();
            } else {
                com.mankelfas.util.ThemeManager.applyTheme(scene);
            }
        } catch (IOException e) {
            System.err.println("Gagal mengganti root ke " + fxml + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Mencari dan menerjemahkan file layout menjadi komponen visual nyata.
     * 
     * @param fxml Nama file layout
     * @return Komponen induk (Parent) yang siap ditampilkan
     * @throws IOException Jika file layout rusak atau hilang
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mankelfas/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
