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
    private ArrayList<Integer> inData;

    /**
     * Constructor privado porque se aplica el patrón Singleton. El objetivo es que exista una única instancia de
     * esta clase.
     */
    private FramesManager() {
        images = new HashMap<>();
        imageNames = new ArrayList<>();
        fName = "";
        inData = new ArrayList<>();
    }

    /**
     * Devuelve la única instancia de la clase.
     *
     * @return instancia clase FramesManager
     */
    public static FramesManager getInstance() {
        return mgr;
    }

    /**
     * Devuelve los datos para la descompresión, provinientes del fichero interno añadido duranta la compresión.
     *
     * @return  array con los datos
     */
    public ArrayList<Integer> getInData() {
        return inData;
    }

    /**
     * Función para leer un archivo ZIP de imágenes
     *
     * @param inputZip -> Nombre del archivo ZIP
     */
    public long loadZipImages(String inputZip) throws IOException {

        // Cargamos el ZIP y sus respectivas entradas/imágenes
        ZipFile f = new ZipFile(new File(inputZip));
        fName = f.getName();
        System.out.println("AOR> Loading frames...");
        Enumeration<? extends ZipEntry> entries = f.entries();
        // Recorremos cada una de las imágenes de entrada y las cargamos en un HashMap junto con su nombre
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream inStr = f.getInputStream(entry);
            // Si el fichero termina en .data, se trata del fichero interno usado por el codec para guardar las
            // teselas concidentes. Se procesa por separado.
            if (entry.getName().contains(".data")) {
                System.out.println("AOR> Found .data file. Parsing values....");
                // Los datos, en la compresión, se serializan como enteros de 4 bytes.
                byte[] buf = new byte[4];
                int counter = inStr.read(buf);
                while((counter != -1)) {
                    for (int i = 0; i < counter; i += 4) {
                        // Cada entero se lee como 4 bytes.
                        int value = buf[i] & 0xff | buf[i + 1] << 8 | buf[i + 2] << 16 | buf[i + 3] << 24;
                        inData.add(value);
                    }
                    counter = inStr.read(buf);
                }

            } else {
                // Cada frame se lee en esta sección.
                BufferedImage img = ImageIO.read(inStr);
                imageNames.add(entry.getName());
                images.put(entry.getName(), img);
            }
        }
        // Cerramos el ZIP y ordenamos la lista de nombres de las imágenes
        f.close();
        Collections.sort(imageNames);
        System.out.println("AOR> File loading complete");
        return new File(inputZip).length();
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
     * Función para guardar imagenes en un archivo ZIP, tras su compresión
     *
     * @param fname -> Nombre del archivo de salida
     */
    public long saveImagesToZip(String fname, ArrayList<Integer> data) throws IOException {
        // Por defecto, si no se indica lo contrario, se establece el nombre del fichero de salida como out
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

        // Creamos una nueva entrada para el fichero que almacenara los datos
        ZipEntry f = new ZipEntry("aor.data");
        zipOS.putNextEntry(f);
        // Guardamos cada int en un array de 4 bytes
        for (int i = 0; i < data.size(); i++) {
            byte d[] = new byte[4];
            d[0] = (byte) (data.get(i) >> 0);
            d[1] = (byte) (data.get(i) >> 8);
            d[2] = (byte) (data.get(i) >> 16);
            d[3] = (byte) (data.get(i) >> 24);
            zipOS.write(d, 0, d.length);
        }
        // Cerramos la entrada
        zipOS.closeEntry();

        // Cerramos OutputsStreams
        zipOS.finish();
        zipOS.close();
        System.out.println("AOR> Frames saved in " + fname + ".aor file");
        // Devolvemos el tamaño final para poder calcular la compresión
        return new File(fname + ".aor").length();
    }

    /**
     * Función para guardar imagenes en un archivo ZIP, sin compresión previa.
     *
     * @param fname -> Nombre del archivo de salida
     */
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
        System.out.println("AOR> Frames saved in " + fname + ".aor file");
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
