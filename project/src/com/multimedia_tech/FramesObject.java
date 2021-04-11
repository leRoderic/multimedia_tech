package com.multimedia_tech;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FramesObject {

    private ArrayList<BufferedImage> frames;
    private String name;

    public FramesObject(ArrayList<BufferedImage> a, String n){
        frames = a;
        name = n;
    }

    public String getName(){
        return name;
    }

    public ArrayList<BufferedImage> getFrames(){
        return frames;
    }
}
