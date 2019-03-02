package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;


public class VerifyController {

    @FXML
    ImageView imageViewer;


    public void initialize(){
        imageViewer.setImage(new Image("sample/gifs/giphy.gif"));
        imageViewer.setCursor(Cursor.HAND);
    }
    @FXML
    public void onMouseClickedEvent(){
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("sample.fxml"));
            try {
                Parent root = loader.load();
                Main.SwitchScenes(root);
            } catch (IOException ex) {

            }
        });
    }

}
