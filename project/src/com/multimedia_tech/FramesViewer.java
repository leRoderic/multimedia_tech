package com.multimedia_tech;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FramesViewer extends JFrame implements Runnable {

    private int fps;
    private ArrayList<BufferedImage> images;
    public JLabel window;

    public FramesViewer(FramesObject fo, int framesPerSecond){
        images = fo.getFrames();
        fps = framesPerSecond;
        window = new JLabel();
    }

    private void configWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        BufferedImage fFrame = images.get(0);
        setPreferredSize(new Dimension(fFrame.getWidth() + 50, fFrame.getHeight() + 50));
        window.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(window, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void reproduce(){
        for(BufferedImage i: images){
            window.setIcon(new ImageIcon(i));
            try{
                TimeUnit.MILLISECONDS.sleep(1000 / fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        configWindow();
        reproduce();
        dispose();
    }
}
