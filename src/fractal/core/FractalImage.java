package fractal.core;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An object to maintain the image.  Internally, it stores a BufferedImage object that
 * can be used to display within a GUI or written to a file.
 *
 * This is the object that we'll share between threads.
 * For this exercise, we won't synchronize threads' access to this object because we'll
 * take care to make sure that only ONE thread will write to a given pixel.  The GUI will ONLY
 * read (to display the image in progress), and we will accept that the GUI may see data that
 * is not completely up to date.
 */
public class FractalImage {

    // The image
    private final BufferedImage image;

    // The number of iterations that corresponds to the end of the color scale
    private static final int COLOR_SCALE_ITERATIONS = 20;

    // The color scale
    private static final int[] COLORS = {0x85c1c8, 0x90a1be, 0x9c8184, 0xa761aa,
            0xaf4980, 0xb83055, 0xc0182a, 0xc80000, 0xd33300, 0xde6600, 0xe99900, 0xf4cc00, 0xffff00};

    // Image dimensions
    private int width;
    private int height;

    public FractalImage( int w, int h ) {
        width = w;
        height = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Write a color to a pixel based on the number of iterations.  This will be called by
     * multiple worker threads, but we must take care that there is only ONE thread that writes
     * to a given pixel.
     * @param x the x pixel coordinate
     * @param y the y pixel coordinate
     * @param iterations the number of iterations
     */
    public void writePixel( int x, int y, int iterations ) {
        int color;
        if( iterations == 0 ) color = 0;
        else {
            double f = ((double) iterations / COLOR_SCALE_ITERATIONS) * COLORS.length;
            int index = (int)f;
            if( index >= COLORS.length ) index = COLORS.length - 1;
            color = COLORS[ index ];
        }
        image.setRGB(x,y,color);
    }

    /**
     * Draw the image to the provided Graphics context.  This should ONLY be called on the
     * GUI event dispatch thread.
     * @param g a Graphics object
     */
    public void draw(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    /**
     * Clear image to black.
     */
    public void clear() {
        Graphics g = image.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0,0,width,height);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
