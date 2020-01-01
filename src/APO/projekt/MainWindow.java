package APO.projekt;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    /**
     * Obiekt zawierający informacje o otwartym pliku.
     */
    private Picture openedFileData;

    /**
     * Obiekty odpowiadające za wyświetlanie obrazu.
     */
    private Pane imagePane;
    private ScrollPane scrollPane;
    private ImageView imageView;
    private Slider zoomSlider;
    private HBox statusBar;
    private Label imageSize;


    /**
     * Konstruktor głównego okna programu.
     *
     */
    public MainWindow(Stage mainStage) {
        this.mainStage = mainStage;
        menuOptions = new ArrayList<>();
        buildWindow();
        refreshWindow();
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
        createScrollPane();
        Separator separator = new Separator();
        createStatusBar();
        VBox mainVBox = new VBox(menuBar, scrollPane, separator, statusBar);
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
        System.out.println("Otwieram" + image + " : " + file);

        imageView.setEffect(null);
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        imageView.fitHeightProperty().bind(zoomSlider.valueProperty().multiply(image.getHeight()));
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

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    /**
     * Tworzy zawartość okna głównego z otwartym obrazem.
     */
    private void createScrollPane() {
        imagePane = new Pane(imageView);
        scrollPane = new ScrollPane(imagePane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefWidthProperty().bind(mainStage.widthProperty());
        scrollPane.prefHeightProperty().bind(mainStage.heightProperty());
        //scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleZoom);
        //scrollPane.setOnDragOver(this::handleFileDrag);
        //scrollPane.setOnDragDropped(this::handleFileDropped);
    }

    /**
     * Odświeża zmiany i wczytuje ponownie, które wymagają ponownego
     * załadowania po otwarciu lub zamknięciu pliku.
     */
    private void refreshWindow() {
        refreshWindowTitle();
        //enabledWhenFileOpended.forEach(menuItem -> menuItem.setDisable(openedFileData == null));
        //refreshStatusBar();
        //resetSelection();

        //closeOpenedWindows();
    }

    private void refreshWindowTitle() {
        if (openedFileData != null) {
            mainStage.setTitle("APO-projekt - " + openedFileData.getFile().getName());
        } else {
            System.out.println("null");
            mainStage.setTitle("APO-projekt");
        }
    }

    /**
     * Tworzy slider obsługujący powiększanie i pomniejszanie obrazu.
     *
     * @return <tt>Label</tt> z wartością zoomu.
     */
    private Label createZoomSlider() {
        zoomSlider = new Slider();
        zoomSlider.setMin(0.1);
        zoomSlider.setValue(1);
        zoomSlider.setMax(4);
        Label sliderValue = new Label((int) (zoomSlider.getValue() * 100) + "%");
        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderValue.setText((int) (zoomSlider.getValue() * 100) + "%");
        });
        return sliderValue;
    }

    /**
     * Tworzy pasek statusu na dole ekranu.
     */
    private void createStatusBar() {
        Label sliderValue = createZoomSlider();
        Separator separator = new Separator(Orientation.VERTICAL);
        imageSize = new Label("");
        statusBar = new HBox(imageSize, separator, sliderValue, zoomSlider);
        statusBar.setMinHeight(25);
        statusBar.setMaxHeight(25);
        statusBar.setAlignment(Pos.CENTER_RIGHT);
    }

    /**
     * Odświeża wartość zoomu na pasku statusu.
     */
    private void refreshImageSize() {
        if (openedFileData != null) {
            Image openedImage = openedFileData.getImageView().getImage();
            imageSize.setText((int) openedImage.getWidth() + "x" + (int) openedImage.getHeight());
        } else {
            imageSize.setText("");
        }
    }

}
