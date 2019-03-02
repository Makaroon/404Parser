package sample;

import com.sun.scenario.animation.shared.TimerReceiver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Download_Controller {

    @FXML
    Label label;

    @FXML
    ImageView imageV;

    public void initialize() {
        imageV.setImage(new Image("sample/gifs/Eclipse-1s-200px.gif"));
        label.setFont(new Font("sample/fonts/FuturaLightC.ttf", 18));
        Timer output = new Timer();
        Thread thread = new Thread(new MyThread(output, label));
        thread.setDaemon(true);
        thread.start();
    }
}

class MyThread implements Runnable{
    Timer output;
    Label label;
    MyThread(Timer output, Label label){
        this.output = output;
        this.label = label;
    }
    @Override
    public void run() {
        output.schedule(new MyTimerTask(label), 0, 10000);
        while (true) {
            if (ThreadsCreate.getState()) {
                output.cancel();
                System.out.println("Canceled");
                Platform.runLater(() -> {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("Verify.fxml"));
                        Main.SwitchScenes(root);
                    } catch (IOException ex) {
                        System.out.println("SOMETHING BAD HAS HAPPENED");
                    }
                });
                break;
            }
        }
    }
}
class MyTimerTask extends TimerTask {
    private String[] array = new String[5];

    private Label label;
    public MyTimerTask(Label label){
        this.label = label;

        char symb = '\u00A9';
        array[0] = "Все произойдет, если только человек будет ждать и надеяться. " + symb + " Бенджамин Дизраэли" ;
        array[1] = "Устаешь ждать, но насколько хуже было бы, если бы ждать стало нечего. " + symb + " Бернард Шоу";
        array[2] = "Любовь растет от ожиданья долгого и быстро гаснет, быстро получив свое. " + symb + " Менандр";
        array[3] = "Тот, кто дожидается удачи, никогда не знает, будет ли он сегодня ужинать. " + symb + " Бенджамин Франклин";
        array[4] = "Кто ждет - тот всегда дождется. " + symb + " Денис Кумар";
    }
    int count = 5;
    Random rnd = new Random();

    @Override
    public void run() {
        int k;
        do{
            k = rnd.nextInt(5);
        }while (k == count);
        int i = k;
        Platform.runLater(()-> label.setText(array[i]));
    }
}