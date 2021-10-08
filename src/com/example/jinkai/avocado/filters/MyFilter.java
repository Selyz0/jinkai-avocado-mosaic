package src.com.example.jinkai.avocado.filters;

import java.awt.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import javax.swing.ImageIcon;

public class MyFilter {
    public static ImageIcon blur(ImageIcon img, int x0, int y0, int x1, int y1){
        Image buf = img.getImage();
        System.out.println("Blur here");
        return img;
    }

    public static void blurTest(){
        Mat src = Imgcodecs.imread("house2.jpg");
        Mat dst = new Mat();
        Imgproc.boxFilter(src,dst,50,new Size(30,30));
        Imgcodecs.imwrite("test.jpg",dst);
    }
}
