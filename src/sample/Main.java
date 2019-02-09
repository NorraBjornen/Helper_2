package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.TextFields;
import sample.Model.DataBase;
import sample.Model.NetWork;
import sample.View.Browser;

import java.util.ArrayList;
import java.util.List;

import static sample.Model.Constants.TOKEN_DELIMITER;

public class Main extends Application {
    public static Scene scene1;
    public static Scene scene2;
    public static Scene scene4;
    public static Stage stage, stageMap;
    public static WebEngine WebEngine;
    public static List<String> Streets = new ArrayList<>();

    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/sample/View/sample.fxml"));
        Parent root1 = loader1.load();

        scene1 = new Scene(root1, 640, 480);
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/sample/View/monitor.fxml"));
        Parent root2 = loader2.load();
        scene2 = new Scene(root2, 640, 480);

        FXMLLoader loader5 = new FXMLLoader(getClass().getResource("/sample/View/auth.fxml"));
        Parent root5 = loader5.load();
        Scene scene5 = new Scene(root5, 620, 400);

        Browser browser = new Browser();
        WebEngine = browser.getWebEngine();
        scene4 = new Scene(browser,640,480, Color.web("#666970"));
        stage.setTitle("Dispatcher");
        stage.setScene(scene5);
        stage.show();

        stageMap = new Stage();
        stageMap.setScene(scene4);
        stageMap.show();

        scene4.setOnKeyPressed(e ->{
            if(e.getCode().equals(KeyCode.F1)){
                stageMap.setFullScreenExitHint("");
                stageMap.setFullScreen(true);
            } else if(e.getCode().equals(KeyCode.F2)){
                stageMap.setFullScreen(false);
            }
        });

        TextField City = (TextField) Main.scene1.lookup("#City");
        TextField Phone = (TextField) Main.scene1.lookup("#Phone");
        TextField Street = (TextField) Main.scene1.lookup("#Street");
        TextField House = (TextField) Main.scene1.lookup("#House");
        TextField Apartment = (TextField) Main.scene1.lookup("#Apartment");
        TextField Entrance = (TextField) Main.scene1.lookup("#Entrance");
        TextField Description = (TextField) Main.scene1.lookup("#Description");
        TextField To = (TextField) Main.scene1.lookup("#To");
        TextField Price = (TextField) Main.scene1.lookup("#Price");
        TextField Ok = (TextField) Main.scene1.lookup("#Ok");

        scene1.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.CONTROL){
                City.setText("КОСТАНАЙ");
                Phone.clear();
                Street.clear();
                House.clear();
                Apartment.clear();
                Entrance.clear();
                Description.clear();
                To.clear();
                Price.clear();
                Ok.setText("OK");
                stage.setScene(scene2);
                //stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
            }
        });

        scene2.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.CONTROL){
                City.setText("КОСТАНАЙ");
                Phone.clear();
                Street.clear();
                House.clear();
                Apartment.clear();
                Entrance.clear();
                Description.clear();
                To.clear();
                Price.clear();
                Ok.setText("OK");
                Phone.requestFocus();
                stage.setScene(scene1);
                //stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        new Thread(()->{
            List<String> streets = new ArrayList<>();
            String command_text = new DataBase().getStreets();
            if (!command_text.equals("NULL")) {
                String[] elements = command_text.split(TOKEN_DELIMITER);
                for (String str : elements) {

                    boolean add = true;
                    for(String string : streets){
                        if(string.equals(str)){
                            add = false;
                        }
                    }
                    if(add) {
                        streets.add(str);
                    }
                }
                Streets.addAll(streets);
                TextFields.bindAutoCompletion((TextField) Main.scene1.lookup("#Street"), streets).setVisibleRowCount(12);
                TextFields.bindAutoCompletion((TextField) Main.scene1.lookup("#To"), streets).setVisibleRowCount(3);
            }
        }).start();

        new Thread(()->{
            NetWork.get().connect();
        }).start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}