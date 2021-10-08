package src.com.example.jinkai.avocado.views;

import src.com.example.jinkai.avocado.models.MyImage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.Container;
import java.io.*;

import java.util.Random;

public class CameraOut {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void showImage(JFrame frame, Mat image) {
        // convert the image to ByteArray
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", image, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        // remove the previous image from the window
        Container panel = frame.getContentPane();
        panel.removeAll();

        // show the input image
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage bufferedImage = ImageIO.read(in);
            panel.add(new JLabel(new ImageIcon(bufferedImage)));
            frame.pack();
            panel.revalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RunCameraOut {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        // prepare the window
        JFrame frame = new JFrame("Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            // generate a random sample image
            int randomValue = random.nextInt(255);
            Scalar color = new Scalar(randomValue, randomValue, randomValue);
            Mat image = new Mat(360, 640, CvType.CV_8UC3, color);

            // show the image in the window
            CameraOut.showImage(frame, image);

            // wait for 0.5 seconds
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}