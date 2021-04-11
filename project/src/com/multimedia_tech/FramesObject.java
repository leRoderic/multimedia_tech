package com.multimedia_tech;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FramesObject {

    private ArrayList<BufferedImage> frames;

    public FramesObject(ArrayList<BufferedImage> a){
        frames = a;
    }

    public ArrayList<BufferedImage> getFrames(){
        return frames;
    }
}
