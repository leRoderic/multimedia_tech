package com.multimedia_tech;

import javax.imageio.ImageIO;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FramesManager {

    private static FramesManager mgr = new FramesManager();
    private ArrayList<String> imageNames;
    private Map<String, BufferedImage> images;
    private String fName;
    private ArrayList<Integer> inData;

    private FramesManager() {
        images = new HashMap<>();
        imageNames = new ArrayList<>();
        fName = "";
        inData = new ArrayList<>();
    }

    public static FramesManager getInstance() {
        return mgr;
    }

    public ArrayList<Integer> getInData() {
        return inData;
    }

    /**
     * Función para leer un archivo ZIP de imágenes
     *
     * @param inputZip -> Nombre del archivo ZIP
     */
    public void loadZipImages(String inputZip) throws IOException {

        // Cargamos el ZIP i sus respectivas entradas/imágenes
        ZipFile f = new ZipFile(new File(inputZip));
        fName = f.getName();
        Enumeration<? extends ZipEntry> entries = f.entries();
        // Recorremos cada una de las imágenes de entrada y las cargamos en un HashMap junto con su nombre
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream inStr = f.getInputStream(entry);
            if (entry.getName().contains(".txt")) {
                /*System.out.println(entry.getName());
                Scanner sc = new Scanner(inStr);
                while(sc.hasNext()){
                    System.out.println(sc.next());
                }
                int asd =23;*/

                /*byte[] buf = new byte[1024];
                int counter = inStr.read(buf);
                while((counter != -1)){
                    for(int i=0; i < counter; i++){
                        inData.add((int)buf[i] & 0xff);
                        System.out.println(buf[i]);
                    }
                    counter = inStr.read(buf);
                }*/
                byte[] buf = new byte[4];
                int counter = inStr.read(buf);
                while((counter != -1)) {
                    for (int i = 0; i < counter; i += 4) {
                        int value = buf[i] & 0xff | buf[i + 1] << 8 | buf[i + 2] << 16 | buf[i + 3] << 24;
                        inData.add(value);
                        System.out.println(value);
                    }
                    counter = inStr.read(buf);
                }

            } else {
                BufferedImage img = ImageIO.read(inStr);
                imageNames.add(entry.getName());
                images.put(entry.getName(), img);
            }
        }
        // Cerramos el ZIP y ordenamos la lista de nombres de la imágenes
        f.close();
        Collections.sort(imageNames);
    }

    /**
     * Función que retorna los frames del video
     *
     * @return frames
     */
    public FramesObject getOrderedFrames() {
        ArrayList<BufferedImage> ret = new ArrayList<>();
        for (String n : imageNames) {
            ret.add(images.get(n));
        }
        return new FramesObject(ret, fName);
    }

    /**
     * Función para guardar imagenes en un archivo ZIP
     *
     * @param fname -> Nombre del archivo de salida
     */
    public void saveImagesToZip(String fname, ArrayList<Integer> data) throws IOException {
        if (fname == null) {
            fname = "out";
        }
        FileOutputStream fileOS = new FileOutputStream(fname + ".aor");
        ZipOutputStream zipOS = new ZipOutputStream(fileOS);
        // Para cada imagen, creamos un fichero jpeg temporal,
        // el cual se añadirá al archivo zip mediante la función createFileToZip
        // Finalmente, este fichero se elimina
        for (int i = 0; i < imageNames.size(); i++) {
            String pathName = "img_" + String.format("%0" + String.valueOf(imageNames.size() - 1).length() + "d", i) + ".jpg";
            File tempImage = new File(pathName);
            ImageIO.write(images.get(imageNames.get(i)), "jpg", tempImage);
            createFileToZip(images.get(imageNames.get(i)), pathName, zipOS);
            tempImage.delete();
        }


        /*ZipEntry f = new ZipEntry("aor.txt");
        zipOS.putNextEntry(f);
        for (int i = 0; i < data.size(); i++) {
            zipOS.write((int)data.get(i));
        }
        zipOS.closeEntry();*/


        ZipEntry f = new ZipEntry("aor.txt");
        zipOS.putNextEntry(f);

        for (int i = 0; i < data.size(); i++) {
            //d[i] = data.get(i).byteValue();
            byte d[] = new byte[4];
            d[0] = (byte) (data.get(i) >> 0);
            d[1] = (byte) (data.get(i) >> 8);
            d[2] = (byte) (data.get(i) >> 16);
            d[3] = (byte) (data.get(i) >> 24);
            //System.out.println(data.get(i));
            zipOS.write(d, 0, d.length);
        }

        zipOS.closeEntry();


        // Cerramos OutputsStreams
        zipOS.finish();
        zipOS.close();
    }

    public void saveImagesToZip(String fname) throws IOException {

        FileOutputStream fileOS = new FileOutputStream(fname + ".aor");
        ZipOutputStream zipOS = new ZipOutputStream(fileOS);
        // Para cada imagen, creamos un fichero jpeg temporal,
        // el cual se añadirá al archivo zip mediante la función createFileToZip
        // Finalmente, este fichero se elimina
        for (int i = 0; i < imageNames.size(); i++) {
            String pathName = "img_" + String.format("%0" + String.valueOf(imageNames.size() - 1).length() + "d", i) + ".jpg";
            File tempImage = new File(pathName);
            ImageIO.write(images.get(imageNames.get(i)), "jpg", tempImage);
            createFileToZip(images.get(imageNames.get(i)), pathName, zipOS);
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
     * @param zipOS    -> Archivo ZIP de salida
     */
    private void createFileToZip(BufferedImage i, String fileName, ZipOutputStream zipOS) throws FileNotFoundException, IOException {
        // Creamos la imagen y el input stream
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        // Creamos una entrada para la imagen a guardar en el ZIP
        ZipEntry zipEntry = new ZipEntry(fileName);
        //Guardamos la imagen como un array de bytes
        zipOS.putNextEntry(zipEntry);
        ImageIO.write(i, "jpg", zipOS);
        // Cerramos entrada ZIP de la imagen y OutputsStream
        zipOS.closeEntry();
        fis.close();
    }
}
