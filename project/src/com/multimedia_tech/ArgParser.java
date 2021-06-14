package com.multimedia_tech;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgParser {

    @Parameter(names = {"--input", "-i"},
            required = true,
            description = "Input file. Mandatory.",
            validateWith = FileExists.class)
    private String inputZip;

    @Parameter(names = {"--output", "-o"},
            required = false,
            description = "Name of the file that will contain the images and necessary info for the codification")
    private String outputName;

    @Parameter(names = {"--encode", "-e"},
            required = false,
            description = "Apply a codec to the input file")
    private boolean encode;

    @Parameter(names = {"--decode", "-d"},
            required = false,
            description = "Decode and reproduce the input file")
    private boolean decode;

    @Parameter(names = {"--fps"},
            required = false,
            description = "FPS that the video will reproduced")
    private int fps;

    @Parameter(names = {"--binarization"},
            required = false,
            description = "Filter of binarization (used if indicated)")
    private int binFilter;

    @Parameter(names = {"--grayscale"},
            required = false,
            description = "Convert images to grayscale")
    private boolean grayScale;

    @Parameter(names = {"--negative"},
            description = "Apply a negative filter to the images")
    public boolean negative;

    @Parameter(names = {"--averaging"},
            required = false,
            description = "Apply a meaning filter in places of value x value")
    private int avg;

    @Parameter(names = {"--edgeDetection"},
            required = false,
            description = "Apply a edge detection filter to the image")
    private boolean edge;

    @Parameter(names = {"--batch", "-b"},
            required = false,
            description = "Dismiss video popup window")
    private boolean batch;

    @Parameter(names = {"--nTiles"},
            required = false,
            description = "Tesselation, number of divisions of the images for each axis. Format --nTiles xAxis,yAxis")
    private String nTiles;

    @Parameter(names = {"--seekRange"},
            required = false,
            description = "Maximum displacement when applying tesselation")
    private int seekRange;

    @Parameter(names = {"--GOP"},
            required = false,
            description = "Maximum number of images between two reference frames")
    private int gop;

    @Parameter(names = {"--quality"},
            required = false,
            description = "Quality factor for tesselation")
    private int quality;

    @Parameter(names = {"--help", "-h"},
            description = "Lists all options available", help = true)
    public boolean help;

    public String getInputZip() {
        return inputZip;
    }

    public String getOutputPath() {
        return outputName;
    }

    public boolean getEncodeOption() {
        return encode;
    }

    public boolean getDecodeOption() {
        return decode;
    }

    public int getFPS() {
        return fps;
    }

    public int getBinarizationFilter() {
        return binFilter;
    }

    public boolean getBatchOption() {
        return batch;
    }

    public boolean getNegativeOption() {
        return negative;
    }

    public int getAVGFilterValue() {
        return avg;
    }

    public boolean getGrayScaleOption() {
        return grayScale;
    }

    public boolean getEdgeDetectionOption() {
        return edge;
    }

    public List<Integer> getTesselationValues() {
        if(nTiles == null){
            return new ArrayList(Arrays.asList(10, 10));
        }
        String [] vals = nTiles.split(",");
        List<Integer> list = new ArrayList<>();
        for(String i: vals){
            list.add(Integer.parseInt(i));
        }
        if(list.size() != 2){
            System.out.println("Error> Missing values, check help and try again");
            System.exit(-1);
        }
        return list;
    }

    public int getSeekRange() {
        if(gop == 0)
            return 4;
        return seekRange;
    }

    public int getGOP() {
        if(gop == 0)
            return 5;
        return gop;
    }

    public int getQuality() {
        if(gop == 0)
            return 5;
        return quality;
    }

    public static enum numParamLIMITS {
        MAX(1000), MIN(0);

        private int value;

        private numParamLIMITS(int v) {
            value = v;
        }

        public int getValue() {
            return value;
        }
    }

}
