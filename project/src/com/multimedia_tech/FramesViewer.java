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

    public FramesViewer(FramesObject fo, int framesPerSecond, boolean decode){
        if(!decode){
            images = fo.getFrames();
        }else{
            images = new ArrayList<>();
        }
        framesTitle = fo.getName();
        fps = framesPerSecond;
        window = new JLabel();
    }

    public void addImage(BufferedImage b){
        images.add(b);
        if(images.size() == 1)
            run();
    }

    /**
     * Función para configurar el JFrame que contendrá la reproducción
     *
     */
    private void configWindow(){
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
