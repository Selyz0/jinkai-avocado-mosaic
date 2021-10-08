package src.com.example.jinkai.avocado.filters;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

public class Blur {
    public static void applyBlur(){
        Mat src = Imgcodecs.imread("house2.jpg");
        Mat dst = new Mat();
        Imgproc.boxFilter(src,dst,50,new Size(30,30));
        Imgcodecs.imwrite("test.jpg",dst);
    }
}