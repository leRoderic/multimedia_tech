package com.multimedia_tech;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;


/**
 * Calculates the Peak Signal-to-Noise Ration (PSNR) from
 * two images.
 * @author Adhonay, Daniel.
 * @since 2017-16-11
 */

public class PSNR {

    public PSNR() {
       
    }
    
    /**
     * Quick alias to get log in base 10.
     * @param x Input number.
     * @return Returns the log10(x).
     */
    public static double logbase10(double x) {
        return Math.log(x) / Math.log(10);
    }

    
    /**
     * Calculates the PSNR from two images.
     * @param im1 Image 1.
     * @param im2 Image 2.
     * @return Returns the PSNR.
     */
    public static double calculate_PSNR(BufferedImage im1, BufferedImage im2) {
        assert(
                im1.getType() == im2.getType()
                        && im1.getHeight() == im2.getHeight()
                        && im1.getWidth() == im2.getWidth());

        double mse = 0;
        int width = im1.getWidth();
        int height = im1.getHeight();
        Raster r1 = im1.getRaster();
        Raster r2 = im2.getRaster();
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++)
                mse += Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2);

        mse /= (double) (width * height);
        double psnr = 10.0 * logbase10(Math.pow(255, 2) / mse);
        //System.err.println("PSNR = " + psnr);
        return psnr;
    }
}