package com.multimedia_tech;

import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FramesObject {

    private ArrayList<BufferedImage> frames;
    private String name;

    public FramesObject(ArrayList<BufferedImage> a, String n) {
        frames = a;
        name = n;
    }

    public String getName() {
        return name;
    }

    public ArrayList<BufferedImage> getFrames() {
        return frames;
    }

    /**
     * Función para aplicar el filtro negativo al vídeo
     */
    public void applyNegativeFilter() {
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    // Se obtiene el color del píxel
                    int rgb = i.getRGB(x, y);
                    Color col = new Color(rgb);
                    // Se crea un nuevo color aplicando el filto negativo: 255 - color original píxel
                    col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                    // Se asigna el color creado anteriormente al píxel de la imagen/frame
                    i.setRGB(x, y, col.getRGB());
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro medio al vídeo
     *
     * @param val -> rango de vecindad
     */
    public void applyMeanFilter(int val) {
        int alpha, red, green, blue;
        int vals[];
        Color col;
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            vals = new int[i.getWidth() * i.getHeight()];
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    alpha = 0;
                    red = 0;
                    green = 0;
                    blue = 0;
                    int count = 0;
                    // Se recorre la vecindad de dicho píxel para poder calcular la media
                    for (int r = y - (val / 2); r <= y + (val / 2); r++) {
                        for (int c = x - (val / 2); c <= x + (val / 2); c++) {
                            // Condición para evitar coger píxeles vecinos que se encuentren
                            // fuera del rango de la imagen original
                            if (r < 0 || r >= i.getHeight() || c < 0 || c >= i.getWidth()) {
                                continue;
                            }
                            // Acumulamos los valores de cada componente del color en esa vecindad
                            else {
                                col = new Color(i.getRGB(c, r));
                                alpha += col.getAlpha();
                                red += col.getRed();
                                green += col.getGreen();
                                blue += col.getBlue();
                                count++;
                            }
                        }
                    }
                    // Calculamos la media a partir de los valores acumulados
                    int p = ((alpha / count) << 24) | ((red / count) << 16) | ((green / count) << 8) | (blue / count);
                    vals[x + y * i.getWidth()] = p;
                }
            }
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    // Asignamos dicho valor al píxel de la imagen/frame
                    i.setRGB(x, y, vals[x + (y * i.getWidth())]);
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro grayscale al vídeo
     */
    public void applyGrayScaleFilter() {
        int red, green, blue;
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    // Cogemos el color del píxel y le asignamos un peso a cada una de sus componentes RGB
                    // en nuestro caso: R-> 0.299 G-> 0.587 B-> 0.114
                    Color c = new Color(i.getRGB(x, y));
                    red = (int) (c.getRed() * 0.299);
                    green = (int) (c.getGreen() * 0.587);
                    blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                    // Asignamos dicho color al píxel de la imagen/frame
                    i.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro binario al vídeo
     *
     * @param val -> valor que establece cuando un píxel es negro o blanco
     */
    public void applyBinarizationFilter(int val) {
        int col, red, green, blue;
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    // Modificamos el color del píxel al igual que grayscale
                    Color c = new Color(i.getRGB(x, y));
                    red = (int) (c.getRed() * 0.299);
                    green = (int) (c.getGreen() * 0.587);
                    blue = (int) (c.getBlue() * 0.114);
                    // Si el color obtenido con la suma de cada uno de los componentes del píxel
                    // es mayor al valor que introduce el usuario, este píxel pasara a ser blanco
                    if ((red + green + blue) >= val) {
                        col = 255;
                    }
                    // Sino, pasará a ser negro
                    else {
                        col = 0;
                    }
                    Color newColor = new Color(col, col, col);
                    // Asignamos dicho color al píxel de la imagen/frame
                    i.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    /**
     * Función para plicar detecion de contornos al vídeo
     */
    public void applyEdgeDetectionFilter() {
        int maxGradient = -1;

        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            int[][] edgeColors = new int[i.getWidth()][i.getHeight()];
            for (int x = 1; x < i.getWidth() - 1; x++) {
                for (int y = 1; y < i.getHeight() - 1; y++) {

                    //Recorre la vecindad el píxel pasando cada un de estos a grayscale
                    int val00 = getGrayScale(i.getRGB(x - 1, y - 1));
                    int val01 = getGrayScale(i.getRGB(x - 1, y));
                    int val02 = getGrayScale(i.getRGB(x - 1, y + 1));

                    int val10 = getGrayScale(i.getRGB(x, y - 1));
                    int val11 = getGrayScale(i.getRGB(x, y));
                    int val12 = getGrayScale(i.getRGB(x, y + 1));

                    int val20 = getGrayScale(i.getRGB(x + 1, y - 1));
                    int val21 = getGrayScale(i.getRGB(x + 1, y));
                    int val22 = getGrayScale(i.getRGB(x + 1, y + 1));

                    // Aplicamos el filtro de gradiente Sovel tanto en horizontal como en vertical
                    int gx = ((-1 * val00) + (0 * val01) + (1 * val02))
                            + ((-2 * val10) + (0 * val11) + (2 * val12))
                            + ((-1 * val20) + (0 * val21) + (1 * val22));

                    int gy = ((-1 * val00) + (-2 * val01) + (-1 * val02))
                            + ((0 * val10) + (0 * val11) + (0 * val12))
                            + ((1 * val20) + (2 * val21) + (1 * val22));

                    double gval = Math.sqrt((gx * gx) + (gy * gy));
                    int g = (int) gval;

                    // Guardamos el máximo gradiente encontrado hasta el momento
                    if (maxGradient < g) {
                        maxGradient = g;
                    }

                    edgeColors[x][y] = g;
                }
            }
            // Normalizamos los gradientes a partir del máximo encontrado
            double scale = 255.0 / maxGradient;

            for (int x = 1; x < i.getWidth() - 1; x++) {
                for (int y = 1; y < i.getHeight() - 1; y++) {
                    int edgeColor = edgeColors[x][y];
                    edgeColor = (int) (edgeColor * scale);
                    edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;
                    // Modificamos el color al píxel de la imagen/frame a partir del gradiente calculado anteriormente
                    i.setRGB(x, y, edgeColor);
                }
            }
        }
    }

    /**
     * Función soporte para aplicar grayscale a un píxel
     *
     * @return int grayscale
     */
    public static int getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);

        return gray;
    }

    public ArrayList<Byte> encode(int gop, int seekRange, int quality, int xTiles, int yTiles) {
        ArrayList<int[]> matchCoordinates;
        ArrayList<Byte> data = new ArrayList();
        //ArrayList<int[]> matchesCoords = new ArrayList();
        // ESTRUCTURA      GOP  xTiles  yTiles  :: #frame
        data.add((byte) (gop & 0xff));
        data.add((byte) (xTiles & 0xff));
        data.add((byte) (yTiles & 0xff));
        int coincidence = 0;

        BufferedImage[] reference = null;

        for (int i = 0; i < frames.size(); i++) {
            int rows = frames.get(i).getWidth() / xTiles;
            int cols = frames.get(i).getHeight() / yTiles;
            if (i % gop != 0) {
                // Código imagénes intercuadro
                matchCoordinates = new ArrayList<>();
                data.add((byte) (i & 0xff));
                for (int j = 0; j < reference.length; j++) {
                    if (findCoincidence(i, frames.get(i), reference[j], xTiles, yTiles, quality, rows, cols, seekRange, data, matchCoordinates)) {

                        coincidence -= -1;
                    }
                }
                if (coincidence <= 0) {
                    data.add((byte) (0));
                    data.add((byte) (0));
                } else {
                    data.add((byte) (coincidence & 0xff));
                    data.add((byte) ((coincidence >> 8) & 0xff));
                    int x, y, r = 0, g = 0, b = 0;
                    for (int m = 0; m < matchCoordinates.size(); m++) {
                        x = matchCoordinates.get(i)[0];
                        y = matchCoordinates.get(i)[1];
                        int[] colors = frames.get(i).getRGB(x, y, xTiles, yTiles, null, 0, xTiles*yTiles);
                        for (int c : colors) {
                            r += ((c >> 16) & 0xFF);
                            g += ((c >> 8) & 0xFF);
                            b += (c & 0xFF);
                        }
                        int R = r / colors.length;
                        int G = g / colors.length;
                        int B = b / colors.length;

                        int[] rgbArray = new int[(xTiles - 2) * (yTiles - 2)];
                        Color c = new Color(R, G, B);

                        Arrays.fill(rgbArray, c.getRGB());
                        frames.get(i).setRGB(++x, ++y, xTiles - 2, yTiles - 2, rgbArray, 0, 0);
                    }
                }

            } else {
                // Código imagénes intracuadro - referencia
                reference = subdivideFrames(frames.get(i), xTiles, yTiles, rows, cols);
            }
        }
        return data;
    }

    public BufferedImage[] subdivideFrames(BufferedImage frame, int xTiles, int yTiles, int rows, int cols) {
        //int rows = frame.getWidth() / xTiles;
        //int cols = frame.getHeight() / yTiles;
        int count = 0;

        BufferedImage subimages[] = new BufferedImage[rows * cols]; //Image array to hold image chunks
        for (int y = 0; y < frame.getHeight(); y += yTiles) {
            for (int x = 0; x < frame.getWidth(); x += xTiles) {
                subimages[count] = frame.getSubimage(x, y, xTiles, yTiles);
                count++;

            }
        }
        return subimages;
    }

    private boolean findCoincidence(int nFrame, BufferedImage frame, BufferedImage reference, int xTiles,
                                    int yTiles, int quality, int rows, int cols, int seekRange, ArrayList<Byte> d,
                                    ArrayList<int[]> c) {


        int xMin, xMax, yMin, yMax;
        yMin = (nFrame % rows) * yTiles - seekRange;
        if (yMin < 0) {
            yMin = 0;
        }

        yMax = yTiles * ((nFrame % rows) + 1) + seekRange;
        if (yMax > frames.get(0).getWidth()) {
            yMax = frames.get(0).getWidth();
        }

        xMin = (nFrame / rows) * xTiles - seekRange;
        if (xMin < 0) {
            xMin = 0;
        }

        xMax = xTiles * ((nFrame / rows) + 1) + seekRange;
        if (xMax > frames.get(0).getHeight()) {
            xMax = frames.get(0).getHeight();
        }

        for (int y = yMin; y <= yMax - yTiles; y++) {
            for (int x = xMin; x <= xMax - xTiles; x++) {

                double correlation = compareImages(frame.getSubimage(y, x, xTiles, yTiles), reference);
                if (correlation < quality) {
                    c.add(new int[]{y, x});
                    d.add((byte) (y & 0xff));
                    d.add((byte) ((y >> 8) & 0xff));
                    d.add((byte) (x & 0xff));
                    d.add((byte) ((x >> 8) & 0xff));
                    d.add((byte) (nFrame & 0xff));
                    d.add((byte) ((nFrame >> 8) & 0xff));
                    return true;
                }
            }
        }

        return false;
    }

    private double compareImages(BufferedImage image1, BufferedImage image2) {
        assert (image1.getHeight() == image2.getHeight() && image1.getWidth() == image2.getWidth());
        double variation = 0.0;
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                variation += compareARGB(image1.getRGB(x, y), image2.getRGB(x, y)) / Math.sqrt(3);
            }
        }
        return variation / (image1.getWidth() * image2.getHeight());
    }

    private double compareARGB(int rgb1, int rgb2) {
        double r1 = ((rgb1 >> 16) & 0xFF) / 255.0;
        double r2 = ((rgb2 >> 16) & 0xFF) / 255.0;
        double g1 = ((rgb1 >> 8) & 0xFF) / 255.0;
        double g2 = ((rgb2 >> 8) & 0xFF) / 255.0;
        double b1 = (rgb1 & 0xFF) / 255.0;
        double b2 = (rgb2 & 0xFF) / 255.0;
        double a1 = ((rgb1 >> 24) & 0xFF) / 255.0;
        double a2 = ((rgb2 >> 24) & 0xFF) / 255.0;

        return a1 * a2 * Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
    }

    public static ArrayList<BufferedImage> decode(File packed) {
        System.out.println("@decode receives " + packed.getName());
        try {
            decompress(packed);
        } catch (Exception ex) {
            Logger.getLogger(Base64.Decoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        //printBuilder();
        build();
        return compressed;
    }

    private static File[] decompress(File encoded) throws Exception {
        String tarFile = encoded.getName();
        File[] output = new File[2];

        // Create a TarInputStream
        TarInputStream tis;
        try {
            tis = new TarInputStream(new BufferedInputStream(new FileInputStream(tarFile)));

            TarEntry entry;
            int id = 0;
            while ((entry = tis.getNextEntry()) != null) {

                byte data[] = new byte[BUFFER_SIZE];
                FileOutputStream fos;
                fos = new FileOutputStream(entry.getName());
                BufferedOutputStream dest = new BufferedOutputStream(fos);

                int count;
                while ((count = tis.read(data)) != -1) {
                    dest.write(data, 0, count);
                }

                dest.flush();
                dest.close();

                //Once extracted read as tmp files. We should delete them after processing.
                System.out.print("\t File " + entry.getName() + " extracted");
                File tmp = new File(entry.getName());
                if (id == 0) {
                    System.out.println("\t\t-> @readZip");
                    compressed = ZipSaveWorker.readZip(tmp);
                } else if (id == 1 && entry.getName().equals(Encoder.BUILDER_FNAME)) {
                    System.out.println("\t-> @decompressGzipFile");
                    builder = decompressGzipFile(entry.getName(), BUFFER_SIZE);
                }
                tmp.delete();
                id++;
            }
            tis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return output;
    }


}
