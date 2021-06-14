package com.multimedia_tech;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
            List<Integer> asd = parser.getTesselationValues();
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
            if(parser.getEdgeDetectionOption()){
                fo.applyEdgeDetectionFilter();
            }

            if(!parser.getBatchOption()) {
                FramesViewer fv = new FramesViewer(fo, fps);
                fv.run();
            }

            if(parser.getEncodeOption()){{
                if (parser.getOutputPath() != null)
                    System.err.println("Add output file");
                }
                List<Integer> dTiles = parser.getTesselationValues();
                ArrayList<Byte> ret =  fo.encode(parser.getGOP(), parser.getSeekRange(), parser.getQuality(), dTiles.get(0), dTiles.get(1));
                fm.saveImagesToZip(parser.getOutputPath(), ret);
            }

            /* if(parser.getOutputPath() != null){
                fm.saveImagesToZip(parser.getOutputPath());
            }*/
        }catch (ParameterException | IOException pex){
            System.err.println(pex.getMessage());
            System.err.println("Try --help or -h for help.");
        }

    }


}
