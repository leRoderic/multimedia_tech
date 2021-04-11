package com.multimedia_tech;

import com.beust.jcommander.Parameter;

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
    private String codec;

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
    private String binFilter;

    @Parameter(names = { "--negative"},
            description = "Apply a negative filter to the images")
    public boolean negative;

    @Parameter(names = {"--averaging"},
            required = false,
            description = "Apply a meaning filter in places of value x value")
    private int avg;

    @Parameter(names = {"--nTiles"},
            required = false,
            description = "Tesselation, number of divisions of the image.")
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

    @Parameter(names = { "--help", "-h" },
            description = "Lists all options available", help = true)
    public boolean help;

    public String getInputZip() {
        return inputZip;
    }

    public String getOutputPath() {
        return outputName;
    }

    public String getCodec(){
        return codec;
    }

    public boolean getDecodeOption(){
        return decode;
    }

    public int getFPS(){
        return fps;
    }

    public String getBinarizationFilter(){
        return binFilter;
    }

    public boolean getNegativeOption(){
        return negative;
    }

    public int getAVGFilterValue(){
        return avg;
    }

    public String getTesselationValues(){
        return nTiles;
    }

    public int getSeekRange(){
        return seekRange;
    }

    public int getGOP(){
        return gop;
    }

    public int getQuality(){
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
