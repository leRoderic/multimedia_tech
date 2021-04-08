package com.multimedia_tech;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class NumInRange implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {

        int number = Integer.parseInt(value);

        if (name.equals("numericParameter") || name.equals("-np")) {
            if (number < ArgParser.numParamLIMITS.MIN.getValue() || number > ArgParser.numParamLIMITS.MAX.getValue()) {
                throw new ParameterException("NumericParameter = " + value + "\nExpected value in range: "
                + Integer.toString(ArgParser.numParamLIMITS.MIN.getValue()) + "-"
                + Integer.toString(ArgParser.numParamLIMITS.MAX.getValue()));
            }
        }
    }
}
