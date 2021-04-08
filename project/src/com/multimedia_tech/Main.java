package com.multimedia_tech;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

    public static void main(String[] args) {

        //String[] test_args = {"--verbose", "4", "--inputZip", "/.myFile.zip", "--debug", "-np", "500"};
        String[] test_args = {"--verbose", "4", "--debug", "-np", "2500"};
        //String[] test_args = {"-h"};

        ArgParser parser = new ArgParser();
        JCommander jComm = null;

        try{
            jComm = new JCommander(parser, test_args);
            jComm.setProgramName("TM1516_P2.jar");

            if (parser.help) {
                jComm.usage();
            }else{
                System.out.println("Verbose level: " + parser.getVerboseLevel());
                //System.out.println("Working with: " + parser.getInputZipPath());
                System.out.println("Debug mode: " + parser.getDebug());
                System.out.println("NumParameter: " + parser.getNumParam());
            }
        }catch (ParameterException pex){
            System.err.println(pex.getMessage());
            System.err.println("Try --help or -h for help.");
        }

    }
}
