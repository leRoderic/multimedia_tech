package com.multimedia_tech;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
     *
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
    *
    * */
    public void applyMeanFilter(int val) {
        int alpha,red, green, blue;
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
                    int p = ((alpha/count)<<24) | ((red/count)<<16) | ((green/count)<<8) | (blue/count);
                    vals[x + y * i.getWidth()] = p;
                }
            }
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    // Asignamos dicho valor al píxel de la imagen/frame
                    i.setRGB(x, y,vals[x + (y * i.getWidth())]);
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro grayscale al vídeo
     *
     * */
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
     *
     * */
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
     *
     * */
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
                    int gx =  ((-1 * val00) + (0 * val01) + (1 * val02))
                            + ((-2 * val10) + (0 * val11) + (2 * val12))
                            + ((-1 * val20) + (0 * val21) + (1 * val22));

                    int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                            + ((0 * val10) + (0 * val11) + (0 * val12))
                            + ((1 * val20) + (2 * val21) + (1 * val22));

                    double gval = Math.sqrt((gx * gx) + (gy * gy));
                    int g = (int) gval;

                    // Guardamos el máximo gradiente encontrado hasta el momento
                    if(maxGradient < g) {
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
                    edgeColor = (int)(edgeColor * scale);
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
     *
     * */
    public static int  getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);

        return gray;
    }
}
