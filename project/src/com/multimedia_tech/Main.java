package com.multimedia_tech;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Main {

    public static ArrayList<String> imageNames = new ArrayList<>();
    public static Map<String, BufferedImage> images = new HashMap<>();

    public static void main(String[] args) {

        String[] test_args = {"--verbose", "4", "--inputZip", "images/Cubo.zip", "--outputPath" , "images/CuboGuardado.zip" ,"--debug", "-np", "500"};
        //String[] test_args = {"--verbose", "4", "--debug", "-np", "2500"};
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
                System.out.println("Working with: " + parser.getInputZipPath());
                System.out.println("Debug mode: " + parser.getDebug());
                System.out.println("NumParameter: " + parser.getNumParam());

                System.out.println("-----------------------");
                System.out.println("Leyendo carpeta");
                readZip(parser.getInputZipPath());

                System.out.println("Guardando imagenes en: " + parser.getOutputPath());

                saveZip(parser.getOutputPath());


            }
        }catch (ParameterException | IOException pex){
            System.err.println(pex.getMessage());
            System.err.println("Try --help or -h for help.");
        }

    }

    public static void readZip(String inputZip) throws IOException{
        ZipFile f = new ZipFile(new File(inputZip));

        Enumeration<? extends ZipEntry> entries = f.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream inStr = f.getInputStream(entry);
            BufferedImage img = ImageIO.read(inStr);
            //visualizar

            //guardamos nombres e imagenes para saveZip
            imageNames.add(entry.getName());
            images.put(entry.getName(), img);
        }
        f.close();
    }

    //Funcion encargada de crear la carpeta zip
    public static void saveZip(String path) throws IOException{
        FileOutputStream fileOS = new FileOutputStream(path);
        ZipOutputStream zipOS = new ZipOutputStream(fileOS);

        for (int i =0; i< imageNames.size(); i++) {
            String pathName = "img_"+Integer.toString(i)+".jpg";
            File tempImage = new File(pathName);
            ImageIO.write(images.get(imageNames.get(i)),"jpg",tempImage);
            createFileToZip(pathName, zipOS);
            tempImage.delete();
        }
        zipOS.finish();
        zipOS.close();
    }

    //Función para guardar imagen en la carpeta zip
    public static void createFileToZip(String pathName, ZipOutputStream zipOS) throws FileNotFoundException, IOException {
        File f = new File(pathName);
        FileInputStream fis = new FileInputStream(f);
        ZipEntry zipEntry = new ZipEntry(pathName);
        zipOS.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOS.write(bytes, 0, length);
        }
        zipOS.closeEntry();
        fis.close();
    }
}
