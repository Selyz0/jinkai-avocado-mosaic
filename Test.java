import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;


public class Test{
    public static void main(String[] args){
        System.load("C:/new/.java/lib/opencv_java453.dll");
        new Test().test();
    }
    public void test(){
        Mat src = Imgcodecs.imread("house2.jpg");
        Mat dst = new Mat();
        Imgproc.boxFilter(src,dst,50,new Size(30,30));
        Imgcodecs.imwrite("test.jpg",dst);
    }
       
}