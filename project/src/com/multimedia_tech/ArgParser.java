package com.multimedia_tech;

import com.beust.jcommander.Parameter;

public class ArgParser {

    @Parameter(names = { "--help", "-h" },
            description = "Prints a help message", help = true)
    public boolean help;

    @Parameter(names = "--debug",
            description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = { "--verbose" },
            description = "Level of verbosity")
    private int verbose = 1;

    @Parameter(names = {"--inputZip", "-i"},
            required = false,
            description = "Zip file with the input images",
            validateWith = FileExists.class)
    private String inputZip;

    @Parameter(names = {"--outputPath", "-p"},
            required = false,
            description = "Output path to save the images")
    private String outputPath;

    @Parameter(names = {"--numericParameter", "-np"},
            required = true,
            description = "A number used as a parameter",
            validateWith = NumInRange.class)
    private int numParam = 0;



    public boolean getDebug() {
        return debug;
    }

    public int getVerboseLevel() {
        return verbose;
    }

    public String getInputZipPath() {
        return inputZip;
    }

    public String getOutputPath() {
        return outputPath;
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

    public int getNumParam() {
        return numParam;
    }
}
