package com.multimedia_tech;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {

        String[] test_args = {"--verbose", "4", "--inputZip", "images/Cubo.zip", "--outputPath" , "images/CuboGuardado.zip" ,"--debug", "-np", "500"};

        ArgParser parser = new ArgParser();
        JCommander jComm = null;
        FileManager fm = FileManager.getInstance();

        try{
            jComm = new JCommander(parser, args);
            jComm.setProgramName("AUAUVR.jar");

            if (parser.help) {
                jComm.usage();
            }
            fm.loadZipImages(parser.getInputZip());
            FramesObject fo = fm.getOrderedFrames();
            if(parser.getOutputPath() != null){
                fm.saveImagesToZip(parser.getOutputPath());
            }
        }catch (ParameterException | IOException pex){
            System.err.println(pex.getMessage());
            System.err.println("Try --help or -h for help.");
        }

    }


}
