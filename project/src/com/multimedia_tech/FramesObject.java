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

    public void applyNegativeFilter() {
        for (BufferedImage i : frames) {
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    int rgb = i.getRGB(x, y);
                    Color col = new Color(rgb);
                    col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                    i.setRGB(x, y, col.getRGB());
                }
            }
        }
    }

    public void applyMeanFilter(int val) {
        int alpha,red, green, blue;
        int vals[];
        Color col;
        for (BufferedImage i : frames) {
            vals = new int[i.getWidth() * i.getHeight()];
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    alpha = 0;
                    red = 0;
                    green = 0;
                    blue = 0;
                    int count = 0;
                    for (int r = y - (val / 2); r <= y + (val / 2); r++) {
                        for (int c = x - (val / 2); c <= x + (val / 2); c++) {
                            if (r < 0 || r >= i.getHeight() || c < 0 || c >= i.getWidth()) {
                                /** Some portion of the mask is outside the image. */
                                continue;
                            } else {
                                col = new Color(i.getRGB(c, r));
                                alpha += col.getAlpha();
                                red += col.getRed();
                                green += col.getGreen();
                                blue += col.getBlue();
                                count++;
                            }
                        }
                    }
                    int p = ((alpha/count)<<24) | ((red/count)<<16) | ((green/count)<<8) | (blue/count);
                    vals[x + y * i.getWidth()] = p;
                }
            }
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    i.setRGB(x, y,vals[x + (y * i.getWidth())]);
                }
            }
        }
    }

    public void applyGrayScaleFilter() {
        int red, green, blue;
        for (BufferedImage i : frames) {
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    Color c = new Color(i.getRGB(x, y));
                    red = (int) (c.getRed() * 0.299);
                    green = (int) (c.getGreen() * 0.587);
                    blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                    i.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    public void applyBinarizationFilter(int val) {
        int col, red, green, blue;
        for (BufferedImage i : frames) {
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    Color c = new Color(i.getRGB(x, y));
                    red = (int) (c.getRed() * 0.299);
                    green = (int) (c.getGreen() * 0.587);
                    blue = (int) (c.getBlue() * 0.114);
                    if ((red + green + blue) >= val) {
                        col = 255;
                    } else {
                        col = 0;
                    }
                    Color newColor = new Color(col, col, col);
                    i.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }
}
