package com.multimedia_tech;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FramesManager {

    private static FramesManager mgr = new FramesManager();
    private ArrayList<String> imageNames;
    private Map<String, BufferedImage> images;
    private String fName;

    private FramesManager(){
        images = new HashMap<>();
        imageNames = new ArrayList<>();
        fName = "";
    }

    public static FramesManager getInstance(){
        return mgr;
    }

    /**
     * Función para leer un archivo ZIP de imágenes
     *
     * @param inputZip -> Nombre del archivo ZIP
     *
     * */
    public void loadZipImages(String inputZip) throws IOException {

        // Cargamos el ZIP i sus respectivas entradas/imágenes
        ZipFile f = new ZipFile(new File(inputZip));
        fName = f.getName();
        Enumeration<? extends ZipEntry> entries = f.entries();
        // Recorremos cada una de las imágenes de entrada y las cargamos en un HashMap junto con su nombre
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream inStr = f.getInputStream(entry);
            BufferedImage img = ImageIO.read(inStr);
            imageNames.add(entry.getName());
            images.put(entry.getName(), img);
        }
        // Cerramos el ZIP y ordenamos la lista de nombres de la imágenes
        f.close();
        Collections.sort(imageNames);
    }

    /**
     * Función que retorna los frames del video
     *
     * @return frames
     *
     * */
    public FramesObject getOrderedFrames(){
        ArrayList<BufferedImage> ret = new ArrayList<>();
        for(String n: imageNames){
            ret.add(images.get(n));
        }
        return new FramesObject(ret, fName);
    }

    /**
     * Función para guardar imagenes en un archivo ZIP
     *
     * @param fname -> Nombre del archivo de salida
     *
     * */
    public void saveImagesToZip(String fname) throws IOException{

        FileOutputStream fileOS = new FileOutputStream(fname + ".aor");
        ZipOutputStream zipOS = new ZipOutputStream(fileOS);
        // Para cada imagen, creamos un fichero jpeg temporal,
        // el cual se añadirá al archivo zip mediante la función createFileToZip
        // Finalmente, este fichero se elimina
        for (int i =0; i< imageNames.size(); i++) {
            String pathName = "img_" + Integer.toString(i) + ".jpg";
            File tempImage = new File(pathName);
            ImageIO.write(images.get(imageNames.get(i)),"jpg",tempImage);
            createFileToZip(pathName, zipOS);
            tempImage.delete();
        }
        // Cerramos OutputsStreams
        zipOS.finish();
        zipOS.close();
    }

    /**
     * Función para guardar imagen en el archivo ZIP
     *
     * @param fileName -> Nombre de la imagen;
     * @param zipOS -> Archivo ZIP de salida
     *
     * */
    private void createFileToZip(String fileName, ZipOutputStream zipOS) throws FileNotFoundException, IOException {
        // Creamos la imagen y el input stream
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        // Creamos una entrada para la imagen a guardar en el ZIP
        ZipEntry zipEntry = new ZipEntry(fileName);
        //Guardamos la imagen como un array de bytes
        zipOS.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOS.write(bytes, 0, length);
        }
        // Cerramos entrada ZIP de la imagen y OutputsStream
        zipOS.closeEntry();
        fis.close();
    }
}
