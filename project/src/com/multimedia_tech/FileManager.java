package com.multimedia_tech;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileManager {

    private static FileManager mgr = new FileManager();
    private ArrayList<String> imageNames;
    private Map<String, BufferedImage> images;

    private FileManager(){
        images = new HashMap<>();
        imageNames = new ArrayList<>();
    }

    public static FileManager getInstance(){
        return mgr;
    }

    public void loadZipImages(String inputZip) throws IOException {
        ZipFile f = new ZipFile(new File(inputZip));

        Enumeration<? extends ZipEntry> entries = f.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream inStr = f.getInputStream(entry);
            BufferedImage img = ImageIO.read(inStr);
            imageNames.add(entry.getName());
            images.put(entry.getName(), img);
        }
        f.close();
        Collections.sort(imageNames);
    }

    public FramesObject getOrderedFrames(){
        ArrayList<BufferedImage> ret = new ArrayList<>();
        for(String n: imageNames){
            ret.add(images.get(n));
        }
        return new FramesObject(ret);
    }

    public void saveImagesToZip(String fname) throws IOException{
        FileOutputStream fileOS = new FileOutputStream(fname + ".aor");
        ZipOutputStream zipOS = new ZipOutputStream(fileOS);
        for (int i =0; i< imageNames.size(); i++) {
            String pathName = "img_" + Integer.toString(i) + ".jpg";
            File tempImage = new File(pathName);
            ImageIO.write(images.get(imageNames.get(i)),"jpg",tempImage);
            createFileToZip(pathName, zipOS);
            tempImage.delete();
        }
        zipOS.finish();
        zipOS.close();
    }

    private void createFileToZip(String fileName, ZipOutputStream zipOS) throws FileNotFoundException, IOException {
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
