package APO.projekt;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * Created by Aleksandra on 01.01.2020.
 */
public class Picture {
    int width;
    int height;
    int[][] pixels;
    private File file;
    private ImageView imageView;

    /**
     * Konstruktor obiektu tworzony podczas otwarcia pliku.
     */
    public Picture(File file, ImageView imageView) {
        this.file = file;
        this.imageView = imageView;
    }

    public Picture(ImageView imageView) {
        this.imageView = imageView;
    }

    public File getFile() {
        return file;
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
