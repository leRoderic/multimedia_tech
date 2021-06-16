package com.multimedia_tech;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class FramesObject {

    private ArrayList<BufferedImage> frames;
    private String name;

    public FramesObject(ArrayList<BufferedImage> a, String n) {
        frames = a;
        name = n;
    }

    public String getName() {
        return name;
    }

    public ArrayList<BufferedImage> getFrames() {
        return frames;
    }

    /**
     * Función para aplicar el filtro negativo al vídeo
     */
    public void applyNegativeFilter() {
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    // Se obtiene el color del píxel
                    int rgb = i.getRGB(x, y);
                    Color col = new Color(rgb);
                    // Se crea un nuevo color aplicando el filto negativo: 255 - color original píxel
                    col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                    // Se asigna el color creado anteriormente al píxel de la imagen/frame
                    i.setRGB(x, y, col.getRGB());
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro medio al vídeo
     *
     * @param val -> rango de vecindad
     */
    public void applyMeanFilter(int val) {
        int alpha, red, green, blue;
        int vals[];
        Color col;
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            vals = new int[i.getWidth() * i.getHeight()];
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    alpha = 0;
                    red = 0;
                    green = 0;
                    blue = 0;
                    int count = 0;
                    // Se recorre la vecindad de dicho píxel para poder calcular la media
                    for (int r = y - (val / 2); r <= y + (val / 2); r++) {
                        for (int c = x - (val / 2); c <= x + (val / 2); c++) {
                            // Condición para evitar coger píxeles vecinos que se encuentren
                            // fuera del rango de la imagen original
                            if (r < 0 || r >= i.getHeight() || c < 0 || c >= i.getWidth()) {
                                continue;
                            }
                            // Acumulamos los valores de cada componente del color en esa vecindad
                            else {
                                col = new Color(i.getRGB(c, r));
                                alpha += col.getAlpha();
                                red += col.getRed();
                                green += col.getGreen();
                                blue += col.getBlue();
                                count++;
                            }
                        }
                    }
                    // Calculamos la media a partir de los valores acumulados
                    int p = ((alpha / count) << 24) | ((red / count) << 16) | ((green / count) << 8) | (blue / count);
                    vals[x + y * i.getWidth()] = p;
                }
            }
            for (int y = 0; y < i.getHeight(); y++) {
                for (int x = 0; x < i.getWidth(); x++) {
                    // Asignamos dicho valor al píxel de la imagen/frame
                    i.setRGB(x, y, vals[x + (y * i.getWidth())]);
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro grayscale al vídeo
     */
    public void applyGrayScaleFilter() {
        int red, green, blue;
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    // Cogemos el color del píxel y le asignamos un peso a cada una de sus componentes RGB
                    // en nuestro caso: R-> 0.299 G-> 0.587 B-> 0.114
                    Color c = new Color(i.getRGB(x, y));
                    red = (int) (c.getRed() * 0.299);
                    green = (int) (c.getGreen() * 0.587);
                    blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                    // Asignamos dicho color al píxel de la imagen/frame
                    i.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    /**
     * Función para aplicar el filtro binario al vídeo
     *
     * @param val -> valor que establece cuando un píxel es negro o blanco
     */
    public void applyBinarizationFilter(int val) {
        int col, red, green, blue;
        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            for (int x = 0; x < i.getWidth(); x++) {
                for (int y = 0; y < i.getHeight(); y++) {
                    // Modificamos el color del píxel al igual que grayscale
                    Color c = new Color(i.getRGB(x, y));
                    red = (int) (c.getRed() * 0.299);
                    green = (int) (c.getGreen() * 0.587);
                    blue = (int) (c.getBlue() * 0.114);
                    // Si el color obtenido con la suma de cada uno de los componentes del píxel
                    // es mayor al valor que introduce el usuario, este píxel pasara a ser blanco
                    if ((red + green + blue) >= val) {
                        col = 255;
                    }
                    // Sino, pasará a ser negro
                    else {
                        col = 0;
                    }
                    Color newColor = new Color(col, col, col);
                    // Asignamos dicho color al píxel de la imagen/frame
                    i.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    /**
     * Función para plicar detecion de contornos al vídeo
     */
    public void applyEdgeDetectionFilter() {
        int maxGradient = -1;

        for (BufferedImage i : frames) {
            // Se recorre el frame de vídeo píxel a píxel
            int[][] edgeColors = new int[i.getWidth()][i.getHeight()];
            for (int x = 1; x < i.getWidth() - 1; x++) {
                for (int y = 1; y < i.getHeight() - 1; y++) {

                    //Recorre la vecindad el píxel pasando cada un de estos a grayscale
                    int val00 = getGrayScale(i.getRGB(x - 1, y - 1));
                    int val01 = getGrayScale(i.getRGB(x - 1, y));
                    int val02 = getGrayScale(i.getRGB(x - 1, y + 1));

                    int val10 = getGrayScale(i.getRGB(x, y - 1));
                    int val11 = getGrayScale(i.getRGB(x, y));
                    int val12 = getGrayScale(i.getRGB(x, y + 1));

                    int val20 = getGrayScale(i.getRGB(x + 1, y - 1));
                    int val21 = getGrayScale(i.getRGB(x + 1, y));
                    int val22 = getGrayScale(i.getRGB(x + 1, y + 1));

                    // Aplicamos el filtro de gradiente Sovel tanto en horizontal como en vertical
                    int gx = ((-1 * val00) + (0 * val01) + (1 * val02))
                            + ((-2 * val10) + (0 * val11) + (2 * val12))
                            + ((-1 * val20) + (0 * val21) + (1 * val22));

                    int gy = ((-1 * val00) + (-2 * val01) + (-1 * val02))
                            + ((0 * val10) + (0 * val11) + (0 * val12))
                            + ((1 * val20) + (2 * val21) + (1 * val22));

                    double gval = Math.sqrt((gx * gx) + (gy * gy));
                    int g = (int) gval;

                    // Guardamos el máximo gradiente encontrado hasta el momento
                    if (maxGradient < g) {
                        maxGradient = g;
                    }

                    edgeColors[x][y] = g;
                }
            }
            // Normalizamos los gradientes a partir del máximo encontrado
            double scale = 255.0 / maxGradient;

            for (int x = 1; x < i.getWidth() - 1; x++) {
                for (int y = 1; y < i.getHeight() - 1; y++) {
                    int edgeColor = edgeColors[x][y];
                    edgeColor = (int) (edgeColor * scale);
                    edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;
                    // Modificamos el color al píxel de la imagen/frame a partir del gradiente calculado anteriormente
                    i.setRGB(x, y, edgeColor);
                }
            }
        }
    }

    /**
     * Función soporte para aplicar grayscale a un píxel
     *
     * @param rgb -> Valor del color RGB
     * @return int grayscale
     */
    public static int getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);

        return gray;
    }

    /**
     * Función para aplicar la codificación de la imágenes
     *
     * @param gop -> Numero de imágenes entre dos frames
     * @param seekRange -> Desplazamiento maximo en la busqueda de teselas coincidentes
     * @param quality -> Factor de calidad que determina cuando dos teselas son coincidentes
     * @param xTiles -> Numero de teselas en la que dividir la imagen en el eje x
     * @param yTiles -> Numero de teselas en la que dividir la imagen en el eje y
     * @return ArrayList</Integers> data
     */
    public ArrayList<Integer> encode(int gop, int seekRange, int quality, int xTiles, int yTiles) {
        ArrayList<int[]> matchCoordinates;
        ArrayList<Integer> data = new ArrayList();
        // ESTRUCTURA-> GOP xTile yTiles #Frame #Concidences:[x y #Frame, x y #Frame..] #Concidences:[x y #Frame, x y #Frame..] ..
        //data.add(111);
        // Guardamos gop, xTiles, yTiles
        data.add(gop);
        data.add(xTiles);
        data.add(yTiles);
        int coincidence = 0;

        BufferedImage[] reference = null;
        long startTime = System.currentTimeMillis();
        // Recorremos todos los frames
        for (int i = 0; i < frames.size(); i++) {
            coincidence = 0;
            // En caso de especificar un numero de tesela no multiplo a los pixeles de la imágen, paramos
            if (frames.get(i).getWidth() % xTiles != 0 || frames.get(i).getHeight() % yTiles != 0) {
                System.out.println("Error> Specified tesselation parameters bigger than image");
                System.exit(-1);
            }
            int rows = frames.get(i).getWidth() / xTiles;
            int cols = frames.get(i).getHeight() / yTiles;

            // Imégenes intercuadro
            if (i % gop != 0) {
                matchCoordinates = new ArrayList<>();
                //Guardamos el id del frame y un 0 de referencia
                data.add(i);
                data.add(0);
                int index = data.size() - 1;

                // Buscamos coinicdencias entre los frames
                for (int j = 0; j < reference.length; j++) {
                    if (findCoincidence(i, frames.get(i), reference[j], xTiles, yTiles, quality, rows, cols, seekRange, data, matchCoordinates)) {

                        coincidence -= -1;
                    }
                }

                // Si se han encontrada coinidencias, modificamos el valor 0 añadido anteriormente
                if(coincidence > 0){
                    data.set(index, coincidence);
                    // Aplicamos el filtro medio para evitar cambios de color bruscos
                    int x, y;
                    for (int m = 0; m < matchCoordinates.size(); m++) {
                        x = matchCoordinates.get(m)[0];
                        y = matchCoordinates.get(m)[1];
                        int[] colors = frames.get(i).getRGB(x, y, xTiles, yTiles, null, 0, xTiles);
                        int r = 0, g = 0, b = 0;
                        for (int c : colors) {
                            r += ((c >> 16) & 0xFF);
                            g += ((c >> 8) & 0xFF);
                            b += (c & 0xFF);
                        }
                        int R = r / colors.length;
                        int G = g / colors.length;
                        int B = b / colors.length;

                        int[] rgbArray = new int[(xTiles - 2) * (yTiles - 2)];
                        Color c = new Color(R, G, B);

                        Arrays.fill(rgbArray, c.getRGB());
                        frames.get(i).setRGB(++x, ++y, xTiles - 2, yTiles - 2, rgbArray, 0, 0);
                    }
                }
            }
            //Imagen de referencia
            else{
                reference = subdivideFrames(frames.get(i), xTiles, yTiles, rows, cols);
            }
        }
        long elapsedTime = (System.currentTimeMillis() - startTime);
        System.out.println("AOR> Encoded " + frames.size() + " frames in " + elapsedTime/1000F + " seconds");
        return data;
    }

    /**
     * Función para subdividir un frame en teselas
     *
     * @param frame -> Freme a tratar
     * @param xTiles -> Teselas en el eje x
     * @param yTiles -> Teselas en el eje y
     * @param rows -> Numero de filas de la imagen
     * @param cols -> Nuemro de columnas de la imagen
     * @return BufferedImage[] subimage
     */
    public BufferedImage[] subdivideFrames(BufferedImage frame, int xTiles, int yTiles, int rows, int cols) {
        int count = 0;

        BufferedImage subimages[] = new BufferedImage[rows * cols];
        // Recorremos el frame i obtenemos las teselas
        for (int y = 0; y < frame.getHeight(); y += yTiles) {
            for (int x = 0; x < frame.getWidth(); x += xTiles) {
                subimages[count] = frame.getSubimage(x, y, xTiles, yTiles);
                count++;

            }
        }
        return subimages;
    }

    /**
     * Función para buscar coincidencias entre frames
     *
     * @param nFrame -> Id el Frame a comparar
     * @param frame -> Frame a comparar
     * @param reference -> Frame de referencia
     * @param xTiles -> Teselas en el eje x
     * @param yTiles -> Teselas en el eje y
     * @param quality -> Factor de calidad que determina cuando dos teselas son coincidentes
     * @param rows -> Numero de filas de la imagen
     * @param cols -> Nuemro de columnas de la imagen
     * @param seekRange -> Desplazamiento maximo en la busqueda de teselas coincidentes
     * @param d -> Catos a guardar
     * @param c -> Coordenadas donde se encuentran las coincidencias
     * @return boolean True si se ha encontrado alguna coincidencia, False en caso contrario
     */
    private boolean findCoincidence(int nFrame, BufferedImage frame, BufferedImage reference, int xTiles,
                                    int yTiles, int quality, int rows, int cols, int seekRange, ArrayList<Integer> d,
                                    ArrayList<int[]> c) {


        int xMin, xMax, yMin, yMax;
        yMin = (nFrame % rows) * yTiles - seekRange;
        if (yMin < 0) {
            yMin = 0;
        }

        yMax = yTiles * ((nFrame % rows) + 1) + seekRange;
        if (yMax > frames.get(0).getWidth()) {
            yMax = frames.get(0).getWidth();
        }

        xMin = (nFrame / rows) * xTiles - seekRange;
        if (xMin < 0) {
            xMin = 0;
        }

        xMax = xTiles * ((nFrame / rows) + 1) + seekRange;
        if (xMax > frames.get(0).getHeight()) {
            xMax = frames.get(0).getHeight();
        }

        for (int y = yMin; y <= yMax - yTiles; y++) {
            for (int x = xMin; x <= xMax - xTiles; x++) {
                // Buscamos coincidencias entre las teselas
                double correlation = compareImages(frame.getSubimage(y, x, xTiles, yTiles), reference);
                // Se usa el factor de calidad pasado por consola
                if (correlation < quality) {
                    c.add(new int[]{y, x});
                    d.add(y);
                    d.add(x);
                    d.add(nFrame);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Función para comparar teselas
     *
     * @param image1
     * @param image2
     * @return double variation
     */
    private double compareImages(BufferedImage image1, BufferedImage image2) {
        assert (image1.getHeight() == image2.getHeight() && image1.getWidth() == image2.getWidth());
        double variation = 0.0;
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                variation += compareARGB(image1.getRGB(x, y), image2.getRGB(x, y)) / Math.sqrt(3);
            }
        }
        return variation / (image1.getWidth() * image2.getHeight());
    }

    /**
     * Funcionpara comparar dos colores
     *
     * @param rgb1
     * @param rgb2
     * @return double
     */
    private double compareARGB(int rgb1, int rgb2) {
        double r1 = ((rgb1 >> 16) & 0xFF) / 255.0;
        double r2 = ((rgb2 >> 16) & 0xFF) / 255.0;
        double g1 = ((rgb1 >> 8) & 0xFF) / 255.0;
        double g2 = ((rgb2 >> 8) & 0xFF) / 255.0;
        double b1 = (rgb1 & 0xFF) / 255.0;
        double b2 = (rgb2 & 0xFF) / 255.0;
        double a1 = ((rgb1 >> 24) & 0xFF) / 255.0;
        double a2 = ((rgb2 >> 24) & 0xFF) / 255.0;

        return a1 * a2 * Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
    }

    /**
     * Función para calcular la media de PSNR
     *
     * @param marks
     * @return double media
     */
    private double calculateAverage(ArrayList<Double> marks) {
        double sum = 0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    /**
     * Función para aplicar la codificación de la imágenes
     *
     * @param d -> Datos de teselacion
     * @param fv -> Objecto FrameViewer
     */
    public void decode(ArrayList<Integer> d, FramesViewer fv) {

        int counter = 0;
        // ESTRUCTURA-> GOP xTile yTiles #Frame #Concidences:[x y #Frame, x y #Frame..] #Concidences:[x y #Frame, x y #Frame..] ..
        //Recuperamos gop, xTiles, yTiles
        int gop = d.get(counter++);
        int xTiles = d.get(counter++);
        int yTiles = d.get(counter++);
        // Inicializamos el thread para visualizar simultaneamente
        Thread t = new Thread(fv);
        // Iniciamos el temporizador
        long startTime = System.currentTimeMillis();
        // Mientras no se haya llegado al final de fichero de datos
        while (counter < d.size()) {
            long start = System.currentTimeMillis();
            //Recuperamos el id del frame i las coincidencias
            int nFrame = d.get(counter++);
            int coincidence = d.get(counter++);
            int rows = frames.get(nFrame).getWidth() / xTiles;
            int cols = frames.get(nFrame).getHeight() / yTiles;
            // Recorremos todas las coincidencias
            for (int i = 0; i < coincidence; i++) {
                //Recuperamos las posiciones x,y e id del fream de cada coincidencia
                int y = d.get(counter++);
                int x = d.get(counter++);
                int nFrameC = d.get(counter++);

                //Reconstruimos el frame con los datos recuperados
                BufferedImage frameI = frames.get((nFrame / gop) * gop).getSubimage((nFrameC % rows) * xTiles, (nFrameC % cols) * yTiles, xTiles, yTiles);
                for (int fy = 0; fy < frameI.getHeight(); fy++) {
                    for (int fx = 0; fx < frameI.getWidth(); fx++) {
                        int color = frameI.getRGB(fx, fy);
                        try {
                            frames.get(nFrame).setRGB(x * xTiles + fx, y * yTiles + fy, color);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            // Si no hay instancia de FrameViewer, sen entiende que está seleccionada la opción de batch y, por ende,
            // que no será necesaria la reproducción de los frames.
            if(fv != null) {
                // En caso contrario se ejecuta el método run de la instancia en un thread separado.
                // Para compensar el tiempo de procesado, se le pasa el tiempo que el codec ha necesitado para
                // hacer el decode. Este delay se usa luego para muestrear las imágenes junto a los FPS.
                fv.setDecodeDelay((int) (System.currentTimeMillis() - start));
                fv.addImage(frames.get(nFrame));
                if (fv.getImages().size() == 1)
                    t.start();
            }
        }
        long elapsedTime = (System.currentTimeMillis() - startTime);
        d.clear();
        ArrayList<Double> avgPSNR = new ArrayList<>();
        System.out.println("AOR> Decoded " + frames.size() + " frames in " + elapsedTime/1000F + " seconds");
        for(int i=1; i < frames.size(); i++){
            avgPSNR.add(PSNR.calculate_PSNR(frames.get(i - 1), frames.get(i)));
        }
        System.out.println("AOR> Average PSNR is " + String.format("%.4f", calculateAverage(avgPSNR)));
    }
}
