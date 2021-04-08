package com.multimedia_tech;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class FileExists implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {

        File f = new File(value);
        if (!f.exists() || f.isDirectory()){
            throw new ParameterException("Input zip file is not a valid file.");
        }
    }
}
