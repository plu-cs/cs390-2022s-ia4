package fractal.core;

import fractal.gui.FractalGui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FractalGenerator {

    // The number of iterations to run.  Set this to a large number so that threads have a lot of work to do.
    public static final int MAX_ITERATIONS = 6000;

    // Start location in the complex plane
    private final double xStart, yStart;

    // Size of a pixel in the complex plane
    private final double xStep, yStep;

    // The image (shared with the GUI thread)
    private FractalImage image;

    private List<FractalGenerationListener> listeners;

    public FractalGenerator(int width, int height) {
        this.image = new FractalImage(width,height);

        // Lower left corner in the complex plane
        this.xStart = -2.0;
        this.yStart = -1.0;

        double ar = (double)width / height;  // Aspect ratio
        double cHeight = 2.0;                // Height in complex plane
        double cWidth = ar * cHeight;        // Width in complex plane

        xStep = cWidth / width;
        yStep = cHeight / height;

        this.listeners = new ArrayList<>();
    }

    public FractalImage getImage() {
        return image;
    }

    /**
     * Add an object that receives generation events
     * @param listener the listener object.
     */
    public void addListener( FractalGenerationListener listener ) {
        listeners.add(listener);
    }

    /**
     * Iterate the Mandelbrot function for the given pixel.
     * @param px the x location of the pixel (pixel column)
     * @param py the y location of the pixel (pixel row)
     * @return the number of iterations required to reach a magnitude of 2.0 or more, or zero if we reached the
     *         maximum number of iterations.
     */
    private int iterate(int px, int py) {
        int height = image.getHeight();

        // Convert the pixel location to the corresponding location in the complex plane
        double cx = xStep * px + xStart;
        double cy = yStep * (height - py - 1) + yStart;

        // Perform the iterations starting with (0,0)
        double zx = 0.0, zy = 0.0;
        double zlen2 = 0.0;
        int iterations = 0;
        while(iterations < MAX_ITERATIONS && zlen2 <= 4.0 ) {
            double zxNext = zx * zx - zy * zy + cx;
            double zyNext = 2.0 * zx * zy + cy;
            zx = zxNext;
            zy = zyNext;
            zlen2 = zx * zx + zy * zy;
            iterations++;
        }
        if( zlen2 <= 4.0 ) {
            return 0;
        } else {
            return iterations;
        }
    }

    /**
     * Generate the fractal image.
     * @param threads the number of threads to use during generation
     */
    public void generate(int threads) {
        // Clear the image
        image.clear();

        int height = image.getHeight();
        int width = image.getWidth();

        long start = System.currentTimeMillis();
        // For each pixel
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                int iterations = iterate(i,j);
                image.writePixel(i,j, iterations);
            }
        }
        long end = System.currentTimeMillis();
        notifyComplete( (end - start) / 1000.0 );
    }

    /**
     * Notifies the listeners that the image is complete.
     * @param time the elapsed time in seconds.
     */
    private void notifyComplete( double time ) {
        for( FractalGenerationListener listener : listeners ) {
            listener.imageComplete(time);
        }
    }

}
