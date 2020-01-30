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

    static int rownum = 0;

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

    public static boolean isGrayscale(BufferedImage image) {

        int pixel,red, green, blue;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                pixel = image.getRGB(x, y);
                red = (pixel >> 16) & 0xff;
                green = (pixel >> 8) & 0xff;
                blue = (pixel) & 0xff;
                if (red != green || green != blue ) return false;
            }
        }
        return true;
    }

    public static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);

        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public static void writeChannel (String channel, BufferedImage image, XSSFSheet sheet, XSSFWorkbook workbook) {

        XSSFRow labelRow = sheet.createRow(rownum++);
        XSSFCell labelCell = labelRow.createCell(0);
        labelCell.setCellValue(channel);

        for (int i = 0; i < image.getHeight(); i++) {
            XSSFRow row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (int j = 0; j < image.getWidth(); j++) {
                int color = image.getRGB(j, i);
                Color col = new Color(color, true);
                XSSFCell cell = row.createCell(cellnum++);
                if (channel.equals("RED")) {
                    cell.setCellValue(col.getRed());
                } else if (channel.equals("GREEN")) {
                    cell.setCellValue(col.getGreen());
                } else if (channel.equals("BLUE")) {
                    cell.setCellValue(col.getBlue());
                }
            }
        }
    }

    public static void export (File file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Obraz");
        BufferedImage image = ImageIO.read(file);
        Boolean isBW = isGrayscale(image);

        XSSFSheet sheetDane = workbook.createSheet("Dane");
        sheetDane.createRow(0).createCell(0).setCellValue(image.getHeight());
        sheetDane.createRow(1).createCell(0).setCellValue(image.getWidth());
        sheetDane.createRow(2).createCell(0).setCellValue(isBW ? "B&W" : "KOLOR");

        if (isBW) {
            writeChannel("RED", image, sheet, workbook);
        } else {
            writeChannel("RED", image, sheet, workbook);
            writeChannel("GREEN", image, sheet, workbook);
            writeChannel("BLUE", image, sheet, workbook);
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

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static File importFile (File openedFile) {

        File outputfile = new File("resultImage.jpg");

        try {
            FileInputStream file = new FileInputStream(openedFile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFSheet sheetDane = workbook.getSheetAt(1);

            int height = (int)sheetDane.getRow(0).getCell(0).getNumericCellValue();
            int width = (int)sheetDane.getRow(1).getCell(0).getNumericCellValue();
            Boolean isGrayscale = sheetDane.getRow(2).getCell(0).getStringCellValue().equals("B&W");

            System.out.println("height " + height + ", width: " + width);
            BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int r = 0;
            int g = 0;
            int b = 0;

            for (int i = 1; i <= height; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < width; j++) {
                    r = (int)row.getCell(j).getNumericCellValue();
                    if (isGrayscale) {
                        g = r;
                        b = r;
                    } else {
                        g = (int) sheet.getRow(i + height + 1).getCell(j).getNumericCellValue();
                        b = (int) sheet.getRow(i + 2 * height + 2).getCell(j).getNumericCellValue();
                    }
                    Color col = new Color(r, g, b);
                    resultImage.setRGB(j, i - 1, col.getRGB());
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

    public static void comparePictures (File file) throws IOException {

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

}
