package APO.projekt;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by Aleksandra on 01.01.2020.
 */
public class ImageUtils {

    public static Mat imageToMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];

        PixelReader reader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, buffer);

        return mat;
    }

    public static Image toGrayscale(Image image) {
        Mat inImage = ImageUtils.imageToMat(image);
        Mat outImage = new Mat();
        Imgproc.cvtColor(inImage, outImage, Imgproc.COLOR_BGR2GRAY);

        return ImageUtils.mat2Image(outImage);
    }

    public static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);

        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public static void export (File file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Obraz");
        BufferedImage image = ImageIO.read(file);

        int rownum = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            XSSFRow row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (int j = 0; j < image.getWidth(); j++) {
                int color = image.getRGB(j, i);
                Color col = new Color(color, true);
                XSSFCell cell = row.createCell(cellnum++);
                cell.setCellValue(col.getRed());
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(new File("result.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("result.xlsx written successfully on disk.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File importFile (File openedFile) {
        File outputfile = new File("resultImage.jpg");
        try {
            FileInputStream file = new FileInputStream(openedFile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int numOfRows = sheet.getLastRowNum();
            Row firstRow = sheet.getRow(0);
            int numOfCells = firstRow.getLastCellNum();
            System.out.println("numOfRows " + numOfRows + " ,numOfCells: " + numOfCells);
            BufferedImage resultImage = new BufferedImage(numOfCells, numOfRows, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < numOfRows; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < numOfCells; j++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(j);
                    int value =  (int)cell.getNumericCellValue();
                    Color col = new Color(value, value, value);
                    resultImage.setRGB(j, i, col.getRGB());
                }
            }
            ImageIO.write(resultImage, "jpg", outputfile);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return outputfile;
    }

}
