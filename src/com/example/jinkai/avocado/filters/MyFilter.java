package com.example.jinkai.avocado.filters;

import java.nio.ByteBuffer; 
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.core.MatOfByte;

import com.example.jinkai.avocado.models.MyImage;

public class MyFilter {
    public static Mat convertImageToMat(Image img) {
        // Image to BufferedImage
        BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

        Graphics g = bimg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        BufferedImage convertedImage = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(bimg, 0, 0, null);

        // BufferedImage to Mat

        byte[] data = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(convertedImage.getHeight(), convertedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    public static BufferedImage convertMatToBufferedImage(Mat mat){
		MatOfByte bytes = new MatOfByte();
		Imgcodecs.imencode(".png", mat, bytes);
		byte[] b = bytes.toArray();
		InputStream in = new ByteArrayInputStream(b);
		BufferedImage buf = null;
		try {
            System.out.println("here");
			buf = ImageIO.read(in);
            return buf;
        } catch(IOException e) {
			e.printStackTrace();
		}

        return null;
    }

    public static ImageIcon blur(ImageIcon img, int x0, int y0, int x1, int y1){
        Image buf = img.getImage();
        Mat src = convertImageToMat(buf);
        Mat dst = Blur.applyBlur(src, x0, y0, x1, y1);
        System.out.println("Blur here");
        ImageIcon icon = new ImageIcon(convertMatToBufferedImage(dst));

        return icon;
    }

    public static ImageIcon setWipe(ImageIcon img, int x0, int y0, int x1, int y1) {
        Image buf = img.getImage();
        Mat src = convertImageToMat(buf);
        Mat dst = Wipe.setWipe(src);
        ImageIcon icon = new ImageIcon(convertMatToBufferedImage(dst));

        return icon;
    }

    public static ImageIcon paintImage(ImageIcon img, int x0, int y0, int x1, int y1) {
        Image buf = img.getImage();
        Mat src = convertImageToMat(buf);
        Rect rect = new Rect(x0, y0, x1-x0, y1-y0);

        Mat paintImage = new MyImage("./assets/fujisan_1.jpg").loadImage();

        Mat dst = ImagePaint.applyFilter(src, rect, paintImage);
        ImageIcon icon = new ImageIcon(convertMatToBufferedImage(dst));
        return icon;
    }

    public static ImageIcon fill(ImageIcon img, int x0, int y0, int x1, int y1) {
        Image buf = img.getImage();
        Mat src = convertImageToMat(buf);
        Rect rect = new Rect(x0, y0, x1-x0, y1-y0);
        Scalar sc = new Scalar(100, 100, 100);
        Mat dst = SolidPaint.applyFilter(src, rect, sc);
        ImageIcon icon = new ImageIcon(convertMatToBufferedImage(dst));
        return icon;
    }
}
