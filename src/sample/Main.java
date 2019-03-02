package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {

    static Stage curStage;
    static Dimension dimension;

    @Override
    public void start(Stage primaryStage) throws Exception{
        curStage = primaryStage;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        dimension = toolkit.getScreenSize();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("HSE Parser Application");
        primaryStage.setResizable(false);
        double width = dimension.getWidth() / 2;
        double height = dimension.getHeight() / 2;
        primaryStage.setScene(new Scene(root, (int)width, (int)height));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void stop(){
        System.exit(0);
    }

    public static void SwitchScenes(Parent root){
        curStage.setScene(new Scene(root, Main.dimension.getWidth() / 2, Main.dimension.getHeight() / 2));
        curStage.show();
    }
}
