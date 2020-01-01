package APO.projekt;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aleksandra on 01.01.2020.
 */
public class MainWindow {

    MenuBar menuBar;

    /**
     * Lista akceptowanych rozszerzeń.
     */
    private static final List<String> ACCEPTED_EXTENSIONS = Arrays.asList("*.jpg", "*.jpeg", "*.bmp", "*.png", "*.tif");

    /**
     * Przechowuje ścieżkę do katalogu, z którego pochodzi ostatnio otwarty obraz.
     */
    private File lastDirectory;


    Stage mainStage;
    private List<MenuItem> menuOptions;
    ImageView imageView;

    /**
     * Obiekt zawierający informacje o otwartym pliku.
     */
    private Picture openedFileData;


    /**
     * Konstruktor głównego okna programu.
     *
     */
    public MainWindow(Stage mainStage) {
        this.mainStage = mainStage;
        menuOptions = new ArrayList<>();
        buildWindow();
    }

    /**
     * Buduje główne okno programu.
     */
    private void buildWindow() {
        createMainStage();
        mainStage.setScene(createScene());
        mainStage.show();
    }

    private void createMainStage() {
        mainStage.setTitle("APO-project");
        mainStage.setOnCloseRequest(event -> Platform.exit());
    }

    private Scene createScene() {
        createMenu();
        VBox mainVBox = new VBox(menuBar);
        return new Scene(mainVBox, 1000, 600);
    }

    /**
     * Tworzy menu główne programu.
     */
    private void createMenu() {
        menuBar = new MenuBar();
        imageView = new ImageView();
        menuBar.getMenus().addAll(createFileMenu());
    }

    /**
     * Tworzy menu plik.
     */
    private Menu createFileMenu() {
        Menu menu = new Menu("Plik");
        MenuItem openFile = createOpenFileItem();
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        MenuItem closeFile = createCloseFileItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        menu.getItems().addAll(openFile, separator1, closeFile, separator2);
        return menu;
    }

    /**
     * Menu otwarcia pliku.
     */
    private MenuItem createOpenFileItem() {
        FileChooser fileChooser = createOpenFileChooser();
        MenuItem openFile = new MenuItem("Otwórz");

        KeyCombination keyCodeCombination = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        openFile.setAccelerator(keyCodeCombination);

        openFile.setOnAction(event -> {
            handleFileOpenAction(fileChooser);
        });
        return openFile;
    }

    private FileChooser createOpenFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz plik");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Obrazy", ACCEPTED_EXTENSIONS);
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser;
    }

    /**
     * Obsługuje akcję otwarcia pliku.
     */
    private void handleFileOpenAction(FileChooser fileChooser)  {
        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(lastDirectory);
        }

        File file;
        try {
            file = fileChooser.showOpenDialog(mainStage);
            System.out.println(file);
        } catch (Exception e) {
            throw(e);
        }

        if (file != null) {
            try {
                openImage(file);
            } catch (java.io.IOException e) {
                System.out.println("Blad otwierania pliku.");
            }

        }
    }


    /**
     * Otwórz obraz
     */
    private void openImage(File file) throws IOException {
        Image image;
        FileInputStream fileInputStream = new FileInputStream(file);
        image = new Image(fileInputStream);
        System.out.println("Otwieram" + image);

        imageView.setEffect(null);
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        imageView.fitHeightProperty();
        openedFileData = new Picture(file, imageView);
        lastDirectory = file.getParentFile();
    }

    /**
     * Zamknij obraz
     */

    private MenuItem createCloseFileItem() {
        MenuItem closeFile = new MenuItem("Zamknij plik");
        menuOptions.add(closeFile);
        closeFile.setOnAction(event -> {
            openedFileData = null;
            imageView.setImage(null);
        });
        return closeFile;
    }



}
