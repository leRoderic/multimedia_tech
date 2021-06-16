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
    private String framesTitle;
    private boolean decode;
    private int decodeDelay;

    public FramesViewer(FramesObject fo, int framesPerSecond, boolean d){
        if(!d){
            images = fo.getFrames();
        }else{
            images = new ArrayList<>();
        }
        decode = d;
        framesTitle = fo.getName();
        fps = framesPerSecond;
        window = new JLabel();
    }

    public ArrayList<BufferedImage> getImages(){
        return images;
    }

    public void setDecodeDelay (int d){
        decodeDelay = d;
    }

    public void addImage(BufferedImage b){
        //window.setIcon(new ImageIcon(b));
        images.add(b);
    }

    /**
     * Función para configurar el JFrame que contendrá la reproducción
     *
     */
    public void configWindow(){
        // Establecemos titulo para la ventana y el frame a mostrar
        setTitle("Reproducing " + framesTitle + " at " + fps + " FPS");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        BufferedImage fFrame = images.get(0);
        // Redimensionamos la ventanta para mostrar correctamente el frame
        setPreferredSize(new Dimension(fFrame.getWidth() + 80, fFrame.getHeight() + 80));
        window.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(window, BorderLayout.CENTER);
        pack();
        // Mostramos la ventana
        setVisible(true);
    }

    /**
     * Función para configurar la reproducción con los FPS introducidos por el usuario
     *
     */
    private void reproduce() {
        int index = 0;
        while (index < images.size()) {
            window.setIcon(new ImageIcon(images.get(index)));
            if(!decode) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 / fps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Thread.sleep((1000 / fps) + decodeDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            index++;
        }
    }

    @Override
    public void run() {
        configWindow();
        reproduce();
        dispose();
    }
}
