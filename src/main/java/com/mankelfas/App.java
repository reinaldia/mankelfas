package com.mankelfas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            scene = new Scene(loadFXML("login"), 640, 480);
            stage.setTitle("Manajemen Keluhan Fasilitas");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat antarmuka pengguna utama: " + e.getMessage());
            throw e;
        }
    }

    public static void setRoot(String fxml) throws IOException {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            System.err.println("Gagal mengganti root ke " + fxml + ": " + e.getMessage());
            throw e;
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mankelfas/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
