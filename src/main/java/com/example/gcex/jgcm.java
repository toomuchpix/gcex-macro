package com.example.gcex;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class jgcm extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(jgcm.class.getResource("gceview.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 250, 430);
        stage.setTitle("JGCM: Java G-Code Manager V1.1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}