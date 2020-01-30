package APO.projekt;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;


/**
 * Created by Aleksandra on 12.01.2020.
 */
public class PreviewWindow {
    /**
     * Elementy okna.
     */
    Stage previewStage;
    VBox vBox;
    HBox hBox;

    @Getter
    Image result;

    /**
     * Konstruktor okna podglądu.
     */
    public PreviewWindow(Image secondImage) {
        ImageView imageView = createImageView(secondImage);
        hBox = new HBox(imageView);

        Button cancel = createButton(secondImage, "Cofnij");
        Button compare = createButton(secondImage, "Porównaj obrazy");
        HBox buttons = createButtonsHBox(cancel, compare);
        vBox = new VBox(hBox, buttons);

        createPreviewScene(secondImage, imageView);
        previewStage.showAndWait();
    }

    private Button createButton(Image resultImage, String text) {
        Button button = new Button(text);
        button.setOnAction(event -> setResultImage(resultImage));

        return button;
    }

    private void setResultImage(Image image) {
        result = image;
        previewStage.close();
    }

    /**
     * Tworzy układ okna podglądu - oblicza wielkość okna na podstawie
     * obrazu, dla którego wyświetlany jest podgląd.
     *
     */
    private void createPreviewScene(Image image, ImageView imageView) {
        double windowWidth = imageView.getBoundsInLocal().getWidth() * 2;
        double windowHeight = imageView.getBoundsInLocal().getHeight() + 55;

        Scene previewScene = new Scene(vBox, windowWidth, windowHeight);

        previewStage = new Stage();
        previewStage.initModality(Modality.APPLICATION_MODAL);
        previewStage.setOnCloseRequest(event -> result = image);

        previewStage.setScene(previewScene);
        previewStage.setTitle("Drugi obraz");
    }

    /**
     * Tworzy obszar z przyciskami.
     */
    private HBox createButtonsHBox(Button cancel, Button save) {
        HBox buttons = new HBox(cancel, save);
        buttons.setPadding(new Insets(13, 0, 10, 0));
        buttons.setSpacing(15);
        buttons.setMaxHeight(55);
        buttons.setAlignment(Pos.CENTER);
        return buttons;
    }

    /**
     * Tworzy podgląd dla podanego obrazu.
     */
    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);
        return imageView;
    }
}
