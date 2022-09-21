package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Enigma Machine");
        Parent load = FXMLLoader.load(getClass().getClassLoader().getResource("tryJavaFX/app/app.fxml"));
        Scene scene = new Scene(load, 888, 720);
        //scene.getStylesheets().add("tryJavaFX/header/headerStyleSheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void Main(String[] args) {
        launch(args);
    }
}