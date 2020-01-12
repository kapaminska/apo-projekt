package APO.projekt;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Aleksandra on 01.01.2020.
 */
public class Main extends Application {

    public MainWindow mainWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = new MainWindow(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }


}
