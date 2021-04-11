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

        ArgParser parser = new ArgParser();
        JCommander jComm = null;

        try{
            jComm = new JCommander(parser, args);
            jComm.setProgramName("AUAUVR.jar");

            if (parser.help) {
                jComm.usage();
            }
            readZip(parser.getInputZip());

            if(parser.getOutputPath() != null){
                System.out.println("ASDASDASD");
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

    //Funcion encargada de crear el fichero zip
    public static void saveZip(String fname) throws IOException{
        FileOutputStream fileOS = new FileOutputStream(fname);
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
    public static void createFileToZip(String fileName, ZipOutputStream zipOS) throws FileNotFoundException, IOException {
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        ZipEntry zipEntry = new ZipEntry(fileName);
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
