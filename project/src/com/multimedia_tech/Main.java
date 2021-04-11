package com.multimedia_tech;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.*;

public class Main {

    public static void main(String[] args) {

        ArgParser parser = new ArgParser();
        JCommander jComm = null;
        FramesManager fm = FramesManager.getInstance();
        int fps = 24;
        try{
            jComm = new JCommander(parser, args);
            jComm.setProgramName("AUAUVR.jar");

            if (parser.help) {
                jComm.usage();
            }
            fm.loadZipImages(parser.getInputZip());
            FramesObject fo = fm.getOrderedFrames();
            if(parser.getFPS() != 0){
                fps = parser.getFPS();
            }
            if(parser.getNegativeOption()){
                fo.applyNegativeFilter();
            }
            if(parser.getAVGFilterValue() != 0){
                fo.applyMeanFilter(parser.getAVGFilterValue());
            }
            if(parser.getGrayScaleOption()){
                fo.applyGrayScaleFilter();
            }
            if(parser.getBinarizationFilter() != 0){
                fo.applyBinarizationFilter(parser.getBinarizationFilter());
            }

            FramesViewer fv = new FramesViewer(fo, fps);
            fv.run();

            if(parser.getOutputPath() != null){
                fm.saveImagesToZip(parser.getOutputPath());
            }
        }catch (ParameterException | IOException pex){
            System.err.println(pex.getMessage());
            System.err.println("Try --help or -h for help.");
        }

    }


}
