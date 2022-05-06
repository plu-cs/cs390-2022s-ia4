package fractal.gui;

import fractal.core.FractalGenerationListener;
import fractal.core.FractalGenerator;
import fractal.core.FractalImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GUI for our fractal generator.
 */
public class FractalGui implements FractalGenerationListener {

    // Image dimensions
    private static final int IMAGE_WIDTH = 1024;
    private static final int IMAGE_HEIGHT = 768;

    // The fractal image
    private FractalImage image;

    // The panel that displays the image
    private final ImagePanel imagePanel;

    // Input/output GUI components
    private JTextField numThreadsTf;
    private JTextField executionTimeTf;
    private JButton startButton;

    /**
     * An inner class that represents the portion of the GUI that displays the image.
     */
    private class ImagePanel extends JComponent {
        public ImagePanel() {
            setBackground( Color.black );
            setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        }

        @Override
        public void paintComponent( Graphics g ) {
            super.paintComponent(g);
            if( image != null ) image.draw(g);
        }
    }

    public FractalGui() {
        image = null;
        imagePanel = new ImagePanel();
        buildGui();
    }

    private void buildGui() {
        JFrame window = new JFrame("Mandelbrot Set");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        numThreadsTf = new JTextField(4);
        numThreadsTf.setText("1");
        startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonListener());
        executionTimeTf = new JTextField( 10);
        executionTimeTf.setText("--");
        executionTimeTf.setEditable(false);

        JPanel hBox = new JPanel();
        hBox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        hBox.add( new JLabel("Number of threads: "));
        hBox.add(numThreadsTf);
        hBox.add(startButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bottomPanel.add(new JLabel("Time: "));
        bottomPanel.add(executionTimeTf);

        window.add(bottomPanel, BorderLayout.SOUTH);
        window.add(hBox,BorderLayout.NORTH);
        window.add(imagePanel, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);
    }

    private class StartButtonListener implements ActionListener {

        /**
         * Creates and starts the Mandelbrot generator.
         *
         * @param e unused
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            FractalGenerator generator = new FractalGenerator(IMAGE_WIDTH, IMAGE_HEIGHT);
            generator.addListener(FractalGui.this);
            image = generator.getImage();

            // Disable the input elements while computing
            startButton.setEnabled(false);
            numThreadsTf.setEditable(false);

            // Get the number of threads
            int numThreads = Integer.parseInt(numThreadsTf.getText());

            // Start the generator
            generator.generate(numThreads);
        }
    }

    @Override
    public void imageChanged() {
        imagePanel.repaint();
    }

    @Override
    public void imageComplete(double time) {
        // Enable the input elements
        startButton.setEnabled(true);
        numThreadsTf.setEditable(true);

        // Repaint the screen.  This causes a call to repaint on the event thread.
        imagePanel.repaint();

        // Display elapsed time
        executionTimeTf.setText(String.format("%.3f s", time));
    }
}
